package org.mulinlab.variantsampler.database;

import com.google.gson.Gson;
import htsjdk.samtools.util.BlockCompressedInputStream;
import htsjdk.samtools.util.Log;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mulinlab.variantsampler.utils.*;
import org.mulinlab.variantsampler.utils.node.DBNode;
import org.mulinlab.varnote.config.param.DBParam;
import org.mulinlab.varnote.utils.JannovarUtils;
import org.mulinlab.varnote.utils.LoggingUtils;
import org.mulinlab.varnote.utils.database.DatabaseFactory;
import org.mulinlab.varnote.utils.database.TbiDatabase;
import org.mulinlab.varnote.utils.database.index.TbiIndex;
import org.mulinlab.varnote.utils.enumset.GenomeAssembly;
import org.mulinlab.varnote.utils.enumset.IndexType;
import org.mulinlab.varnote.utils.gz.MyBlockCompressedOutputStream;
import org.mulinlab.varnote.utils.gz.MyEndianOutputStream;
import org.mulinlab.varnote.utils.jannovar.VariantAnnotation;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.io.File;
import java.io.IOException;
import java.util.Map;


public final class DatabaseBuilder {
    public final static String TAB = GP.TAB;

    public enum Population {EUR, EAS, AFR};
    public static final String AF = "_AF";

    private final String mafStr;
    private final DBSource dbSource;
    private final LDComputer ldComputer;
    private final DistanceComputer distanceComputer;
    private RoadmapAnnotation roadmapAnno;

    private final BlockCompressedInputStream in;
    private final TbiIndex index;
    private final TbiDatabase database;
    private static JannovarUtils jannovarUtils;
    private Map<String, Map<String, String>> variantAnnoMap;
    private TissueAnnotation tissueAnno;
    private GCAnnotation gcAnnotation;

    private final Gson gson = new Gson();
    private MyEndianOutputStream out;

    public DatabaseBuilder(final String srcFile, final Population popus) throws Exception {
        LoggingUtils.setLoggingLevel(Log.LogLevel.ERROR);

        this.dbSource = new DBSource(srcFile);
        this.mafStr = popus.toString() + AF;

        getVariantEffectResource();
        this.ldComputer = new LDComputer(dbSource.getVal(DBSource.BITFILE));
        this.ldComputer.setLdDistance(dbSource.getValInt(DBSource.LD_WINDOW));

        in = new BlockCompressedInputStream(new File(dbSource.getVal(DBSource.DB1000G)));

        loadJannovar();

        DBParam dbParam = new DBParam(dbSource.getVal(DBSource.DB1000G));
        dbParam.setIndexType(IndexType.TBI);
        database = (TbiDatabase)DatabaseFactory.readDatabase(dbParam);
        database.setVCFLocCodec(false);

        index = (TbiIndex)database.getIndex();

        distanceComputer = new DistanceComputer(dbSource.getVal(DBSource.GENCODE_GENE));
        roadmapAnno = new RoadmapAnnotation(dbSource.getVal(DBSource.ROADMAP));
        gcAnnotation = new GCAnnotation(dbSource.getVal(DBSource.GC_PATH));
    }

    public void getVariantEffectResource() throws IOException {
        if(variantAnnoMap == null) {
            variantAnnoMap = gson.fromJson(FileUtils.readFileToString(new File(dbSource.getVal(DBSource.VF_PATH)), "utf-8"), Map.class);
        }
    }

    public void getTissueAnno() throws IOException {
        if(tissueAnno == null) {
            final String tissuePath = dbSource.getVal(DBSource.TISSUE_PATH);
            String[] tissueList = FileUtils.readFileToString(new File(dbSource.getVal(DBSource.TISSUE_LIST)), "utf-8").split("\n");

            tissueAnno = new TissueAnnotation(tissueList, tissuePath);
        }
    }

    public void loadJannovar() {
        if(this.jannovarUtils == null) {
            this.jannovarUtils = new JannovarUtils(dbSource.getVal(DBSource.SERPATH));
            this.jannovarUtils.setGenome(GenomeAssembly.valueOf(dbSource.getVal(DBSource.GENOME)));
            this.jannovarUtils.setJannovarData();
        }
    }

    public String getMergeResult() {
        return dbSource.getVal(DBSource.OUT_DIR) + mafStr.replace(AF, "") ;
    }

    public void buildDatabase(final String chr) throws IOException {

        long count = 0;
        double mafCutoff = dbSource.getValDouble(DBSource.MAF_CUTOFF);
//        final long startTime = System.currentTimeMillis();
        out = new MyEndianOutputStream(new MyBlockCompressedOutputStream(getMergeResult() + chr + ".gz"));
        in.seek(index.getMinOffForChr(index.chr2tid(chr)));

        String line;
        LocFeature locFeature;
        VariantContext ctx;
        DBNode dbNode;

        loopA: while ((line = in.readLine()) != null) {
            if(line.charAt(0) != '#') {
                locFeature = database.decode(line);
                if(!locFeature.chr.equals(chr)) break;

                ctx = locFeature.variantContext;
                double mafval = ctx.getAttributeAsDouble(mafStr, -1);

                if(mafval != -1) {
                    mafval = mafval > 0.5 ? 1 - mafval : mafval;

                    if(mafval > mafCutoff) {
                        dbNode = processLocus(new LocFeature(locFeature.beg, locFeature.end, locFeature.chr, locFeature.ref, locFeature.alt), mafval);
                        if(dbNode != null) {
                            printNode(dbNode);
                        }

                        count++;
                        if(count % 10000 == 0) {
                            System.out.println(count ); //+ ", " + seconds
                        }
                    }
                } else {
                    System.out.println(locFeature + "\t" + mafval);
                }
            }
        }
        close();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        roadmapAnno.close();
        distanceComputer.close();
        ldComputer.close();
        tissueAnno.close();
        gcAnnotation.close();
    }

    public void printNode(final DBNode dbNode) throws IOException {
        final LocFeature loc = dbNode.getLocFeature();

        out.writeBytes(String.format("%s\t%d\t%s\t%s\t%f\t%d\t%s\t%s\t%s\t", loc.chr, (loc.beg + 1), loc.ref,
                loc.alt, dbNode.mafOrg, dbNode.dtct,
                StringUtils.join(dbNode.getGeneDensity(), TAB), StringUtils.join(dbNode.getGeneInLD(), TAB), StringUtils.join(dbNode.getLdBuddies(), TAB)));
        for (long[] cellType: dbNode.getCellMarks()) {
            out.writeBytes(StringUtils.join(cellType, ',') + TAB);
        }
        out.writeBytes(dbNode.getCategory() + TAB);
        out.writeBytes(dbNode.getTissueAnno() + TAB);
        out.writeBytes(StringUtils.join(dbNode.getGc(), ',') + "\n");
    }

    public DBNode processLocus(final LocFeature locFeature, final double mafval) throws IOException {
        final String chr = locFeature.chr;
        final int pos = locFeature.beg + 1;

        Pair<Integer[], Pair<Integer, Integer>[]> ldResult = ldComputer.compute(chr, pos, locFeature.ref, locFeature.alt);
        if(ldResult != null) {
            getTissueAnno();

            final int dtct = distanceComputer.computeDTCT(chr, pos);
            final Integer[] geneDensity = distanceComputer.computeGeneInDistance(chr, pos);
            final Integer[] geneInLD = distanceComputer.computeGeneInLD(chr, ldResult.getValue());
            final long[][] cellMarks = roadmapAnno.query(chr, pos);

            int category = -1;
            VariantAnnotation annotation = jannovarUtils.annotate(chr, pos, locFeature.ref, locFeature.alt);
            if(annotation != null) {
                Map<String, String> map = variantAnnoMap.get(annotation.getVariantEffect().toString());
                category = (int)(Double.parseDouble(map.get("category")));
            }

            long[] tissueArr = tissueAnno.getAnno(chr, pos, locFeature.ref, locFeature.alt).toLongArray();
            return new DBNode(locFeature, mafval, dtct, geneDensity, geneInLD, ldResult.getKey(), cellMarks, null, category, tissueArr.length > 0 ? tissueArr[0] : 0, gcAnnotation.getGCContent(chr, pos));
        } else {
            return null;
        }
    }
}
