package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestMarketStructureClassifier {

    private final Random random = new Random(1234);

    private List<PriceBar> getBars() {
        List<PriceBar> bars = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            bars.add(new PriceBar("TEST", 20.0 + random.nextDouble()));
        }
        return bars;
    }

    @Test
    public void TestMarketStructureClassifier_Flat() {
        List<PriceBar> bars = getBars();

        // base case, not enough pivots identified
        TrendClassification trend = MarketStructureClassifier.classifyMarketStructure(bars, 1.0);
        Assertions.assertEquals(TrendClassification.Flat, trend);

        // generate a perfectly flat structure
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(18.0);
        trend = MarketStructureClassifier.classifyMarketStructure(bars, 1.0);
        Assertions.assertEquals(TrendClassification.Flat, trend);

        // generate an ambiguous structure
        bars.get(4).setHigh(24.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(16.0);
        bars.get(18).setLow(18.0);
        trend = MarketStructureClassifier.classifyMarketStructure(bars, 1.0);
        Assertions.assertEquals(TrendClassification.Flat, trend);
    }

    @Test
    public void TestMarketStructureClassifier_WeakUp() {
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(22.95); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(18.95); // just under 1.0
        TrendClassification trend = MarketStructureClassifier.classifyMarketStructure(bars, 1.0);
        Assertions.assertEquals(TrendClassification.WeakUp, trend);
    }

    @Test
    public void TestMarketStructureClassifier_WeakDown() {
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(21.05); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(17.05); // just under 1.0
        TrendClassification trend = MarketStructureClassifier.classifyMarketStructure(bars, 1.0);
        Assertions.assertEquals(TrendClassification.WeakDown, trend);
    }

    @Test
    public void TestMarketStructureClassifier_StrongUp() {
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(23.05); // just over 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(19.05); // just under 1.0
        TrendClassification trend = MarketStructureClassifier.classifyMarketStructure(bars, 1.0);
        Assertions.assertEquals(TrendClassification.StrongUp, trend);
    }

    @Test
    public void TestMarketStructureClassifier_StrongDown() {
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(20.95); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(16.95); // just under 1.0
        TrendClassification trend = MarketStructureClassifier.classifyMarketStructure(bars, 1.0);
        Assertions.assertEquals(TrendClassification.StrongDown, trend);
    }

}
