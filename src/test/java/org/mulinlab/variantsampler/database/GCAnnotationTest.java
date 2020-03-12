package org.mulinlab.variantsampler.database;

import org.junit.Test;
import org.mulinlab.variantsampler.utils.DBSource;
import java.io.IOException;


public class GCAnnotationTest {

    @Test
    public void getGC() throws IOException {
        DBSource dbSource = new DBSource("src/main/resources/db.ini");
        final String gcPath = dbSource.getVal(DBSource.GC_PATH);

        GCAnnotation gcAnnotation = new GCAnnotation(gcPath);
        int[] gc = gcAnnotation.getGCContent("1", 10177);

        for (int i = 0; i < gc.length; i++) {
            System.out.println(gc[i]);
        }
        gcAnnotation.close();
    }
}