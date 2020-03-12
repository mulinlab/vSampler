package org.mulinlab.variantsampler.cmdline.tools;

import org.broadinstitute.barclay.argparser.Argument;
import org.broadinstitute.barclay.argparser.ArgumentCollection;
import org.broadinstitute.barclay.argparser.CommandLineProgramProperties;
import org.mulinlab.variantsampler.cmdline.CMDProgram;
import org.mulinlab.variantsampler.cmdline.InputFileArgumentCollection;
import org.mulinlab.variantsampler.cmdline.programGroup.SamplerProgramGroup;
import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.QueryParam;
import org.mulinlab.variantsampler.utils.enumset.*;

import java.io.File;
import java.io.IOException;

@CommandLineProgramProperties(
        summary = Sampler.USAGE_SUMMARY + Sampler.USAGE_DETAILS,
        oneLineSummary = Sampler.USAGE_SUMMARY,
        programGroup = SamplerProgramGroup.class)
public final class Sampler extends CMDProgram {

    static final String USAGE_SUMMARY = "Sampler";
    static final String USAGE_DETAILS = "\n\nUsage example:" + GP.PRO_CMD + ".jar Sampler -Q input.txt -D data/EUR.gz\n";


    ////File options
    @ArgumentCollection()
    protected final InputFileArgumentCollection inputArguments = new InputFileArgumentCollection();

    @Argument(fullName = "Database", shortName = "D", doc = "The database file.")
    private File databaseFile = null;

    @Argument(fullName = "OutPath", shortName = "O", doc = "The output folder path.", optional = true)
    private String outPath = null;


    ////Basic options
    @Argument(fullName = "isCrossChr", shortName = "CC", doc = "Indicator of sampling across chromosomes or not.", optional = true)
    private Boolean isCrossChr = false;

    @Argument(fullName = "excludeInput", shortName = "EI", doc = "Indicator to exclude input SNPs from matched SNPs or not.", optional = true)
    private Boolean excludeInput = true;

    @Argument(fullName = "vriantTypeSpecific", shortName = "VFS", doc = "Indicator of doing variant type specific sampling or not. i.e. sample indels for indels, snps for snps", optional = true)
    private Boolean vriantTypeSpecific = true;

    @Argument(fullName = "controlNumber", shortName = "SN", doc = "Sample control number", optional = true)
    private int samplerNumber = 1;

    @Argument(fullName = "annoNumber", shortName = "AN", doc = "Annotation number", optional = true)
    private int annoNumber = 1;


    ////Annotation options
    @Argument(fullName = "MAFDeviation", shortName = "MD", doc = "Deviation range of MAF. Input variant MAF ± MAF deviation range. (D1 means ±0.01, D2 means ±0.02, D3 means ±0.03, D4 means ±0.04, D5 means ±0.05, D6 means ±0.06, D7 means ±0.07, D8 means ±0.08, D9 means ±0.09, D10 means ±0.1)")
    private MAFDeviation mafDevia = GP.DEFAULT_MAF_DEVIATION;

    @Argument(fullName = "disDeviation", shortName = "DD", doc = "Deviation range of distance to closest transcription start site (DTCT). Input variant DTCT ± DTCT deviation range.", optional = true)
    private int disDevia = GP.DEFAULT_DIS_DEVIATION;

    @Argument(fullName = "geneDeviation", shortName = "GD", doc = "Deviation range of gene density number.", optional = true)
    private int geneDevia = GP.DEFAULT_GENE_DEVIATION;

    @Argument(fullName = "inLDvariantsDeviation", shortName = "LDD", doc = "Deviation range of in LD variants number.", optional = true)
    private int ldBuddiesDevia = GP.DEFAULT_LD_BUDDIES_DEVIATION;

    @Argument(fullName = "GeneInDis", shortName = "GP", doc = "Physical distance cutoff to define gene density of variants. (KB100 means distance in 100KB, KB200 means distance in 200KB, KB300 means distance in 300KB, KB400 means distance in 400KB, KB500 means distance in 500KB, KB600 means distance in 600KB, KB700 means distance in 700KB, KB800 means distance in 800KB, KB900 means distance in 900KB, KB1000 means distance in 1M).", optional = true)
    private GeneInDis geneInDis = GP.DEFAULT_GENE_DIS;

    @Argument(fullName = "GeneInLD", shortName = "GLD", doc = "LD cutoff to define gene density of variants. (LD1 means ld>0.1, LD2 means ld>0.2, LD3 means ld>0.3, LD4 means ld>0.4, LD5 means ld>0.5, LD6 means ld>0.6, LD7 means ld>0.7, LD8 means ld>0.8, LD9 means ld>0.9).", optional = true)
    private LD geneInLD = GP.DEFAULT_GENE_LD;

    @Argument(fullName = "inLDvariants", shortName = "LDB", doc = "LD cutoff to define in LD variants. (LD1 means ld>0.1, LD2 means ld>0.2, LD3 means ld>0.3, LD4 means ld>0.4, LD5 means ld>0.5, LD6 means ld>0.6, LD7 means ld>0.7, LD8 means ld>0.8, LD9 means ld>0.9).", optional = true)
    private LD ldBuddies = GP.DEFAULT_IN_LD_VIRANTS;


    @Argument(fullName = "CellType", shortName = "CT", doc = "Roadmap cell type. This should be supplied with `-M,--Mark`", optional = true)
    private CellType cellType = null;

    @Argument(fullName = "Mark", shortName = "M", doc = "Roadmap cell type specific epigenomic mark. This should be supplied with `-CT,--CellType`", optional = true)
    private Marker marker = null;

    @Argument(fullName = "Tissue", shortName = "TS", doc = "Match eQTL in tissue.", optional = true)
    private TissueType tissueType = null;

    @Argument(fullName = "RegionMatch", shortName = "RM", doc = "Indicator to match variant region or not. The types of variant region are exonic + splicing altering, noncoding and others.", optional = true)
    private boolean regionMatch = false;


    @Argument(fullName = "GCType", shortName = "GCT", doc = "Distance range to compute GC content(BP100 means ±100bp, BP200 means ±200bp, BP300 means ±300bp, BP400 means ±400bp, BP500 means ±500bp).", optional = true)
    private GCType gcType = null;

    @Argument(fullName = "GCDeviation", shortName = "GCD", doc = "Deviation range of GC. Input GC content ± GC deviation range. (D1 means ±0.01, D2 means ±0.02, D3 means ±0.03, D4 means ±0.04, D5 means ±0.05, D6 means ±0.06, D7 means ±0.07, D8 means ±0.08, D9 means ±0.09, D10 means ±0.1)")
    private GCDeviation gcDeviation = GP.DEFAULT_GC_DEVIATION;

    @Argument(fullName = "Seed", shortName = "S", doc = "Random seed.", optional = true)
    private int randomSeed = -1;

    @Override
    protected int doWork() {
        try {
            QueryParam queryParam = new QueryParam(inputArguments.getFormat(inputArguments.getQueryFilePath(), true), GeneInDis.getIdx(geneInDis), LD.getIdx(geneInLD), LD.getIdx(ldBuddies), Marker.getIdx(marker), CellType.getIdx(cellType),
                    mafDevia, disDevia, geneDevia, ldBuddiesDevia, regionMatch, TissueType.getIdx(tissueType),  GCType.getIdx(gcType), gcDeviation);
            queryParam.setVariantTypeSpecific(vriantTypeSpecific);
            queryParam.setCrossChr(isCrossChr);
            queryParam.setExcludeInput(excludeInput);
            queryParam.setSamplerNumber(samplerNumber);
            queryParam.setAnnoNumber(annoNumber);

            if(randomSeed != -1) queryParam.setRandomSeed(randomSeed);
            org.mulinlab.variantsampler.query.Sampler query = new org.mulinlab.variantsampler.query.Sampler(inputArguments.getQueryFilePath(), databaseFile.getAbsolutePath(), queryParam, outPath);
            query.doQuery();
            query.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
