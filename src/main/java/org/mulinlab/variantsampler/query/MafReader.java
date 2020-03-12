package org.mulinlab.variantsampler.query;

import htsjdk.samtools.seekablestream.SeekableStreamFactory;
import htsjdk.samtools.util.BlockCompressedInputStream;
import org.mulinlab.variantsampler.database.RoadmapAnnotation;
import org.mulinlab.variantsampler.database.TissueAnnotation;
import org.mulinlab.variantsampler.index.Index;
import org.mulinlab.variantsampler.index.IndexMaf;
import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.MafAddress;
import org.mulinlab.variantsampler.utils.node.Node;
import org.mulinlab.variantsampler.utils.QueryParam;
import org.mulinlab.varnote.constants.GlobalParameter;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.io.IOException;

public final class MafReader {
    private byte[] longbuf = new byte[8];

    private final BlockCompressedInputStream mafin;
    private boolean hasRefAlt = false;
    private final Index index;

    public MafReader(final String dbPath, final boolean hasRefAlt) throws IOException {
        this.mafin = new BlockCompressedInputStream(SeekableStreamFactory.getInstance().getBufferedStream(
                SeekableStreamFactory.getInstance().getStreamFor(IndexMaf.getMafPath(dbPath))));
        this.index = new Index(IndexMaf.getMafIdxPath(dbPath));
        this.hasRefAlt = hasRefAlt;
    }

    public void close() throws IOException {
        this.mafin.close();
    }

    public Node[] loadMAF(final String chr, final int maf, final QueryParam queryParam, final InputStack inputStack) throws IOException {
        if(maf < 0 || maf > 50) return null;

        MafAddress address = index.getMafAddress(chr, maf);

        mafin.seek(address.getMafAddress());
        int size = GlobalParameter.readInt(mafin);
        Node[] nodes = new Node[size];

        Node node;
        int beg, idx;
        byte[] b;
        String refalt;
        LocFeature locFeature;

        for (int i = 0; i < size; i++) {
            node = new Node();
            node.maf = maf;

            beg = GlobalParameter.readInt(mafin);
            locFeature = new LocFeature(beg, beg, chr);

            b = new byte[GlobalParameter.readInt(mafin)];
            mafin.read(b);
            refalt = new String(b);
            idx = refalt.indexOf(",");
            locFeature.ref = refalt.substring(0, idx);
            locFeature.alt = refalt.substring(idx+1);
            locFeature.end = beg + locFeature.ref.length();

            node.setLocFeature(locFeature);
            node.dtct = GlobalParameter.readInt(mafin);
            node.mafOrg = GP.readFloat(mafin);

            if(queryParam.excludeInput && inputStack.isNodeInQuery(node, hasRefAlt)) {
                node.isInQuery = true;
            }
            nodes[i] = node;
        }

        if(queryParam.geneDisIndex != GP.NO_GENE_DIS) {
            mafin.seek(address.getGeneDistanceAddress()[queryParam.geneDisIndex]);
            for (int i = 0; i < size; i++) {
                nodes[i].geneDis = GlobalParameter.readShort(mafin);
            }
        }

        if(queryParam.geneLDIndex != GP.NO_GENE_LD) {
            mafin.seek(address.getGeneLDAddress()[queryParam.geneLDIndex]);
            for (int i = 0; i < size; i++) {
                nodes[i].geneLD = GlobalParameter.readShort(mafin);
            }
        }

        if(queryParam.ldIndex != GP.NO_LD_BUDDIES) {
            mafin.seek(address.getLdBuddiesAddress()[queryParam.ldIndex]);
            for (int i = 0; i < size; i++) {
                nodes[i].ldBuddies = GlobalParameter.readShort(mafin);
            }
        }

        int len;
        if(queryParam.hasCellMarker) {
            mafin.seek(address.getRoadmapAnnoAddress()[queryParam.markerIndex]);
            for (int i = 0; i < size; i++) {
                len = GlobalParameter.read(mafin);
                if(len == 0) {
                    nodes[i].roadmap = false;
                } else {
                    long[] val = new long[len];
                    for (int j = 0; j < len; j++) {
                        val[j] = GlobalParameter.readLong(mafin, longbuf);
                    }
                    nodes[i].roadmap = RoadmapAnnotation.getValOfIndex(val, queryParam.cellIdx);
                }
            }
        }

        if(queryParam.variantTypeMatch) {
            mafin.seek(address.getCatAddress());
            for (int i = 0; i < size; i++) {
                nodes[i].cat = GlobalParameter.read(mafin);
            }
        }

        if(queryParam.tissueIdx != GP.NO_TISSUE) {
            mafin.seek(address.getTissueAddress());

            for (int i = 0; i < size; i++) {
                len = GlobalParameter.read(mafin);
                if(len == 0) {
                    nodes[i].tissue = false;
                } else {
                    nodes[i].tissue = TissueAnnotation.getValOfIndex(new long[]{GlobalParameter.readLong(mafin, longbuf)}, queryParam.tissueIdx);
                }
            }
        }

        if(queryParam.getGcRange() != null) {
            mafin.seek(address.getGcAddress()[queryParam.gcIdx]);
            for (int i = 0; i < size; i++) {
                nodes[i].gc = GlobalParameter.read(mafin);
            }
        }

        return nodes;
    }
}
