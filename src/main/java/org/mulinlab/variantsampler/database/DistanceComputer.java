package org.mulinlab.variantsampler.database;

import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.Pair;
import org.mulinlab.varnote.operations.readers.db.VannoMixReader;
import org.mulinlab.varnote.operations.readers.db.VannoReader;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.io.IOException;
import java.util.List;


public final class DistanceComputer {
    private final int RANGE_STEP = GP.RANGE_STEP;
    private int[] density;

    private final VannoReader genecodeReader;

    public DistanceComputer(final String geneFile) throws IOException {
        this.genecodeReader = new VannoMixReader(geneFile);
        density = new int[GP.GENE_DIS_SIZE];
        for (int i = 0; i < density.length; i++) {
            density[i] = (i+1) * 100 * 1000;
        }
    }

    public void close() {
        genecodeReader.close();
    }

    public Integer[] computeGeneInLD(final String chr, final Pair<Integer, Integer>[] range) throws IOException {
        Integer[] results = new Integer[range.length];

        for (int i = range.length - 1; i >= 0; i--) {
            if(range[i].getKey() == -1 || range[i].getValue() == -1) {
                results[i] = 0;
            } else {
                results[i] = computeDensity(chr, range[i].getKey(), range[i].getValue());
            }
        }

        return results;
    }

    public Integer[] computeGeneInDistance(final String chr, final int pos) throws IOException {
        Integer[] results = new Integer[density.length];

        int begin, end;
        for (int i = density.length - 1; i >= 0; i--) {
            begin = (pos - density[i]) > 0 ? (pos - density[i]) : 0;
            end = pos + density[i];

            results[i] = computeDensity(chr, begin, end);
        }

        return results;
    }

    public int computeDensity(final String chr, final int start, final int end) throws IOException {
        genecodeReader.query(new LocFeature(start, end, chr));
        return genecodeReader.getResults().size();
    }


    public Integer computeDTCT(final String chr, final int pos) throws IOException {
        int range = RANGE_STEP;
        List<String> results;
        int begin, end;

        while (true) {
            begin = (pos - range) > 0 ? (pos - range) : 0;
            end = pos + range;

            genecodeReader.query(new LocFeature(begin, end, chr));
            results = genecodeReader.getResults();

            if(results.size() > 0) {
                break;
            } else {
                range = range + RANGE_STEP;
            }
        }

        Pair<Integer, String> pair = findClosest(pos, results);
//        System.out.println(pair.getValue());

        return pair.getKey();
//        System.out.println(chr + "_" + pos + "\t" + r.getKey() + "\t" + r.getValue());
    }

    public Pair<Integer, String> findClosest(final int pos, final List<String> results) {
        int minDis = -1, dis, disin = -1, begin, end;
        String minR = null;

        String[] token;
        for (String s: results) {
            token = s.split(DatabaseBuilder.TAB);
            if(token[6].equals("+")) {
                dis = Math.abs(Integer.parseInt(token[3]) - pos);
            } else {
                dis = Math.abs(Integer.parseInt(token[4]) - pos);
            }

            if( (pos >= Integer.parseInt(token[3]) && (pos <= Integer.parseInt(token[4])))) {
                if(disin == -1 || disin > dis) disin = dis;
                minR = s;
            }

            if(minDis == -1 || minDis > dis) {
                minDis = dis;
                if(disin == -1) minR = s;
            }
        }

        return new Pair(disin == -1 ? minDis : disin, minR);
    }
}
