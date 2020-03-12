package org.mulinlab.variantsampler.utils.enumset;

import org.apache.commons.lang3.StringUtils;
import org.mulinlab.variantsampler.utils.GP;

import java.util.ArrayList;
import java.util.List;

public enum GCType {
    BP100(0, "100bp"), BP200(1, "200bp"), BP300(2, "300bp"), BP400(3, "400bp"), BP500(4, "500bp");

    private final int idx;
    private final String title;

    GCType(final int idx, final String title) {
        this.idx = idx;
        this.title = title;
    }

    public int getIdx() {
        return idx;
    }

    public String getTitle() {
        return title;
    }

    public static GCType getVal(final int index) {
        return  GCType.values()[index];
    }

    public static String desc() {
        List<String> s = new ArrayList<>();
        for (GCType d: GCType.values()) {
            s.add(d.toString() + " means ±" + d.title);
        }

        return StringUtils.join(s, ", ");
    }

    public static int getIdx(final GCType gcType) {
        if (gcType == null) {
            return GP.NO_GC;
        } else {
            for (GCType gcType1:GCType.values()) {
                if(gcType == gcType1) {
                    return gcType1.idx;
                }
            }
            return GP.NO_GC;
        }
    }
}
