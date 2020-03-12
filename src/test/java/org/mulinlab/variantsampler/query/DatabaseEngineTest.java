package org.mulinlab.variantsampler.query;

import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

public class DatabaseEngineTest {

    @Test
    public void randomSelect() {

        final Random r = new Random();

        for (int i = 0; i < 300000; i++) {
            int index = r.nextInt(3324237);
            int maf = r.nextInt(50);
            int chr = r.nextInt(22);

            long addr = (((long)index << 16) | (maf << 8) | chr);


            int index1 = (int)(addr >> 16 & 0xFFFFFFFFFFFFL);
            int maf1 = (int) addr >> 8 & 0xff;
            int chr1 = (int)addr & 0xff;

            assertEquals(index1, index);
            assertEquals(maf1, maf);
            assertEquals(chr1, chr);
        }
    }

    @Test
    public void randomSelect1() {

        final Random r = new Random();

        for (int i = 0; i < 300000; i++) {
            int val1 = r.nextInt(32000);
            int val2 = r.nextInt(32000);

            int addr = ((val1 << 16) | val2);

            int val11 = addr >> 16 & 0xFFFF;
            int val12 = addr & 0xFFFF;

            assertEquals(val1, val11);
            assertEquals(val12, val12);
        }
    }
}