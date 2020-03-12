package org.mulinlab.variantsampler.database;


import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.Pair;
import org.mulinlab.varnote.operations.index.Variant;
import org.mulinlab.varnote.operations.readers.gt1000g.VannoGTReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class LDComputer {

    private Integer[] ldBuddies;
    private Pair<Integer, Integer>[] posRange;

    private Map<String, Integer> variantMap;
    private List<Variant> variants;

    private int beg = 0, end = 0;
    private String currentChr = "";
    private int ldDistance = 100*1000, step = 1000 * 1000;
    private double cufoff = 0.1;

    VannoGTReader gtReader;

    public LDComputer(final String gtDatabase) throws IOException {
        this.gtReader = new VannoGTReader(gtDatabase);
        this.gtReader.setLdDistance(ldDistance/1000);

        ldBuddies = new Integer[GP.LD_BUDDIES_SIZE];
        posRange = new Pair[GP.GENE_LD_SIZE];

        for (int i = 0; i < ldBuddies.length; i++) {
            ldBuddies[i] = new Integer(0);
        }

        for (int i = 0; i < posRange.length; i++) {
            posRange[i] = new Pair(-1, -1);
        }
    }

    public void close() {
        try {
            gtReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pair<Integer[], Pair<Integer, Integer>[]> compute(final String chr, final int pos, final String ref, final String alt) throws IOException {
        if(!chr.equals(currentChr)) {
            beg = 0; end = 0;
            currentChr = chr;

            variantMap = new HashMap<>();
        }

        try{
            if(pos > (end - ldDistance)) {
                variantMap = new HashMap<>();
                beg = (pos - ldDistance) > 0 ? (pos - ldDistance) : 0;
                end = pos + step;

                variants = gtReader.findVariants(chr, beg, end);
                for (int i = 0; i < variants.size(); i++) {
                    variants.get(i).setData(gtReader.convertToDouble(variants.get(i)));
                    variantMap.put(variants.get(i).getPos() + "_" + new String(variants.get(i).getRefByte()) + "_" + new String(variants.get(i).getAltByte()), i);
                }
            }

            Integer q = variantMap.get(pos + "_" + ref + "_" + alt);
            if(q != null) {
                Variant query = variants.get(q);
                javafx.util.Pair<Double, Double> ld;
                resetLDBuddies();

                for (int i = 0; i < variants.size(); i++) {
                    if(i != q && (variants.get(i).getPos() > (query.getPos() - ldDistance) && variants.get(i).getPos() < (query.getPos() + ldDistance))) {
                        ld = gtReader.computeLdLinear(variants.get(i).getData(), query.getData());
                        if(ld.getKey() * ld.getKey() > cufoff) {
                            addLDBuddies(ld.getKey() * ld.getKey(), variants.get(i));
                        }
                    }
                }

                return new Pair<Integer[], Pair<Integer, Integer>[]>(ldBuddies, posRange);
            } else {
                System.out.println(chr + ":" + pos + " " + ref + "|" + alt);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void addLDBuddies(final double r2, final Variant variant) {
        double index = r2*10 - 1;
        for (int i = 8; i >= 0; i--) {
            if(index > i) {
                index = i;
                break;
            }
        }
        if(index >= 0) {
            if(index > 8) index = 8;
            for (int i = 0; i <= index; i++) {
                ldBuddies[i]++;

                if(variant.getPos() < posRange[i].getKey() || posRange[i].getKey() == -1) {
                    posRange[i].setKey(variant.getPos());
                }
                if(variant.getPos() > posRange[i].getValue()) {
                    posRange[i].setValue(variant.getPos());
                }
            }
        }
    }

    private void resetLDBuddies() {
        for (int i = 0; i < ldBuddies.length; i++) {
            ldBuddies[i] = 0;
        }

        for (int i = 0; i < posRange.length; i++) {
            posRange[i].setKey(-1);
            posRange[i].setValue(-1);
        }
    }

    public void setLdDistance(int ldDistance) {
        this.ldDistance = ldDistance * 1000;
    }
}
