package org.mulinlab.variantsampler.query;


import org.mulinlab.variantsampler.utils.QueryParam;
import org.mulinlab.variantsampler.utils.SamplerWrite;
import org.mulinlab.variantsampler.utils.node.Node;
import org.mulinlab.varnote.config.anno.databse.VCFParser;
import org.mulinlab.varnote.operations.readers.query.AbstractFileReader;
import org.mulinlab.varnote.utils.TimeMetric;
import org.mulinlab.varnote.utils.VannoUtils;
import org.mulinlab.varnote.utils.enumset.FileType;
import org.mulinlab.varnote.utils.enumset.FormatType;
import org.mulinlab.varnote.utils.format.Format;
import org.mulinlab.varnote.utils.headerparser.HeaderFormatReader;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sampler {

    protected DatabaseEngine dbEngine;
    protected InputStack inputStack;

    protected TimeMetric timeMetric;
    protected QueryParam queryParam;
    protected SamplerWrite samplerWrite;

    public Sampler(final String queryFile, final String dbFile) {
        this(queryFile, dbFile, QueryParam.defaultQueryParam(), null);
    }

    public Sampler(final String queryFile, final Format format, final String dbFile) {
        this(queryFile, dbFile, QueryParam.defaultQueryParam(format), null);
    }

    public Sampler(final String queryFile, final String dbFile, final QueryParam queryParam) {
        this(queryFile, dbFile, queryParam, null);
    }
    public Sampler(final String queryFile, final String dbFile, final QueryParam queryParam, String outputDir) {
        this.queryParam = queryParam;

        this.timeMetric = new TimeMetric("VSampler ");
        this.timeMetric.setMaxCount(1000);
        try {
            samplerWrite = new SamplerWrite(queryFile, dbFile, queryParam, outputDir);

            FileType fileType = VannoUtils.checkFileType(queryFile);
            checkFormat(queryParam.getFormat(), queryFile, fileType);
            AbstractFileReader reader = VannoUtils.getReader(queryFile, fileType, queryParam.getFormat());

            inputStack = new InputStack(reader.getFilterIterator(), queryParam.getFormat().isRefAndAltExsit());
            dbEngine = new DatabaseEngine(dbFile, inputStack, queryParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkFormat(Format format, String file, FileType fileType) {
        if(format.type == FormatType.VCF) {
            new VCFParser(file);
        } else {
            if (format.isHasHeader()) {
                format = HeaderFormatReader.readDefaultHeader(file, fileType, format, true);
            } else {
                format = HeaderFormatReader.readDefaultHeader(file, fileType, format, false);
                HeaderFormatReader.checkDataIsValid(format, format.getDataStr().split("\t"));
            }
        }
    }

    public void close() throws IOException {
        timeMetric.doEnd();
        dbEngine.close();
        samplerWrite.close();
    }

    public void doQuery() throws IOException {
        List<LocFeature> locFeatures;

        if(queryParam.isCrossChr()) {
            locFeatures = inputStack.getLocFeatureForAll(new ArrayList<>());
            Map<Integer, List<Node>> map = dbEngine.convertQuery(locFeatures);
            locFeatures = null;
            proceeMap(map);
        } else {
            for (String chr: inputStack.getChrList()) {
                locFeatures = inputStack.getLocFeatureForChr(new ArrayList<>(), chr);
                Map<Integer, List<Node>> map = dbEngine.convertQuery(locFeatures);
                locFeatures = null;
                proceeMap(map);
            }
        }
    }

    public void proceeMap(Map<Integer, List<Node>> map) throws IOException {
        for (Integer maf: map.keySet()) {
            for (Node node: map.get(maf)) {
                processLoc(node);
            }
        }
    }

    public void addRecordCount() {
        timeMetric.addRecord();
    }

    public void processLoc(Node node) throws IOException {
        addRecordCount();
        dbEngine.findNode(node, samplerWrite);
        node = null;
    }

    public DatabaseEngine getDbEngine() {
        return dbEngine;
    }

    public InputStack getInputStack() {
        return inputStack;
    }

    public long getCount() {
        return timeMetric.getCount();
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public SamplerWrite getSamplerWrite() {
        return samplerWrite;
    }
}
