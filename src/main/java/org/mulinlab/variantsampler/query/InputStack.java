package org.mulinlab.variantsampler.query;

import org.mulinlab.variantsampler.utils.node.Node;
import org.mulinlab.variantsampler.utils.sort.BegSort;
import org.mulinlab.varnote.filters.iterator.LineFilterIterator;
import org.mulinlab.varnote.utils.node.LocFeature;
import java.util.*;

public class InputStack {

    private List<String> chrList;
//    private Map<String, Integer> chr2idMap;
    private Map<String, Map<Integer, Integer>> inputs;
    private Map<String, Map<Integer, List<Integer>>> inputsMultiple;

    private int count = 0;
    private final boolean hasRefAlt;

    public InputStack(final LineFilterIterator iterator, final boolean hasRefAlt) {

        this.hasRefAlt = hasRefAlt;
        chrList = new ArrayList<>();
        inputs = new HashMap<>();
        inputsMultiple = new HashMap<>();

        Map<Integer, Integer> chrMap;
        Map<Integer, List<Integer>> chrMultiMap;

        LocFeature locFeature = null;

        while (iterator.hasNext()) {
            locFeature = iterator.next();

            if(locFeature != null) {
                locFeature.chr = locFeature.chr.toLowerCase().replace("chr", "");
                count++;

                if(inputs.get(locFeature.chr) == null) {
                    chrMap = new HashMap<>();
                    chrMultiMap = new HashMap<>();
                    chrList.add(locFeature.chr);
                } else {
                    chrMap = inputs.get(locFeature.chr);
                    chrMultiMap = inputsMultiple.get(locFeature.chr);
                }

                if(chrMap.get(locFeature.beg) == null) {
                    chrMap.put(locFeature.beg, getValue(locFeature, hasRefAlt));
                } else {
                    List<Integer> ends = chrMultiMap.get(locFeature.beg);
                    if(ends == null) {
                        ends = new ArrayList<>();
                        ends.add(getValue(locFeature, hasRefAlt));
                    }
                    chrMultiMap.put(locFeature.beg, ends);
                }
                inputs.put(locFeature.chr, chrMap);
                inputsMultiple.put(locFeature.chr, chrMultiMap);
            }
        }
        Collections.sort(chrList);
//
//        chr2idMap = new HashMap<>();
//        for (int i = 0; i < chrList.size(); i++) {
//            chr2idMap.put(chrList.get(i), i);
//        }
        iterator.close();
    }

    public static int getValue(final LocFeature locFeature, final boolean hasRefAlt) {
        if(!hasRefAlt) {
            return locFeature.end - locFeature.beg;
        } else {
            int baseCount = 0;
            for (int i = 0; i < locFeature.ref.length(); i++) {
                baseCount += (byte)locFeature.ref.charAt(i) * 2;
            }

            for (int i = 0; i < locFeature.alt.length(); i++) {
                baseCount += (byte)locFeature.alt.charAt(i);
            }
            return baseCount;
        }
    }

    public int getCount() {
        return count;
    }

    public List<LocFeature> getLocFeatureForChr(final List<LocFeature> alllist, final String chr) {
        Map<Integer, Integer> locs = inputs.get(chr);
        Map<Integer, List<Integer>> mlocs = inputsMultiple.get(chr);

        List<LocFeature> list = new ArrayList<>();
        for (Integer loc: locs.keySet()) {
            list.add(new LocFeature(loc,  locs.get(loc), chr));
        }

        for (Integer loc: mlocs.keySet()) {
            for (Integer end: mlocs.get(loc)) {
                list.add(new LocFeature(loc,  end, chr));
            }
        }

        Collections.sort(list, new BegSort());
        for (LocFeature locFeature: list) {
            alllist.add(locFeature);
        }
        return list;
    }

    public List<LocFeature> getLocFeatureForAll(final List<LocFeature> list) {
        for (String chr: inputs.keySet()) {
            getLocFeatureForChr(list, chr);
        }

        return list;
    }

    public List<String> getChrList() {
        return chrList;
    }

    public boolean isNodeInQuery(Node node, final boolean hasRefAlt) {
        LocFeature locFeature = node.getLocFeature();
        int val = getValue(locFeature, hasRefAlt);

        Map<Integer, Integer> chrMap = inputs.get(locFeature.chr);
        Map<Integer, List<Integer>> chrMultiMap = inputsMultiple.get(locFeature.chr);

        if(chrMap != null && chrMap.get(locFeature.beg) != null && chrMap.get(locFeature.beg) == val) {
            return true;
        } else {
            if(chrMultiMap == null) return false;
            List<Integer> ends = chrMultiMap.get(locFeature.beg);
            if(ends != null) {
                for (Integer end: ends) {
                    if(end == val) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

//    public int chr2id(final String chr) {
//        return chr2idMap.get(chr);
//    }
}
