package org.mulinlab.variantsampler.index;

import htsjdk.samtools.util.BlockCompressedInputStream;
import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.node.DBNode;
import org.mulinlab.variantsampler.utils.MafAddress;
import org.mulinlab.variantsampler.utils.Pair;
import org.mulinlab.variantsampler.utils.sort.MAFSort;
import org.mulinlab.varnote.exceptions.InvalidArgumentException;
import org.mulinlab.varnote.operations.decode.TABLocCodec;
import org.mulinlab.varnote.operations.decode.VCFLocCodec;
import org.mulinlab.varnote.operations.readers.db.VannoMixReader;
import org.mulinlab.varnote.operations.readers.db.VannoReader;
import org.mulinlab.varnote.utils.database.index.TbiIndex;
import org.mulinlab.varnote.utils.format.Format;
import org.mulinlab.varnote.utils.gz.MyBlockCompressedOutputStream;
import org.mulinlab.varnote.utils.gz.MyEndianOutputStream;
import org.mulinlab.varnote.utils.node.LocFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class IndexMaf {
    public final static String MAF = ".maf";
    public final static String INDEX_EXT = ".maf.idx";

    private static byte[] longbuf = new byte[8];

    private final VannoReader srcReader;
    private final TbiIndex index;
    private final BlockCompressedInputStream in;
    private final MyEndianOutputStream out;
    private final MyEndianOutputStream outIdx;

    private List<DBNode> mafList;
    private Map<Integer, MafAddress> mafAddrsMap;
    private Map<Integer, Map<Integer, MafAddress>> chrMap;
    private final TABLocCodec tabLocCodec;

    public static String getMafPath(final String filePath) {
        return filePath.replace(".gz", MAF);
    }

    public static String getMafIdxPath(final String filePath) {
        return filePath.replace(".gz", INDEX_EXT);
    }

    public IndexMaf(final String filePath) throws IOException {
        this.srcReader = new VannoMixReader(filePath);
        this.index = new TbiIndex(filePath + ".tbi");
        in = new BlockCompressedInputStream(new File(filePath));
        out = new MyEndianOutputStream(new MyBlockCompressedOutputStream(getMafPath(filePath)));
        outIdx = new MyEndianOutputStream(new MyBlockCompressedOutputStream(getMafIdxPath(filePath)));

        this.tabLocCodec = GP.getDefaultDecode( true);

        chrMap = new HashMap<>();
        for (int i=GP.FROM_CHROM; i<=GP.TO_CHROM; i++) {

            mafAddrsMap = new HashMap<>();
            System.out.println("chr=" + i);
            for (int j = 0; j <= GP.MAX_MAF; j++) {
                System.out.println("maf=" + j);
                getMafList(String.valueOf(i), j);
                if (mafList.size() > 0) {
                    writeIndexForMaf(j);
                }
            }
            chrMap.put(i, mafAddrsMap);
        }

        writeEnd();

        in.close();
        srcReader.close();
        out.close();
        outIdx.close();
    }

    private void writeEnd() throws IOException {
        Map<Integer, MafAddress> mafAddrsMap;

        outIdx.writeInt(chrMap.keySet().size());
        for (Integer chr:chrMap.keySet()) {
            mafAddrsMap = chrMap.get(chr);
            outIdx.writeInt(chr);

            outIdx.writeInt(mafAddrsMap.keySet().size());
            for (MafAddress address: mafAddrsMap.values()) {
                outIdx.writeInt(address.getMaf());
                outIdx.writeLong(address.getMafAddress());

                outIdx.writeInt(address.getGeneDistanceAddress().length);
                for (int j = 0; j < address.getGeneDistanceAddress().length; j++) {
                    outIdx.writeLong(address.getGeneDistanceAddress()[j]);
                }

                outIdx.writeInt(address.getGeneLDAddress().length);
                for (int j = 0; j < address.getGeneLDAddress().length; j++) {
                    outIdx.writeLong(address.getGeneLDAddress()[j]);
                }

                outIdx.writeInt(address.getLdBuddiesAddress().length);
                for (int j = 0; j < address.getLdBuddiesAddress().length; j++) {
                    outIdx.writeLong(address.getLdBuddiesAddress()[j]);
                }

                outIdx.writeInt(address.getRoadmapAnnoAddress().length);
                for (int j = 0; j < address.getRoadmapAnnoAddress().length; j++) {
                    outIdx.writeLong(address.getRoadmapAnnoAddress()[j]);
                }

                outIdx.writeLong(address.getCatAddress());
                outIdx.writeLong(address.getTissueAddress());

                outIdx.writeInt(address.getGcAddress().length);
                for (int j = 0; j < address.getGcAddress().length; j++) {
                    outIdx.writeLong(address.getGcAddress()[j]);
                }
            }
        }
    }

    private void writeIndexForMaf(final int maf) throws IOException {
        MafAddress mafAddress = new MafAddress(maf);
        mafAddress.setMafAddress(out.getOut().getFilePointer());

        out.writeInt(mafList.size());
        String refalt;
        for (DBNode dbNode: mafList) {
            out.writeInt(dbNode.getLocFeature().beg);
            refalt = dbNode.getLocFeature().ref + "," + dbNode.getLocFeature().alt;
            out.writeInt(refalt.length());
            out.writeBytes(refalt);

            out.writeInt(dbNode.dtct);
            out.writeFloat((float) dbNode.mafOrg);
        }

        for (int i=0; i<GP.GENE_DIS_SIZE; i++) {
            mafAddress.setGeneDistanceAddress(out.getOut().getFilePointer(), i);
            for (DBNode dbNode: mafList) {
                try {
                    if(dbNode.getGeneDensity()[i] >= GP.MAX_SHORT_UNSIGNED) throw new InvalidArgumentException("out of boundary " + dbNode.getLocFeature());
                    out.writeShort(dbNode.getGeneDensity()[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i=0; i<GP.GENE_LD_SIZE; i++) {
            mafAddress.setGeneLDAddress(out.getOut().getFilePointer(), i);

            for (DBNode dbNode: mafList) {
                if(dbNode.getGeneInLD()[i] >= GP.MAX_SHORT_UNSIGNED) throw new InvalidArgumentException("out of boundary " + dbNode.getLocFeature());
                out.writeShort(dbNode.getGeneInLD()[i]);
            }
        }

        for (int i=0; i<GP.LD_BUDDIES_SIZE; i++) {
            mafAddress.setLdBuddiesAddress(out.getOut().getFilePointer(), i);

            for (DBNode dbNode: mafList) {
                if(dbNode.getLdBuddies()[i] >= GP.MAX_SHORT_UNSIGNED) throw new InvalidArgumentException("out of boundary " + dbNode.getLocFeature());
                out.writeShort(dbNode.getLdBuddies()[i]);
            }
        }

        long[] cellMark;
        for (int i=0; i<GP.ROADMAP_SIZE; i++) {
            mafAddress.setLdBuddiesAddress(out.getOut().getFilePointer(), i);
            for (DBNode dbNode: mafList) {
                cellMark = dbNode.getCellMarks()[i];
                if(cellMark != null && cellMark.length > 0) {
                    out.writeByte(cellMark.length);
                    for (long cell: cellMark) {
                        out.writeLong(cell);
                    }
                } else {
                    out.writeByte(0);
                }
            }
        }

        mafAddress.setCatAddress(out.getOut().getFilePointer());
        for (DBNode dbNode: mafList) {
            out.writeByte(dbNode.getCategory());
        }

        mafAddress.setTissueAddress(out.getOut().getFilePointer());
        for (DBNode dbNode: mafList) {
            if(dbNode.getTissueAnno() == 0) {
                out.writeByte(0);
            } else {
                out.writeByte(1);
                out.writeLong(dbNode.getTissueAnno());
            }
        }

        for (int i = 0; i < GP.GC_SIZE; i++) {
            mafAddress.setGcAddress(out.getOut().getFilePointer(), i);
            for (DBNode dbNode: mafList) {
                out.writeByte(dbNode.getGc()[i]);
            }
        }

        mafAddrsMap.put(maf, mafAddress);
    }

    private void getMafList(final String chr, final int maf) throws IOException {
        in.seek(index.getMinOffForChr(index.chr2tid(chr)));
        mafList = new ArrayList<>();

        String line;
        DBNode dbNode;
        LocFeature locFeature;

        long filePointer = in.getFilePointer();
        while ((line = in.readLine()) != null) {
            locFeature = tabLocCodec.decode(line);
            dbNode = new DBNode(locFeature);

            if(!dbNode.getLocFeature().chr.equals(chr)) break;
            if((dbNode.mafOrg > GP.MAF_FILTER) && (dbNode.maf == maf)) {
                dbNode.setAddress(filePointer);
                dbNode.decodeOthers();
                mafList.add(dbNode);
            }
            filePointer = in.getFilePointer();
        }

        Collections.sort(mafList, new MAFSort());
    }
}
