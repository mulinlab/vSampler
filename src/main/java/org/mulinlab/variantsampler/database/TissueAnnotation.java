package org.mulinlab.variantsampler.database;

import org.mulinlab.varnote.operations.decode.TABLocCodec;
import org.mulinlab.varnote.operations.readers.db.VannoMixReader;
import org.mulinlab.varnote.operations.readers.db.VannoReader;
import org.mulinlab.varnote.utils.format.Format;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.io.IOException;
import java.util.*;

public final class TissueAnnotation {
    private final String[] tissueList;
    private final VannoReader tissueReader;
    private TABLocCodec tabLocCodec;

    public TissueAnnotation(final String[] tissueList, final String tissueFile) throws IOException {
        this.tissueList = tissueList;
        tissueReader = new VannoMixReader(tissueFile);

        Format format = Format.newTAB();
        format.sequenceColumn = 1;
        format.startPositionColumn = 2;
        format.endPositionColumn = 2;
        format.refPositionColumn = 3;
        format.altPositionColumn = 4;
        this.tabLocCodec = new TABLocCodec(format, true);
    }

    public void close() {
        tissueReader.close();
    }

    public BitSet getAnno(final String chr, final int pos, final String ref, final String alt) throws IOException {
        BitSet bits1 = new BitSet(tissueList.length);

        tissueReader.query(new LocFeature(pos-1, pos, chr, ref, alt));
        List<String> results = tissueReader.getResults();

        LocFeature locFeature;
        Map<String, Boolean> hitMap = new HashMap<>();
        if(results.size() > 0) {
            for (String s:results) {
                locFeature = tabLocCodec.decode(s);
                if(((locFeature.beg + 1) == pos) && locFeature.ref.equals(ref) && locFeature.alt.equals(alt)) {
                    hitMap.put(locFeature.parts[16], true);
                }
            }
        }

        for (int i = 0; i < tissueList.length; i++) {
            if(hitMap.get(tissueList[i]) != null) {
                bits1.set(i);
            }
        }
        return bits1;
    }

    public static boolean getValOfIndex(final long[] val, final int idx) {
        BitSet bit = BitSet.valueOf(val);
        return bit.get(idx);
    }
}
