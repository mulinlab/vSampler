package org.mulinlab.variantsampler.utils.enumset;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum GCDeviation {
    D1(0.01), D2(0.02), D3(0.03), D4(0.04), D5(0.05), D6(0.06), D7(0.07), D8(0.08), D9(0.09), D10(0.1);

    private final double value;
    GCDeviation(final double value) {
        this.value = value;
    }

    public double getVal() {
        return value;
    }
    public static GCDeviation getVal(final int index) {
        return  GCDeviation.values()[index];
    }

    public static String desc() {
        List<String> s = new ArrayList<>();
        for (GCDeviation d: GCDeviation.values()) {
            s.add(d.toString() + " means ±" + d.value);
        }

        return StringUtils.join(s, ", ");
    }
}
