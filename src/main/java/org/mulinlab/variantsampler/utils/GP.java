package org.mulinlab.variantsampler.utils;

import org.mulinlab.variantsampler.utils.enumset.*;
import org.mulinlab.variantsampler.utils.node.Node;
import org.mulinlab.varnote.constants.GlobalParameter;
import org.mulinlab.varnote.operations.decode.TABLocCodec;
import org.mulinlab.varnote.utils.format.Format;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class GP {
    public final static String PRO_NAME = "VariantSampler";
    public final static String PRO_CMD = "java -jar /path/to/VariantSampler.jar" ;
    public final static boolean DEFAULT_LOG = true;

    public final static String TAB = "\t";
    public static final boolean COLOR_STATUS;

    static {
        COLOR_STATUS = true;
    }

    public final static int HELP_SIMILARITY_FLOOR = 7;
    public final static int MINIMUM_SUBSTRING_LENGTH = 5;

    public static final int MAX_SHORT_UNSIGNED = 65535;

    public static final int FROM_CHROM = 1;
    public static final int TO_CHROM = 22;
    public static final int MAX_MAF = 50;

    public static final int GENE_DIS_SIZE = 10;
    public static final int GENE_LD_SIZE = 9;
    public static final int LD_BUDDIES_SIZE = 9;
    public static final int ROADMAP_SIZE = 6;
    public static final int GC_SIZE = 5;

    public static final int GENE_DIS_START = 6;
    public static final int GENE_LD_START = 16;
    public static final int LD_BUDDIES_START = 25;
    public static final int ROADMAP_START = 34;
    public static final int CAT_START = 40;
    public static final int TISSUE_START = 41;
    public static final int GC_START = 42;


    public static final int RSID_IDX = 46;
    public static final int CHR_IDX = 0;
    public static final int BP_IDX = 1;
    public static final int REF_IDX = 2;
    public static final int ALT_IDX = 3;
    public static final int MAF_IDX = 4;
    public static final int DIS_IDX = 5;

    public static final int BITSET_SIZE = 64;
    public static final int ROADMAP_LEFT = 64;
    public static final int ROADMAP_RIGHE = 63;

    public static final double MAF_FILTER = 0.01;

    public static final int LD_DISTANCE = (int)(0.1*1000*1000);
    public static final int RANGE_STEP = 5000;

    public static final int NO_MARKER = -1;
    public static final int NO_TISSUE = -1;
    public static final int NO_CELL_TYPE = -1;
    public static final int NO_GENE_DIS = -1;
    public static final int NO_DTCT = -1;
    public static final int NO_GENE_LD = -1;
    public static final int NO_LD_BUDDIES = -1;
    public static final int NO_GC = -1;

    public static final int TISSUE_SIZE = 49;

    public static final GeneInDis DEFAULT_GENE_DIS = GeneInDis.KB500;
    public static final LD DEFAULT_GENE_LD = LD.LD5;
    public static final LD DEFAULT_IN_LD_VIRANTS = LD.LD5;

    public static final MAFDeviation DEFAULT_MAF_DEVIATION = MAFDeviation.D5;
    public static final int DEFAULT_DIS_DEVIATION = 5000;
    public static final int DEFAULT_GENE_DEVIATION = 5;
    public static final int DEFAULT_LD_BUDDIES_DEVIATION = 50;
    public static final GCDeviation DEFAULT_GC_DEVIATION = GCDeviation.D5;

    public static final String ANNO_OUT = "anno.out.txt";
    public static final String SAMPLER_OUT = "sampler.out.txt";
    public static final String CONFIG_OUT = "sampler.config.txt";
    public static final String INPUT_EXCLUDE_OUT = "input.exclude.txt";

    public static float readFloat(InputStream is) throws IOException {
        return Float.intBitsToFloat(GlobalParameter.readInt(is));
    }

    public final static String DTCT_HEADER = "DTCT";
    public final static String GENE_DIS_HEADER = "Gene_Dis";
    public final static String GENE_LD_HEADER = "Gene_LD";
    public final static String LD_BUDDIES_HEADER = "inLDvariants";

    public final static String GC_HEADER = "GC_Content";
    public final static String TISSUE_HEADER = "eQTL_in_Tissue";
    public final static String VARIANT_REGION = "Variant_Region";

    public static TABLocCodec getDefaultDecode(boolean isFull) {
        Format format = Format.newTAB();
        format.sequenceColumn = 1;
        format.startPositionColumn = 2;
        format.endPositionColumn = 2;
        format.refPositionColumn = 3;
        format.altPositionColumn = 4;
        return new TABLocCodec(format, isFull);
    }

    public static long[] string2LongArr(String[] p) {
        long[] longarr = new long[p.length];
        for (int j = 0; j < p.length; j++) {
            longarr[j] = Long.parseLong(p[j]);
        }

        return longarr;
    }

    public static int[] string2IntegerArr(String[] p) {
        int[] intarr = new int[p.length];
        for (int j = 0; j < p.length; j++) {
            intarr[j] = Integer.parseInt(p[j]);
        }
        return intarr;
    }

    public static int[] generateRandomArray(int max, int N, Random r) {
        int[] samplerArr = new int[N];

        int seedArray[] = new int[max];
        for (int i = 0; i < max; i++) {
            seedArray[i] = i;
        }

        int index = 0;
        for(int i = 0; i < N; i++)
        {
            if(i == max) {
                for (int j = 0; j < max; j++) {
                    seedArray[j] = j;
                }
                i = 0;
                N = N - max;
            }
            int seed = r.nextInt(max - i);
            samplerArr[index++] = seedArray[seed];
            seedArray[seed] = seedArray[max - i - 1];
        }

        return samplerArr;
    }
}
