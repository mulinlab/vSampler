package org.mulinlab.variantsampler.index;

import org.junit.Test;
import org.mulinlab.variantsampler.utils.enumset.CellType;
import org.mulinlab.variantsampler.utils.node.DBNode;
import org.mulinlab.varnote.operations.readers.itf.BGZReader;

import java.io.IOException;
import java.util.BitSet;


public class IndexMafTest {

    @Test
    public void getMafList() throws IOException {
        IndexMaf indexMaf = new IndexMaf("/hg19/EUR.gz");
    }

}