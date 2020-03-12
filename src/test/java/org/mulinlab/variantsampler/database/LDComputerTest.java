package org.mulinlab.variantsampler.database;

import org.junit.Test;
import org.mulinlab.variantsampler.utils.DBSource;
import org.mulinlab.variantsampler.utils.Pair;
import org.mulinlab.varnote.filters.iterator.NoFilterIterator;
import org.mulinlab.varnote.utils.enumset.FileType;
import org.mulinlab.varnote.utils.gz.MyBlockCompressedOutputStream;
import org.mulinlab.varnote.utils.gz.MyEndianOutputStream;

import java.io.IOException;

public class LDComputerTest {

    DBSource dbSource = new DBSource("src/main/resources/db.ini");

    public LDComputerTest() {
    }

    @Test
    public void compute() {
        try {
            LDComputer computer = new LDComputer(dbSource.getVal(DBSource.BITFILE));
//            Pair<Integer[], Pair<Integer, Integer>[]> pair = computer.compute("9", 5453460, "G", "A");
//            System.out.println(pair.getKey());


            Pair<Integer[], Pair<Integer, Integer>[]> pair1 = computer.compute("1", 10177, "A", "AC");
            System.out.println(pair1.getKey());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}