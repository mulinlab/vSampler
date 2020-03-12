package org.mulinlab.variantsampler.database;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mulinlab.variantsampler.utils.DBSource;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class TissueAnnotationTest {

    @Test
    public void getAnno() throws IOException {
        DBSource dbSource = new DBSource("src/main/resources/db.ini");
        final String tissuePath = dbSource.getVal(DBSource.TISSUE_PATH);


        String str = FileUtils.readFileToString(new File(dbSource.getVal(DBSource.TISSUE_LIST)), "utf-8");
        String[] tissueList = str.split("\n");

        for (String t:tissueList) {
            System.out.print(t.toUpperCase() + ",");
        }

        TissueAnnotation tissueAnnotation = new TissueAnnotation(tissueList, tissuePath);
        BitSet bitSet = tissueAnnotation.getAnno("1", 14677, "G", "A");

        for (int i = 0; i < tissueList.length; i++) {
            System.out.print((bitSet.get(i) ? 1 : 0 )+ " ") ;
        }

        long a = bitSet.toLongArray()[0];
        System.out.println(a);
        bitSet = BitSet.valueOf(new long[]{a});
        for (int i = 0; i < tissueList.length; i++) {
            System.out.print((bitSet.get(i) ? 1 : 0 )+ " ") ;
        }
        System.out.println("");
    }
}