package org.mulinlab.variantsampler.database;

import org.junit.Test;
import org.mulinlab.variantsampler.utils.RunFactory;

public class DatabaseBuilderTest {

    @Test
    public void testBuildDatabase() throws Exception {
        RunFactory.buildDatabase("src/main/resources/db.ini", DatabaseBuilder.Population.EUR, 1);
    }

}