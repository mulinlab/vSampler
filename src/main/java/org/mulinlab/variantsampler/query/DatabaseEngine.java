package org.mulinlab.variantsampler.query;

import org.mulinlab.variantsampler.utils.GP;
import org.mulinlab.variantsampler.utils.SamplerWrite;
import org.mulinlab.variantsampler.utils.node.Node;
import org.mulinlab.variantsampler.utils.QueryParam;
import org.mulinlab.varnote.config.param.DBParam;
import org.mulinlab.varnote.operations.decode.TABLocCodec;
import org.mulinlab.varnote.operations.readers.db.VannoMixReader;
import org.mulinlab.varnote.operations.readers.db.VannoReader;
import org.mulinlab.varnote.utils.database.DatabaseFactory;
import org.mulinlab.varnote.utils.enumset.IntersectType;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.io.IOException;
import java.util.*;

public class DatabaseEngine {
    private static final int IN = Compare.IN;

    private final Random r = new Random();
    private final Compare compare;


    private static final int NO_MAF = -1;
    private final MafReader mafReader;
    private final VannoReader reader;

    private final QueryParam queryParam;
    private final TABLocCodec tabLocCodec;

    private Map<Integer, Map<Integer, Node[]>> chrMafMap;
    private boolean hasRefAlt = false;
    private InputStack inputStack;
    private long[] results;

    private int MAXLEN = 10000;
    public DatabaseEngine(final String dbPath, final InputStack inputStack, final QueryParam queryParam) throws IOException {
        final DBParam dbParam = new DBParam(dbPath);
        dbParam.setIntersect(IntersectType.INTERSECT);

        this.queryParam = queryParam;
        this.hasRefAlt = queryParam.getFormat().isRefAndAltExsit();

        this.reader = new VannoMixReader(DatabaseFactory.readDatabase(dbParam));
        this.mafReader = new MafReader(dbPath, hasRefAlt);

        this.tabLocCodec = GP.getDefaultDecode( false);
        this.chrMafMap = new HashMap<>();
        this.inputStack = inputStack;

        if(queryParam.getRandomSeed() != -1) {
            r.setSeed(queryParam.getRandomSeed());
        }

        results = new long[MAXLEN];
        this.compare = new Compare();
    }

    public void close() throws IOException {
        this.reader.close();
        this.mafReader.close();
    }

    public Map<Integer, List<Node>> convertQuery(final List<LocFeature> querys) throws IOException{
        Map<Integer, List<Node>> map = new HashMap<>();
        List<Node> mafList;

        Node node;
        int maf ;

        LocFeature match = null;
        for (LocFeature locFeature: querys) {
            match = null;
            reader.query(new LocFeature(locFeature.beg, locFeature.beg + 1, locFeature.chr));

            if(reader.getResults().size() > 0) {
                match = getMatchAnno(reader.getResults(), locFeature);
            }

            if(match != null) {
                node = new Node(match, queryParam);
                maf = node.maf;
            } else {
                node = new Node(locFeature);
                maf = NO_MAF;
            }

            if(map.get(maf) == null) {
                mafList = new ArrayList<>();
            } else {
                mafList = map.get(node.maf);
            }
            mafList.add(node);
            map.put(maf, mafList);
        }

        return map;
    }

    private LocFeature getMatchAnno(List<String> results, LocFeature query) {
        LocFeature db;

        for (String r:results) {
            db = tabLocCodec.decode(r);
            if(InputStack.getValue(db, hasRefAlt) == query.end) return db;
        }
        return null;
    }

    public void findNode(Node node, SamplerWrite samplerWrite) throws IOException {
        if (queryParam.isCrossChr()) {
            findNodeCrossChr(node, samplerWrite);
        } else {
            findNodeInChr(node, samplerWrite);
        }
    }

    public void findNodeCrossChr(Node node, SamplerWrite samplerWrite) throws IOException {

        int c = 0;
        if(node.hasAnno > 0) {
            compare.setQuery(node, queryParam);
            for (int i = GP.FROM_CHROM; i <= GP.TO_CHROM ; i++) {
                c = countInChr(i, node, c);
            }
        }

        printQuery(node, c, samplerWrite);
    }

    public void findNodeInChr(Node node, SamplerWrite samplerWrite) throws IOException {
        int chrID = Integer.parseInt(node.getChr());

        removeUselessChr(chrID);

        int c = 0;
        if(node.hasAnno > 0) {
            compare.setQuery(node, queryParam);
            c = countInChr(chrID, node, c);
        }

        printQuery(node, c, samplerWrite);
    }

    public int countInChr(Integer chr, Node query, int index) throws IOException {
        Node[] dnNodes;
        Node db;

        Map<Integer, Node[]> mafMap = getMafMap(chr);
        removeUselessMaf(query.maf);

        for (int i = getMin(query.maf); i <= getMax(query.maf); i++) {
            if(mafMap.get(i) == null) {
                mafMap.put(i, mafReader.loadMAF(String.valueOf(chr), i, queryParam, inputStack));
                chrMafMap.put(chr, mafMap);
            }

            dnNodes = mafMap.get(i);
            if(dnNodes != null) {
                for (int j = 0; j < dnNodes.length; j++) {
                    db = dnNodes[j];

                    if(isMatch(query, db)) {
                        if(index == MAXLEN) {
                            grow();
                        }
                        results[index++] = (((long)j << 16) | (i << 8) | chr);
                    }
                }
            }
        }
        return index;
    }

    private void grow() {
        int oldCapacity = MAXLEN;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        MAXLEN = newCapacity;
        results = Arrays.copyOf(results, newCapacity);
    }

    private void printQuery(Node q, int size, SamplerWrite samplerWrite) throws IOException {
        int[] samplerArr = null;
        if(size > 0) {
            q.poolSize = size;
            samplerArr = GP.generateRandomArray(size, queryParam.getSamplerNumber(), r);
        }

        if(q.hasAnno > 0) {
            StringBuffer s = new StringBuffer();
            s.append(q.poolSize + ":" + queryParam.getSamplerNumber());
            s.append(GP.TAB);
            s.append(q.briefQuery(hasRefAlt));
            s.append(GP.TAB);
            s.append(q.briefAnno());
            s.append(GP.TAB);

            samplerWrite.printAnno(q, SamplerWrite.QUERY);
            for (int i = 1; i <= queryParam.getSamplerNumber(); i++) {
                if(samplerArr == null) {
                    s.append("-");
                } else {
                    int num = samplerArr[i-1];

                    Node dbNode = getNodeByAddr(results[num]);
                    s.append(dbNode.briefAnno());

                    if(i <= queryParam.getAnnoNumber()) {
                        printAnno(dbNode, i, samplerWrite);
                    }
                }

                if(i < queryParam.getSamplerNumber()) {
                    s.append(GP.TAB);
                }
            }
            samplerWrite.printSampler(q, s.toString());
        } else {
            samplerWrite.printSampler(q, q.briefQuery(false));
        }
    }

    public void printAnno(Node dbNode, int i, SamplerWrite samplerWrite) throws IOException {
        samplerWrite.printAnno(dbNode, SamplerWrite.CONTROL + i);
    }

    private int getMin(int maf) {
        return queryParam.getMafRangeMin(maf);
    }

    private int getMax(int maf) {
        return queryParam.getMafRangeMax(maf);
    }

    private Map<Integer, Node[]> getMafMap(Integer chr) {
        Map<Integer, Node[]> mafMap = chrMafMap.get(chr);
        if(mafMap == null) {
            return new HashMap<>();
        } else {
            return mafMap;
        }
    }

    private boolean isMatch(Node q, Node db) {
        if(db.isInQuery) {
            return false;
        }

        if(queryParam.variantTypeSpecific && q.isIndel != db.isIndel) {
            return false;
        }

        if(compare.isInRangeMAFOri(db.mafOrg) == IN) {
            if(queryParam.getDisRange() != null && compare.isInRangeDistance(db.dtct) != IN) {
                return false;
            }
            if(queryParam.geneDisIndex != GP.NO_GENE_DIS && compare.isInRangeGene(db.geneDis) != IN) {
                return false;
            }
            if(queryParam.geneLDIndex != GP.NO_GENE_LD && compare.isInRangeGene(db.geneLD) != IN) {
                return false;
            }
            if(queryParam.ldIndex != GP.NO_LD_BUDDIES && compare.isInRangeLDBuddies(db.ldBuddies) != IN) {
                return false;
            }
            if(queryParam.hasCellMarker && (q.roadmap != db.roadmap)) {
                return false;
            }
            if(queryParam.variantTypeMatch && (q.cat != db.cat)) {
                return false;
            }
            if(queryParam.tissueIdx!= GP.NO_TISSUE && (q.tissue != db.tissue)) {
                return false;
            }
            if(queryParam.gcRange != null && compare.isInRangeGC(db.gc) != IN) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private Node getNodeByAddr(Long addr) throws IOException {
        return chrMafMap.get((int)(addr & 0xff)).get((int)((addr >> 8) & 0xff))[(int)((addr >> 16) & 0xFFFFFFFFFFFFL)];
    }

    private void removeUselessChr(final Integer chr) {
        for (Integer key: chrMafMap.keySet()) {
            if(key != chr) {
                chrMafMap.put(key, null);
            }
        }
    }

    private void removeUselessMaf(final int maf) {
        Map<Integer, Node[]> mafMap;

        for (Integer chr: chrMafMap.keySet()) {

            mafMap = chrMafMap.get(chr);
            if(mafMap != null) {
                Iterator<Map.Entry<Integer, Node[]>> iter = mafMap.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<Integer, Node[]> entry = iter.next();
                    if(isInRangeMAF(maf, entry.getKey()) != IN) {
                        iter.remove();
                    }
                }
            }
        }
    }

    private int isInRangeMAF(final int nodeMaf, final int dbmaf) {
        if(dbmaf < queryParam.getMafRangeMin(nodeMaf)) {
            return Compare.LESS;
        } else if(dbmaf > queryParam.getMafRangeMax(nodeMaf)) {
            return Compare.GREATER;
        } else {
            return IN;
        }
    }



}
