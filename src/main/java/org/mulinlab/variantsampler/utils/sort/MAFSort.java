package org.mulinlab.variantsampler.utils.sort;

import org.mulinlab.variantsampler.utils.node.AbstractNode;

import java.util.Comparator;

public final class MAFSort implements Comparator<AbstractNode>  {
    public int compare(AbstractNode a, AbstractNode b)
    {
        return (a.mafOrg < b.mafOrg ? -1 : (a.mafOrg == b.mafOrg ? 0 : 1));
    }
}
