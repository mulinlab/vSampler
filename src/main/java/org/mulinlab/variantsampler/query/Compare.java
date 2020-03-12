package org.mulinlab.variantsampler.query;

import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.QueryParam;
import org.mulinlab.variantsampler.utils.node.Node;

public class Compare {
    public static final int LESS = 1;
    public static final int IN = 2;
    public static final int GREATER = 3;

    private double minMAF;
    private double maxMAF;

    private int minGC;
    private int maxGC;

    private int minDis;
    private int maxDis;

    private int minGene;
    private int maxGene;

    private int minLD;
    private int maxLD;

    public Compare() {
    }

    public void setQuery(Node q, QueryParam queryParam) {
        minMAF = queryParam.getMafOriRangeMin(q.mafOrg);
        maxMAF = queryParam.getMafOriRangeMax(q.mafOrg);

        if(queryParam.getGcRange() != null) {
            minGC = queryParam.getGCRangeMin(q.gc);
            maxGC = queryParam.getGCRangeMax(q.gc);
        }

        if(queryParam.getDisRange() != null) {
            minDis = queryParam.getDisRangeMin(q.dtct);
            maxDis = queryParam.getDisRangeMax(q.dtct);
        }

        if(queryParam.geneDisIndex != GP.NO_GENE_DIS) {
            minGene = queryParam.getGeneRangeMin(q.geneLD);
            maxGene = queryParam.getGeneRangeMax(q.geneLD);
        } else if(queryParam.geneLDIndex != GP.NO_GENE_LD) {
            minGene = queryParam.getGeneRangeMin(q.geneDis);
            maxGene = queryParam.getGeneRangeMax(q.geneDis);
        }

        if(queryParam.ldIndex != GP.NO_LD_BUDDIES) {
            minLD = queryParam.getLDBuddiesRangeMin(q.ldBuddies);
            maxLD = queryParam.getLDBuddiesRangeMax(q.ldBuddies);
        }
    }

    public int isInRangeMAFOri(final double dbmaf) {
        if(dbmaf < minMAF) {
            return LESS;
        } else if(dbmaf > maxMAF) {
            return GREATER;
        } else {
            return IN;
        }
    }

    public int isInRangeGC(final int dbGC) {
        if(dbGC < minGC) {
            return LESS;
        } else if(dbGC > maxGC) {
            return GREATER;
        } else {
            return IN;
        }
    }

    public int isInRangeDistance(final int dbDistance) {
        if(dbDistance < minDis) {
            return LESS;
        } else if(dbDistance > maxDis) {
            return GREATER;
        } else {
            return IN;
        }
    }

    public int isInRangeGene(final int dbGene) {
        if(dbGene < minGene) {
            return LESS;
        } else if(dbGene > maxGene) {
            return GREATER;
        } else {
            return IN;
        }
    }

    public int isInRangeLDBuddies(final int dbLDBuddies) {
        if(dbLDBuddies < minLD) {
            return LESS;
        } else if(dbLDBuddies > maxLD) {
            return GREATER;
        } else {
            return IN;
        }
    }
}
