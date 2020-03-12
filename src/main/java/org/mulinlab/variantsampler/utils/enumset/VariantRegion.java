package org.mulinlab.variantsampler.utils.enumset;

public enum VariantRegion {
    EXONIC_SPLICING(0, "exonic + splicing altering"),
    NONCODING(1, "noncoding"),
    OTHER(2, "other");


    private final int idx;
    private final String name;

    VariantRegion(final int idx, final String name) {
        this.idx = idx;
        this.name = name;
    }

    public int getIdx() {
        return idx;
    }

    public String getName() {
        return name;
    }

    public static VariantRegion getVal(final int index) {
        return  VariantRegion.values()[index];
    }
}
