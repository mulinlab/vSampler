package org.mulinlab.variantsampler.utils.enumset;

import org.apache.commons.lang3.StringUtils;
import org.mulinlab.variantsampler.utils.GP;

import java.util.ArrayList;
import java.util.List;

public enum LD {
    LD1(0, "ld>0.1"), LD2(1, "ld>0.2"), LD3(2, "ld>0.3"), LD4(3, "ld>0.4"),
    LD5(4, "ld>0.5"), LD6(5, "ld>0.6"), LD7(6, "ld>0.7"), LD8(7, "ld>0.8"),
    LD9(8, "ld>0.9");

    private final int idx;
    private final String title;

    LD(final int idx, final String title) {
        this.idx = idx;
        this.title = title;
    }

    public int getIdx() {
        return idx;
    }

    public String getTitle() {
        return title;
    }

    public static LD getVal(final int index) {
        return  LD.values()[index];
    }

    public static int getIdx(final LD ld) {
        if (ld == null) {
            return GP.DEFAULT_GENE_LD.getIdx();
        } else {
            for (LD ld1:LD.values()) {
                if(ld1 == ld) {
                    return ld1.getIdx();
                }
            }
            return GP.DEFAULT_GENE_LD.getIdx();
        }
    }

    public static String desc() {
        List<String> s = new ArrayList<>();
        for (LD d: LD.values()) {
            s.add(d.toString() + " means " + d.getTitle());
        }

        return StringUtils.join(s, ", ");
    }
}
