package org.mulinlab.variantsampler.utils.sort;

import org.mulinlab.variantsampler.utils.node.AbstractNode;
import java.util.Comparator;

public final class PosSort implements Comparator<AbstractNode> {
    public int compare(AbstractNode a, AbstractNode b)
    {
        return (a.getPos() < b.getPos() ? -1 : (a.getPos() == b.getPos() ? 0 : 1));
    }
}
