package com.adamhedges.financial.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestPivots {

    @Test
    public void TestPivots_getPivots() {
        List<Double> values = new ArrayList<>();
        for (double d : Context.data) {
            values.add(d);
        }

        List<Integer> highs = Pivots.getPivots(values, (l, r) -> l > r);
        Assertions.assertEquals(List.of(1, 7, 12, 14), highs);

        List<Integer> lows = Pivots.getPivots(values, (l, r) -> l < r);
        Assertions.assertEquals(List.of(0, 3, 10, 13, 15), lows);
    }

    @Test
    public void TestPivots_getDispersion() {
        List<Double> values = new ArrayList<>();
        for (double d : Context.data) {
            values.add(d);
        }

        List<Integer> highPeaks = Pivots.getPivots(values, (l, r) -> l > r);
        List<Double> highDisp = Pivots.getDispersions(values, highPeaks, 2, (l, r) -> l - r);
        List<Double> expectedHighDisp = List.of(3.03, 8.0, 6.98, 4.77);

        Assertions.assertEquals(expectedHighDisp.size(), highDisp.size());
        for (int i = 0; i < highDisp.size(); i++) {
            Assertions.assertEquals(expectedHighDisp.get(i), highDisp.get(i), 0.001);
        }

        List<Integer> lowPeaks = Pivots.getPivots(values, (l, r) -> l < r);
        List<Double> lowDisp = Pivots.getDispersions(values, lowPeaks, 2, (l, r) -> (l - r) * -1);
        List<Double> expectedLowDisp = List.of(2.12, 6.92, 9.93, 3.90, 4.77);

        Assertions.assertEquals(expectedLowDisp.size(), lowDisp.size());
        for (int i = 0; i < highDisp.size(); i++) {
            Assertions.assertEquals(expectedLowDisp.get(i), lowDisp.get(i), 0.001);
        }
    }

    @Test
    public void TestPivots_consolidatePivots() {
        List<Double> values = new ArrayList<>();
        for (double d : Context.data) {
            values.add(d);
        }

        List<Integer> highPivots = Pivots.getPivots(values, (l, r) -> l > r);
        List<Double> highDispersions = Pivots.getDispersions(values, highPivots, 2, (l, r) -> l - r);

        List<Integer> lowPivots = Pivots.getPivots(values, (l, r) -> l < r);
        List<Double> lowDispersions = Pivots.getDispersions(values, lowPivots, 2, (l, r) -> (l - r) * -1);

        List<Integer> consolidatedHighs = Pivots.consolidatePivots(highPivots, highDispersions, 3);
        List<Integer> consolidatedLows = Pivots.consolidatePivots(lowPivots, lowDispersions, 3);

        List<Integer> expectedHighs = List.of(1, 7, 12);
        List<Integer> expectedLows = List.of(3, 10, 15);

        Assertions.assertEquals(3, consolidatedHighs.size());
        Assertions.assertEquals(expectedHighs, consolidatedHighs);

        Assertions.assertEquals(3, consolidatedLows.size());
        Assertions.assertEquals(expectedLows, consolidatedLows);
    }

    @Test
    public void TestPivots_get() {
        List<Double> values = new ArrayList<>();
        for (double d : Context.data) {
            values.add(d);
        }

        List<Integer> consolidatedHighs = Pivots.get(values, (l, r) -> l > r, (l, r) -> l - r, 2, 3);
        List<Integer> expectedHighs = List.of(1, 7, 12);

        Assertions.assertEquals(expectedHighs.size(), consolidatedHighs.size());
        Assertions.assertEquals(expectedHighs, consolidatedHighs);
    }

}
