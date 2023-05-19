package com.mcquest.server.commerce;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    public void testCopperPart() {
        int[] values = {0, 27, 160, 1438, 34781324};
        int[] expected = {0, 27, 60, 38, 24};
        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i], new Money(values[i]).getCopperPart());
        }
    }

    @Test
    public void testSilverPart() {
        int[] values = {0, 27, 160, 1438, 34781324};
        int[] expected = {0, 0, 1, 14, 13};
        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i], new Money(values[i]).getSilverPart());
        }
    }

    @Test
    public void testGoldPart() {
        int[] values = {0, 27, 160, 10000, 14438, 34781324};
        int[] expected = {0, 0, 0, 1, 1, 3478};
        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i], new Money(values[i]).getGoldPart());
        }
    }
}
