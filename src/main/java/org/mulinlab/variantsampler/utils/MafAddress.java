package org.mulinlab.variantsampler.utils;

public final class MafAddress {
    private final int maf;
    private long mafAddress;
    private long[] geneDistanceAddress;
    private long[] geneLDAddress;
    private long[] ldBuddiesAddress;

    private long[] roadmapAnnoAddress;
    private long catAddress;
    private long tissueAddress;
    private long[] gcAddress;

    public MafAddress(final int maf) {
        this.maf = maf;
        geneDistanceAddress = new long[GP.GENE_DIS_SIZE];
        geneLDAddress = new long[GP.GENE_LD_SIZE];
        ldBuddiesAddress = new long[GP.LD_BUDDIES_SIZE];
        roadmapAnnoAddress = new long[GP.ROADMAP_SIZE];
        gcAddress = new long[GP.GC_SIZE];
    }

    public long getMafAddress() {
        return mafAddress;
    }

    public void setMafAddress(long mafAddress) {
        this.mafAddress = mafAddress;
    }

    public long[] getGeneDistanceAddress() {
        return geneDistanceAddress;
    }

    public void setGeneDistanceAddress(long geneDistanceAddress, final int index) {
        this.geneDistanceAddress[index] = geneDistanceAddress;
    }

    public long[] getGeneLDAddress() {
        return geneLDAddress;
    }

    public void setGeneLDAddress(long geneLDAddress, final int index) {
        this.geneLDAddress[index] = geneLDAddress;
    }

    public long[] getLdBuddiesAddress() {
        return ldBuddiesAddress;
    }

    public void setLdBuddiesAddress(long ldBuddiesAddress, final int index) {
        this.ldBuddiesAddress[index] = ldBuddiesAddress;
    }

    public long[] getRoadmapAnnoAddress() {
        return roadmapAnnoAddress;
    }

    public void setRoadmapAnnoAddress(long roadmapAnno, final int index) {
        this.roadmapAnnoAddress[index] = roadmapAnno;
    }

    public long getCatAddress() {
        return catAddress;
    }

    public void setCatAddress(long catAddress) {
        this.catAddress = catAddress;
    }

    public long getTissueAddress() {
        return tissueAddress;
    }

    public void setTissueAddress(long tissueAddress) {
        this.tissueAddress = tissueAddress;
    }

    public long[] getGcAddress() {
        return gcAddress;
    }

    public void setGcAddress(long gcAddress, final int index) {
        this.gcAddress[index] = gcAddress;
    }

    public int getMaf() {
        return maf;
    }
}
