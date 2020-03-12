package org.mulinlab.variantsampler.utils.sort;

import org.mulinlab.variantsampler.utils.node.AbstractNode;
import java.util.Comparator;

public final class DTCTSort implements Comparator<AbstractNode>  {
    public int compare(AbstractNode a, AbstractNode b)
    {
        return (a.dtct < b.dtct ? -1 : (a.dtct == b.dtct ? 0 : 1));
    }
}
