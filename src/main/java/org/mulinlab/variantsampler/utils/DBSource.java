package org.mulinlab.variantsampler.utils;

import org.mulinlab.varnote.exceptions.InvalidArgumentException;
import org.mulinlab.varnote.filters.iterator.NoFilterIterator;
import org.mulinlab.varnote.utils.enumset.FileType;
import java.util.HashMap;
import java.util.Map;

public final class DBSource {
    static final char COMMAND = '#';
    static final char SECTION_BEGIN = '[';
    static final char SECTION_END = ']';

    public static final String ROADMAP = "roadmap";
    public static final String DB1000G = "1000G";
    public static final String GENCODE_GENE = "gene_file";
    public static final String BITFILE = "bit_file";
    public static final String SERPATH = "ser_path";
    public static final String GENOME = "genome";
    public static final String OUT_DIR = "output_dir";
    public static final String VF_PATH = "vf_path";
    public static final String TISSUE_LIST = "tissue_list";
    public static final String TISSUE_PATH = "tissue_path";
    public static final String GC_PATH = "gc_path";
    public static final String LD_WINDOW = "ld_window";
    public static final String MAF_CUTOFF = "maf_cutoff";


    private final String[] required = new String[]{ROADMAP, DB1000G, GENCODE_GENE, BITFILE, SERPATH, GENOME, OUT_DIR, VF_PATH, TISSUE_LIST, TISSUE_PATH, GC_PATH, LD_WINDOW, MAF_CUTOFF};
    private final Map<String, String> srcMap;

    public DBSource(final String srcFile) throws InvalidArgumentException {
        srcMap = new HashMap<>();
        for (String src: required) {
            srcMap.put(src, "");
        }

        NoFilterIterator reader = new NoFilterIterator(srcFile, FileType.TXT);

        String line;
        while(reader.hasNext()) {
            line = reader.next();
            if ((line.charAt(0) != SECTION_BEGIN) && line.charAt(0) != COMMAND) {
                parseLine(line);
            }
        }

        for (String src: required) {
            if(srcMap.get(src).equals("")) {
                throw new InvalidArgumentException(String.format("%s database is missing.", src));
            }
        }
        reader.close();
    }

    private void parseLine(final String line) {
        String name = null, value = null;

        int idx = line.indexOf('=');
        if(idx != -1 ) {
            name = line.substring(0, idx).trim();
            value = line.substring(idx+1).trim();

            if(srcMap.get(name) != null) {
                srcMap.put(name, value);
            }
        }
    }

    public String getVal(final String dbName) {
        return srcMap.get(dbName);
    }

    public double getValDouble(final String dbName) {
        return Double.parseDouble(srcMap.get(dbName));
    }

    public int getValInt(final String dbName) {
        return Integer.parseInt(srcMap.get(dbName));
    }
}
