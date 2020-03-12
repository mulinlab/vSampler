package org.mulinlab.variantsampler.utils.enumset;

import org.mulinlab.variantsampler.utils.GP;

public enum Marker {
    DNase(0), H3K4me1(1), H3K4me3(2), H3K36me3(3), H3K27me3(4), H3K9me3(5);

    private final int idx;

    Marker(final int idx) {
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }
    public static Marker getVal(final int index) {
        return  Marker.values()[index];
    }

    public static int getIdx(final Marker marker) {
        if (marker == null) {
            return GP.NO_MARKER;
        } else {
            for (Marker marker1:Marker.values()) {
                if(marker1 == marker) {
                    return marker1.getIdx();
                }
            }
            return GP.NO_MARKER;
        }
    }
}
