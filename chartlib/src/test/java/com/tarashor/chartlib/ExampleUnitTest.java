package com.tarashor.chartlib;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        int[] a = new int[]{1,2,5,10,40,50};

        assertEquals(2, Arrays.binarySearch(a, 5));
        assertEquals(4, -Arrays.binarySearch(a, 11) - 1);
    }
}