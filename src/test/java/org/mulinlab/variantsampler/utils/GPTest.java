package org.mulinlab.variantsampler.utils;

import org.junit.Test;
import org.mulinlab.variantsampler.utils.node.Node;

import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class GPTest {

    @Test
    public void generateRandomArray() {
        Random r = new Random();
        r.setSeed(10);

        int[] arr = GP.generateRandomArray(10, 5, r);

        for (int n:arr) {
            System.out.print(n + " ");
        }

        System.out.println("\n");

        arr = GP.generateRandomArray(10, 11, r);

        for (int n:arr) {
            System.out.print(n + " ");
        }

        System.out.println("\n");
    }
}