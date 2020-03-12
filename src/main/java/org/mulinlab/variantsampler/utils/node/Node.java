package org.mulinlab.variantsampler.utils.node;

import org.mulinlab.variantsampler.database.RoadmapAnnotation;
import org.mulinlab.variantsampler.database.TissueAnnotation;
import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.QueryParam;
import org.mulinlab.variantsampler.utils.enumset.VariantRegion;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.util.List;


public final class Node extends AbstractNode {

    public int hasAnno = 1;
    public int geneDis;
    public int geneLD;
    public int ldBuddies;
    public boolean roadmap;
    public byte cat;
    public boolean tissue;
    public int gc;

    public boolean isIndel = false;
    public boolean isInQuery = false;
    public int poolSize = 0;
    public List<Node> result;

    public Node() {
        this.locFeature = null;
    }

    public Node(final LocFeature locFeature) {
        this.locFeature = locFeature;
        this.hasAnno = 0;
    }

    public Node(final LocFeature locFeature, final QueryParam queryParam) {
        this.locFeature = locFeature.clone();
        this.isIndel = (this.locFeature.ref.length() != this.locFeature.alt.length());

        setOthers(locFeature.origStr.split("\t"), queryParam);
    }

    public void setLocFeature(LocFeature locFeature) {
        super.setLocFeature(locFeature);
        this.isIndel = (this.locFeature.ref.length() != this.locFeature.alt.length());
    }

    public void setOthers(String[] tokens, QueryParam queryParam) {
        this.mafOrg = Double.parseDouble(tokens[GP.MAF_IDX]);
        maf = (int)(this.mafOrg * 100);
        dtct = Integer.parseInt(tokens[GP.DIS_IDX]);

        if(queryParam.geneDisIndex != GP.NO_GENE_DIS) {
            geneDis = Integer.parseInt(tokens[GP.GENE_DIS_START + queryParam.geneDisIndex]);
        }

        if(queryParam.geneLDIndex != GP.NO_GENE_LD) {
            geneLD = Integer.parseInt(tokens[GP.GENE_LD_START + queryParam.geneLDIndex]);
        }

        if(queryParam.ldIndex != GP.NO_LD_BUDDIES) {
            ldBuddies = Integer.parseInt(tokens[GP.LD_BUDDIES_START + queryParam.ldIndex]);
        }

        if(queryParam.hasCellMarker) {
            if(tokens[ GP.ROADMAP_START + queryParam.markerIndex].trim().length() > 0) {
                roadmap = RoadmapAnnotation.getValOfIndex(GP.string2LongArr(tokens[queryParam.markerIndex + GP.ROADMAP_START].split(",")), queryParam.cellIdx);
            } else {
                roadmap = false;
            }
        }

        if(queryParam.variantTypeMatch) {
            cat = (byte)Integer.parseInt(tokens[GP.CAT_START]);
        }

        if(queryParam.tissueIdx != GP.NO_TISSUE) {
            if(tokens[GP.TISSUE_START].trim().length() > 0) {
                tissue = TissueAnnotation.getValOfIndex(new long[]{Long.parseLong(tokens[GP.TISSUE_START])}, queryParam.tissueIdx);
            } else {
                tissue = false;
            }
        }

        if(queryParam.gcIdx != GP.NO_GC) {
            gc = GP.string2IntegerArr(tokens[GP.GC_START].split(","))[queryParam.gcIdx];
        }

        locFeature.parts = null;
        locFeature.origStr = null;
    }

    public String briefQuery(boolean isRefAlt) {
        if(isRefAlt) {
            return String.format("%s:%d:%s:%s", locFeature.chr, locFeature.beg + 1, locFeature.ref, locFeature.alt);
        } else {
            return String.format("%s:%d", locFeature.chr, locFeature.beg + 1);
        }
    }

    public String briefAnno() {
        if(hasAnno == 0) {
            return "-";
        } else {
            return String.format("%s:%d:%s:%s", locFeature.chr, locFeature.beg + 1, locFeature.ref, locFeature.alt);
        }
    }

    public String toString(final QueryParam queryParam) {
        String s;
        if(maf < 0) {
            s = String.format("%s\t%d\t%s\t%s\t%s", locFeature.chr, locFeature.beg + 1, locFeature.ref == null ? "-" : locFeature.ref,
                    locFeature.alt == null ? "-" : locFeature.alt, "-");
        } else {
            s= String.format("%s\t%d\t%s\t%s\t%f", locFeature.chr, locFeature.beg + 1, locFeature.ref, locFeature.alt, mafOrg);
        }

        if( queryParam.getDisRange() != null) s += "\t" + ((hasAnno == 0) ? "-" : dtct);
        if( queryParam.geneDisIndex != GP.NO_GENE_DIS) s += "\t" + ((hasAnno == 0)? "-" : geneDis);
        if( queryParam.geneLDIndex != GP.NO_GENE_LD) s += "\t" + ((hasAnno == 0) ? "-" : geneLD);
        if( queryParam.ldIndex != GP.NO_LD_BUDDIES) s += "\t" + ((hasAnno == 0) ? "-" : ldBuddies);
        if(queryParam.gcIdx != GP.NO_GC) {
            s += "\t" + ((hasAnno == 0) ? "-" : gc + "%");
        }
        if(queryParam.variantTypeMatch) {
            s += "\t" + ((hasAnno == 0) ? "-" : VariantRegion.getVal(cat));
        }
        if( queryParam.hasCellMarker) s += "\t" + ((hasAnno == 0) ? "-" : roadmap);
        if(queryParam.tissueIdx != GP.NO_TISSUE) {
            s += "\t" + ((hasAnno == 0) ? "-" : tissue);
        }
        return s;
    }


}
