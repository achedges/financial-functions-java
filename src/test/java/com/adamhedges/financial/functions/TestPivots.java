package com.adamhedges.financial.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPivots {

    @Test
    public void TestPivots_getPivots() {
        List<Double> values = Arrays.stream(Context.pivots).boxed().toList();

        List<Integer> highPivots = Pivots.getPivots(values, (l, r) -> l >= r);
        Assertions.assertEquals(Context.expectedHighPivots, highPivots);

        List<Integer> lows = Pivots.getPivots(values, (l, r) -> l <= r);
        Assertions.assertEquals(Context.expectedLowPivots, lows);
    }

    @Test
    public void TestPivots_getDispersion() {
        List<Double> values = Arrays.stream(Context.pivots).boxed().toList();
        int span = values.size() / Pivots.DEFAULT_DISPERSION_GROUPS;

        List<Integer> highPivots = Pivots.getPivots(values, (l, r) -> l >= r);
        List<Double> highDispersions = Pivots.getDispersions(values, highPivots, span, (l, r) -> l - r);

        Assertions.assertEquals(Context.expectedHighDispersions.size(), highDispersions.size());
        for (int i = 0; i < highDispersions.size(); i++) {
            Assertions.assertEquals(Context.expectedHighDispersions.get(i), highDispersions.get(i), 0.00001);
        }

        List<Integer> lowPivots = Pivots.getPivots(values, (l, r) -> l <= r);
        List<Double> lowDispersions = Pivots.getDispersions(values, lowPivots, span, (l, r) -> r - l);

        Assertions.assertEquals(Context.expectedLowDispersions.size(), lowDispersions.size());
        for (int i = 0; i < lowDispersions.size(); i++) {
            Assertions.assertEquals(Context.expectedLowDispersions.get(i), lowDispersions.get(i), 0.00001);
        }
    }

    @Test
    public void TestPivots_consolidatePivots() {
        List<Double> values = Arrays.stream(Context.pivots).boxed().toList();
        int dispersionSpan = values.size() / Pivots.DEFAULT_DISPERSION_GROUPS;
        int consolidationSpan = values.size() / Pivots.DEFAULT_CONSOLIDATION_GROUPS;

        List<Integer> highPivots = Pivots.getPivots(values, (l, r) -> l >= r);
        List<Double> highDispersions = Pivots.getDispersions(values, highPivots, dispersionSpan, (l, r) -> l - r);

        List<Integer> lowPivots = Pivots.getPivots(values, (l, r) -> l <= r);
        List<Double> lowDispersions = Pivots.getDispersions(values, lowPivots, dispersionSpan, (l, r) -> r - l);

        List<Integer> consolidatedHighs = Pivots.consolidatePivots(highPivots, highDispersions, consolidationSpan);
        List<Integer> consolidatedLows = Pivots.consolidatePivots(lowPivots, lowDispersions, consolidationSpan);

        Assertions.assertEquals(Context.expectedHighConsolidations, consolidatedHighs);
        Assertions.assertEquals(Context.expectedLowConsolidations, consolidatedLows);
    }

    @Test
    public void TestPivots_get() {
        List<Double> values = new ArrayList<>();
        for (double d : Context.data) {
            values.add(d);
        }

        List<Integer> consolidatedHighs = Pivots.get(values, (l, r) -> l > r, (l, r) -> l - r, 2, 3);
        List<Integer> expectedHighs = List.of(1, 7, 12);
        Assertions.assertEquals(expectedHighs, consolidatedHighs);

        // test default behavior
        consolidatedHighs = Pivots.get(values, (l, r) -> l > r, (l, r) -> l - r);
        Assertions.assertEquals(expectedHighs, consolidatedHighs);
    }

}
