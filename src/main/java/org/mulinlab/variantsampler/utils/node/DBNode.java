package org.mulinlab.variantsampler.utils.node;

import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.varnote.utils.node.LocFeature;

import java.util.BitSet;

public final class DBNode extends AbstractNode {
    private Integer[] geneDensity;
    private Integer[] geneInLD;
    private Integer[] ldBuddies;
    private long[][] cellMarks;
    private long address;
    private boolean isIndel;
    private int category;
    private long tissueAnno;
    private int[] gc;

    public DBNode(LocFeature locFeature, double maf, int dtct, Integer[] geneDensity, Integer[] geneInLD, Integer[] ldBuddies, long[][] cellMarks, String rsid,
                  int category, long tissueAnno, int[] gc) {
        this.locFeature = locFeature;
        this.mafOrg = maf;
        this.maf = (int)(maf * 100);
        this.dtct = dtct;
        this.geneDensity = geneDensity;
        this.geneInLD = geneInLD;
        this.ldBuddies = ldBuddies;
        this.cellMarks = cellMarks;
        this.rsid = rsid;
        this.category = category;
        this.tissueAnno = tissueAnno;
        this.gc = gc;
    }

    public DBNode(final LocFeature locFeature) {
        this.locFeature = locFeature.clone();
        locFeature.origStr = null;
        this.isIndel = (this.locFeature.ref.length() != this.locFeature.alt.length());

        this.mafOrg = Double.parseDouble(locFeature.parts[GP.MAF_IDX]);
        this.maf = (int)(mafOrg * 100);

        this.dtct = Integer.parseInt(locFeature.parts[GP.DIS_IDX]);
    }

    public void decodeOthers() {
        geneDensity = new Integer[GP.GENE_DIS_SIZE];
        geneInLD = new Integer[GP.GENE_LD_SIZE];
        ldBuddies = new Integer[GP.LD_BUDDIES_SIZE];
        cellMarks = new long[GP.ROADMAP_SIZE][];

        for (int i = 0; i < GP.GENE_DIS_SIZE; i++) {
            geneDensity[i] = Integer.parseInt(locFeature.parts[i + GP.GENE_DIS_START]);
        }

        for (int i = 0; i < GP.GENE_LD_SIZE; i++) {
            geneInLD[i] = Integer.parseInt(locFeature.parts[i + GP.GENE_LD_START]);
        }

        for (int i = 0; i < GP.LD_BUDDIES_SIZE; i++) {
            ldBuddies[i] = Integer.parseInt(locFeature.parts[i + GP.LD_BUDDIES_START]);
        }

        for (int i = 0; i < GP.ROADMAP_SIZE; i++) {
            if(locFeature.parts[i + GP.ROADMAP_START].trim().length() > 0) {
                cellMarks[i] = GP.string2LongArr(locFeature.parts[i + GP.ROADMAP_START].split(","));
            } else {
                cellMarks[i] = null;
            }
        }

        category = Integer.parseInt(locFeature.parts[GP.CAT_START]);
        if(locFeature.parts[GP.TISSUE_START].trim().length() > 0) {
            tissueAnno = Long.parseLong(locFeature.parts[GP.TISSUE_START]);
        } else {
            tissueAnno = 0;
        }

        gc = GP.string2IntegerArr(locFeature.parts[GP.GC_START].split(","));
        locFeature.parts = null;
    }

    public Integer[] getGeneDensity() {
        return geneDensity;
    }

    public Integer[] getGeneInLD() {
        return geneInLD;
    }

    public Integer[] getLdBuddies() {
        return ldBuddies;
    }

    public long[][] getCellMarks() {
        return cellMarks;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public boolean isIndel() {
        return isIndel;
    }

    public int getCategory() {
        return category;
    }

    public long getTissueAnno() {
        return tissueAnno;
    }

    public int[] getGc() {
        return gc;
    }
}
