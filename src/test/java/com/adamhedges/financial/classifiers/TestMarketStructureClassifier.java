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
    public void TestMarketStructureClassifier_init() {
        MarketStructureClassifier classifier = new MarketStructureClassifier();
        Assertions.assertEquals(0.0005, classifier.getStrongTrendThreshold());

        MarketStructureClassifier classifier2 = new MarketStructureClassifier(1.0);
        Assertions.assertEquals(1.0, classifier2.getStrongTrendThreshold());
    }

    @Test
    public void TestMarketStructureClassifier_getLastHighPivot() {
        MarketStructureClassifier classifier = new MarketStructureClassifier();

        Assertions.assertTrue(classifier.getLastHighPivot().isEmpty());
        Assertions.assertTrue(classifier.getLastLowPivot().isEmpty());

        List<PriceBar> bars = getBars();
        // generate an ambiguous structure
        bars.get(4).setHigh(24.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(16.0);
        bars.get(18).setLow(18.0);
        classifier.classifyMarketStructure(bars);

        Assertions.assertEquals(22.0, classifier.getLastHighPivot().orElse(new PriceBar("")).getHigh(), 0.01);
        Assertions.assertEquals(18.0, classifier.getLastLowPivot().orElse(new PriceBar("")).getLow());
    }

    @Test
    public void TestMarketStructureClassifier_Flat() {
        List<PriceBar> bars = getBars();
        MarketStructureClassifier classifier = new MarketStructureClassifier(1.0);

        // base case, not enough pivots identified
        TrendClassification trend = classifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Flat, trend);

        // generate a perfectly flat structure
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(18.0);
        trend = classifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Flat, trend);

        // generate an ambiguous structure
        bars.get(4).setHigh(24.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(16.0);
        bars.get(18).setLow(18.0);
        trend = classifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Flat, trend);
    }

    @Test
    public void TestMarketStructureClassifier_WeakUp() {
        MarketStructureClassifier classifier = new MarketStructureClassifier(1.0);
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(22.95); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(18.95); // just under 1.0
        TrendClassification trend = classifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.WeakUp, trend);
    }

    @Test
    public void TestMarketStructureClassifier_WeakDown() {
        MarketStructureClassifier classifier = new MarketStructureClassifier(1.0);
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(21.05); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(17.05); // just under 1.0
        TrendClassification trend = classifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.WeakDown, trend);
    }

    @Test
    public void TestMarketStructureClassifier_StrongUp() {
        MarketStructureClassifier classifier = new MarketStructureClassifier(1.0);
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(23.05); // just over 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(19.05); // just under 1.0
        TrendClassification trend = classifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.StrongUp, trend);
    }

    @Test
    public void TestMarketStructureClassifier_StrongDown() {
        MarketStructureClassifier classifier = new MarketStructureClassifier(1.0);
        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(20.95); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(16.95); // just under 1.0
        TrendClassification trend = classifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.StrongDown, trend);
    }

}
