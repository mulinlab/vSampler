package org.mulinlab.variantsampler.utils.node;

import de.charite.compbio.jannovar.annotation.VariantEffect;
import org.mulinlab.varnote.utils.node.LocFeature;

public abstract class AbstractNode {
    public LocFeature locFeature;
    public String rsid;
    public int maf = -1;
    public double mafOrg = -1;
    public int dtct;

    public LocFeature getLocFeature() {
        return locFeature;
    }

    public void setLocFeature(LocFeature locFeature) {
        this.locFeature = locFeature;
    }

    public String getRsid() {
        return rsid;
    }

    public void setRsid(String rsid) {
        this.rsid = rsid;
    }


    public String getChr() {
        return locFeature.chr;
    }
    public int getPos() {
        return locFeature.beg;
    }

}
