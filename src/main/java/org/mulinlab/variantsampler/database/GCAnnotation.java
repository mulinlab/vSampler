package org.mulinlab.variantsampler.database;

import org.mulinlab.varnote.operations.decode.BEDLocCodec;
import org.mulinlab.varnote.operations.readers.db.VannoMixReader;
import org.mulinlab.varnote.operations.readers.db.VannoReader;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.io.IOException;
import java.util.List;

public final class GCAnnotation {
    private final VannoReader gcReader;
    private BEDLocCodec bedLocCodec;
    private int[] gcwindow = new int[]{100, 200, 300, 400, 500};

    public GCAnnotation(final String gcFile) throws IOException {
        gcReader = new VannoMixReader(gcFile);
        this.bedLocCodec = new BEDLocCodec(true);
    }

    public void close() {
        gcReader.close();
    }

    public int[] getGCContent(final String chr, final int pos) throws IOException {
        int[] r = new int[gcwindow.length];
        for (int i = 0; i < gcwindow.length; i++) {
            r[i] = getGCContent(chr, pos, gcwindow[i]);
        }

        return r;
    }

    public int getGCContent(final String chr, final int pos, final int window) throws IOException {

        int beg = pos - window, end = pos + window;
        gcReader.query(new LocFeature(beg, end, chr));
        List<String> results = gcReader.getResults();

        LocFeature locFeature;
        double gcContent = 0;

        int  c = 0, ccount = 0;
        if(results.size() > 0) {
            for (String s:results) {
                locFeature = bedLocCodec.decode(s);
                if(locFeature.beg < beg && beg < locFeature.end) {
                    c = locFeature.end - beg ;
                } else if(locFeature.beg < end && end < locFeature.end) {
                    c = end - locFeature.beg ;
                } else {
                    c = locFeature.end - locFeature.beg;
                }
                ccount += c;
                gcContent += Double.parseDouble(locFeature.parts[3]) * c;
            }

            return (int)(gcContent/ccount);
        }

        return -1;
    }

}
