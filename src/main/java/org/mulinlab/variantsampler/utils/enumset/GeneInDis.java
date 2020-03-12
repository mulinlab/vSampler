package org.mulinlab.variantsampler.utils.enumset;

import org.apache.commons.lang3.StringUtils;
import org.mulinlab.variantsampler.utils.GP;

import java.util.ArrayList;
import java.util.List;

public enum GeneInDis {
    KB100(0, "100KB"), KB200(1, "200KB"),
    KB300(2, "300KB"), KB400(3, "400KB"),
    KB500(4, "500KB"), KB600(5, "600KB"),
    KB700(6, "700KB"), KB800(7, "800KB"),
    KB900(8, "900KB"), KB1000(9, "1M");

    private final int idx;
    private final String title;

    GeneInDis(final int idx, final String title) {
        this.idx = idx;
        this.title = title;
    }

    public int getIdx() {
        return idx;
    }

    public String getTitle() {
        return title;
    }

    public static GeneInDis getVal(final int index) {
        return  GeneInDis.values()[index];
    }

    public static int getIdx(final GeneInDis geneInDis) {
        if (geneInDis == null) {
            return GP.DEFAULT_GENE_DIS.getIdx();
        } else {
            for (GeneInDis geneInDis1:GeneInDis.values()) {
                if(geneInDis1 == geneInDis) {
                    return geneInDis1.getIdx();
                }
            }
            return GP.DEFAULT_GENE_DIS.getIdx();
        }
    }

    public static String desc() {
        List<String> s = new ArrayList<>();
        for (GeneInDis d: GeneInDis.values()) {
            s.add(d.toString() + " means distance in " + d.getTitle());
        }

        return StringUtils.join(s, ", ");
    }
}
