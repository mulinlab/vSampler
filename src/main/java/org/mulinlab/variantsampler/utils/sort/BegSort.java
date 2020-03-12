package org.mulinlab.variantsampler.utils.sort;


import org.mulinlab.varnote.utils.node.LocFeature;
import java.util.Comparator;

public final class BegSort implements Comparator<LocFeature> {

    public int compare(LocFeature a, LocFeature b)
    {
        return (a.beg < b.beg ? -1 : (a.beg == b.beg ? 0 : 1));
    }
}
