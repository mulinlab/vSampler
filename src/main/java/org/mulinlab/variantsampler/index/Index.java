package org.mulinlab.variantsampler.index;

import htsjdk.samtools.util.BlockCompressedInputStream;
import org.mulinlab.variantsampler.utils.MafAddress;
import org.mulinlab.varnote.constants.GlobalParameter;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Index {

    private final String indexPath;
    private Map<Integer, Map<Integer, MafAddress>> chrMap;

    private byte[] longbuf = new byte[8];

    public Index(final String filePath) {
        this.indexPath = filePath;
        this.chrMap = new HashMap<>();

        try {
            readIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readIndex() throws IOException {
        BlockCompressedInputStream in = new BlockCompressedInputStream(new File(this.indexPath));
        final int size = GlobalParameter.readInt(in);

        int listSize, gdSize, chr;
        Map<Integer, MafAddress> mafAddrsMap;
        MafAddress address;

        for (int i = 0; i < size; i++) {
            chr = GlobalParameter.readInt(in);
            listSize = GlobalParameter.readInt(in);
            mafAddrsMap = new HashMap<>();

            for (int j = 0; j < listSize; j++) {
                address = new MafAddress(GlobalParameter.readInt(in));
                address.setMafAddress(GlobalParameter.readLong(in, longbuf));

                gdSize = GlobalParameter.readInt(in);
                for (int k = 0; k < gdSize; k++) {
                    address.setGeneDistanceAddress(GlobalParameter.readLong(in, longbuf), k);
                }

                gdSize = GlobalParameter.readInt(in);
                for (int k = 0; k < gdSize; k++) {
                    address.setGeneLDAddress(GlobalParameter.readLong(in, longbuf), k);
                }

                gdSize = GlobalParameter.readInt(in);
                for (int k = 0; k < gdSize; k++) {
                    address.setLdBuddiesAddress(GlobalParameter.readLong(in, longbuf), k);
                }

                gdSize = GlobalParameter.readInt(in);
                for (int k = 0; k < gdSize; k++) {
                    address.setRoadmapAnnoAddress(GlobalParameter.readLong(in, longbuf), k);
                }

                address.setCatAddress(GlobalParameter.readLong(in, longbuf));
                address.setTissueAddress(GlobalParameter.readLong(in, longbuf));

                gdSize = GlobalParameter.readInt(in);
                for (int k = 0; k < gdSize; k++) {
                    address.setGcAddress(GlobalParameter.readLong(in, longbuf), k);
                }
                mafAddrsMap.put(address.getMaf(), address);
            }

            chrMap.put(chr, mafAddrsMap);
        }
        in.close();
    }

    public MafAddress getMafAddress(final String chr, final int maf) {
        return chrMap.get(chr2id(chr)).get(maf);
    }

    public int chr2id(final String chr) {
        return Integer.parseInt(chr.toUpperCase().replace("CHR", ""));
    }
}
