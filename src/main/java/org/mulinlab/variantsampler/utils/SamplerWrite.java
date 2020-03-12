package org.mulinlab.variantsampler.utils;

import org.apache.commons.lang3.StringUtils;
import org.mulinlab.variantsampler.utils.enumset.*;
import org.mulinlab.variantsampler.utils.node.Node;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SamplerWrite {
    public static final String QUERY = "query";
    public static final String RATIO = "ratio";
    public static final String ANNO = "annotation";
    public static final String CONTROL = "control";

    private BufferedWriter annoOutput;
    private BufferedWriter samplerOutput;
    private BufferedWriter configOutput;
    private BufferedWriter inputExcludeOutput;
    private QueryParam queryParam;
    private boolean hasRefAlt = false;
    private String outputDir;
    private String outFolderName;

    private int insufficientCount = 0;
    private int excludedCount = 0;
    private int annoCount = 0;
    private List<Integer> insuffPool;

    private String queryFile;
    private String dbFile;

    public SamplerWrite(final String queryFile, final String dbFile, final QueryParam queryParam, String outputDir) {
        this.queryParam = queryParam;
        this.queryFile = queryFile;
        this.dbFile = dbFile;

        if(outputDir == null) outputDir = getDefaultOutput(queryFile);
        else {
            File dir = new File(outputDir);
            if(!dir.exists()) {
                dir.mkdir();
            }
        }

        this.insuffPool = new ArrayList<>();
        this.outputDir = outputDir;
        this.hasRefAlt = queryParam.getFormat().isRefAndAltExsit();

        try {
            this.annoOutput = new BufferedWriter(new FileWriter(new File(outputDir + File.separator + GP.ANNO_OUT)));
            this.samplerOutput = new BufferedWriter(new FileWriter(new File(outputDir + File.separator + GP.SAMPLER_OUT)));
            this.configOutput = new BufferedWriter(new FileWriter(new File(outputDir + File.separator + GP.CONFIG_OUT)));
            this.inputExcludeOutput = new BufferedWriter(new FileWriter(new File(outputDir + File.separator + GP.INPUT_EXCLUDE_OUT)));

            writeHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printConfig(final String queryFile, final String dbFile) throws IOException {
        printConfigLine("Sampler File: " + new File(queryFile).getName());
        printConfigLine("Database File: " + new File(dbFile).getName());
        printConfigLine("");

        List<Log> configs = toConfigArray();
        int group = 0;
        for (Log config: configs) {
            printConfigLine(config.toString());
            if(config.getGroup() != group) {
                printConfigLine("");
                group = config.getGroup();
            }
        }
        configOutput.close();
    }

    public List<Log> toConfigArray() {
        List<Log> list = new ArrayList<>();
        list.add(new Log(0, "isExcludeInput", "Exclude input SNPs", queryParam.excludeInput + ""));
        list.add(new Log(0, "isCrossChr", "Sampling across chromosomes", queryParam.isCrossChr() + ""));
        list.add(new Log(0, "isVariantTypeSpecific", "Variant type specific", queryParam.variantTypeSpecific + ""));
        list.add(new Log(0, "samplerNumber", "Sample control number", queryParam.getSamplerNumber() + ""));
        list.add(new Log(0, "annoNumber", "Annotation number", queryParam.getAnnoNumber() + ""));


        list.add(new Log(1, "maf", "MAF deviation", "[" + (double)queryParam.getMafRange()[0]/100 + ", " + (double)queryParam.getMafRange()[1]/100 + "]"));
        if(queryParam.getDisRange() != null) {
            list.add(new Log(1, "dtct", "Distance to closest tss deviation", "[" + queryParam.getDisRange()[0] + ", " + queryParam.getDisRange()[1] + "]"));
        }
        if(queryParam.geneDisIndex != GP.NO_GENE_DIS) {
            list.add(new Log(1, "GeneInDis", "Gene density in distance", GeneInDis.getVal(queryParam.geneDisIndex).getTitle()));
        }
        if(queryParam.geneLDIndex != GP.NO_GENE_LD) {
            list.add(new Log(1, "GeneInLD", "Gene density in ld", LD.getVal(queryParam.geneLDIndex).getTitle()));
        }
        if(queryParam.getGeneRange() != null) {
            list.add(new Log(1, "geneDeviation", "Gene density deviation", "[" + queryParam.getGeneRange()[0] + ", " + queryParam.getGeneRange()[1] + "]"));
        }
        if(queryParam.ldIndex != GP.NO_LD_BUDDIES) {
            list.add(new Log(1, "LDBuddies", "in LD variants", LD.getVal(queryParam.ldIndex).getTitle()));
            list.add(new Log(1, "ldDeviation", "LD deviation", "[" + queryParam.getLdBuddiesRange()[0] + ", " + queryParam.getLdBuddiesRange()[1] + "]"));
        }

        if(queryParam.getGcRange() != null) {
            list.add(new Log(1, "gcType", "GC Range", GCType.getVal(queryParam.gcIdx).getTitle()));
            list.add(new Log(1, "gcDeviation", "GC deviation", "[" + queryParam.getGcRange()[0]/100 + ", " + queryParam.getGcRange()[1]/100 + "]"));
        }

        list.add(new Log(1, "VariantRegion", "Variant Region Match", queryParam.variantTypeMatch + ""));
        if(queryParam.hasCellMarker) {
            list.add(new Log(1, "CellType", "Cell type-specific Epigenomic Marks:", CellType.getVal(queryParam.cellIdx).toString() + "|" + Marker.getVal(queryParam.markerIndex).toString()));
        }
        if(queryParam.tissueIdx > -1) {
            list.add(new Log(1, "Tissue", "Tissue", TissueType.getVal(queryParam.tissueIdx).toString()));
        }


        list.add(new Log(2, "insufficient", "Number of insufficient match", String.valueOf(getInsufficientCount())));
        if(getAnnoCount() > 0) {
            list.add(new Log(2, "insufficientPro", "Proportion of insufficient match", String.format(" %.4f", (double)getInsufficientCount()/getAnnoCount())));
        }
        list.add(new Log(2, "medianSize", "Median size of insufficient pool", String.valueOf(getMedian())));
        list.add(new Log(2, "medianPro", "Median proportion of of insufficient pool", String.format(" %.3f", getMedian()/queryParam.getSamplerNumber())));
        list.add(new Log(2, "excluded", "Number of variants excluded", String.valueOf(getExcludedCount())));

        return list;
    }

    public void printConfigLine(String line) throws IOException {
        configOutput.write(line);
        configOutput.newLine();
    }

    public String getDefaultOutput(final String queryFile) {
        File query = new File(queryFile);
        this.outFolderName = query.getName() + "_out";

        File folder = new File(query.getParent() + File.separator + outFolderName);

        if(!folder.exists()) {
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }

    public void writeHeader() throws IOException {
        List<String> header = new ArrayList<>();
        header.add(RATIO);
        header.add(QUERY);
        header.add(ANNO);

        for (int i = 1; i <= queryParam.getSamplerNumber(); i++) {
            header.add(CONTROL + i);
        }
        this.samplerOutput.write(StringUtils.join(header, "\t"));
        this.samplerOutput.newLine();

        this.annoOutput.write(getHeader(queryParam));
        this.annoOutput.newLine();
    }

    public String getHeader(final QueryParam queryParam) {
        String header = "LABEL\tCHR\tPOS\tREF\tALT\tMAF";

        if(queryParam.getDisRange() != null) {
            header += "\t" + GP.DTCT_HEADER;
        }
        if(queryParam.geneDisIndex != GP.NO_GENE_DIS) {
            header += "\t" + GP.GENE_DIS_HEADER + "_" + GeneInDis.getVal(queryParam.geneDisIndex);
        }
        if(queryParam.geneLDIndex != GP.NO_GENE_LD) {
            header += "\t" + GP.GENE_LD_HEADER + "_" + LD.getVal(queryParam.geneLDIndex);
        }
        if(queryParam.ldIndex != GP.NO_LD_BUDDIES) {
            header += "\t" + GP.LD_BUDDIES_HEADER + "_" + LD.getVal(queryParam.ldIndex);
        }
        if(queryParam.getGcRange() != null) {
            header += "\t" + GP.GC_HEADER + "_" + GCType.getVal(queryParam.gcIdx);
        }
        if(queryParam.variantTypeMatch) {
            header += "\t" + GP.VARIANT_REGION;
        }
        if(queryParam.hasCellMarker) {
            header += "\t" + CellType.getVal(queryParam.cellIdx).toString() + "_" + Marker.getVal(queryParam.markerIndex).toString();
        }
        if(queryParam.tissueIdx != GP.NO_TISSUE) {
            header += "\t" + GP.TISSUE_HEADER + "_" + TissueType.getVal(queryParam.tissueIdx);
        }
//        if(hasJannovar) {
//            header += "\tVariant Effect\tGene Symbol\tGene ID";
//        }
        return header;
    }

    public void printSampler(final Node query, String line) throws IOException {
        if(query.hasAnno > 0) {
            annoCount++;
            if(query.poolSize < queryParam.getSamplerNumber()) {
                insufficientCount ++;
                insuffPool.add(query.poolSize);
            }
            this.samplerOutput.write(line);
            this.samplerOutput.newLine();
        } else {
            excludedCount++;
            this.inputExcludeOutput.write(line);
            this.inputExcludeOutput.newLine();
        }
    }


    public void printAnno(final Node node, final String label) throws IOException {
        if(node == null) {
            annoOutput.write("Not found");
        } else {
            annoOutput.write(label + "\t" + node.toString(queryParam));
        }
        annoOutput.newLine();
    }

    public void close() throws IOException {
        annoOutput.close();
        samplerOutput.close();
        this.inputExcludeOutput.close();

        printConfig(queryFile, dbFile);
        ZipFiles.zipDirectory(new File(this.outputDir), this.outputDir + ".zip");
    }

    public int getInsufficientCount() {
        return insufficientCount;
    }

    public int getExcludedCount() {
        return excludedCount;
    }

    public int getAnnoCount() {
        return annoCount;
    }

    public double getMedian() {
        Collections.sort(insuffPool);

        int size = insuffPool.size();
        if(size > 0) {
            if (size % 2 == 0)
                return ((double)insuffPool.get(size/2) + (double)insuffPool.get(size/2 - 1))/2;
            else
                return (double)insuffPool.get(size/2);
        } else {
            return 0;
        }
    }

    public class Log {
        private int group;
        private String key;
        private String desc;
        private String val;

        public Log(int group, String key, String desc, String val) {
            this.group = group;
            this.key = key;
            this.desc = desc;
            this.val = val;
        }

        @Override
        public String toString() {
            return desc + ": " + val;
        }

        public int getGroup() {
            return group;
        }
    }
}
