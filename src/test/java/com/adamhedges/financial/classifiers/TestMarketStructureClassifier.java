package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Assertions.assertEquals(0.25, classifier.getStrongTrendThreshold());
        Assertions.assertEquals(MarketStructureClassifier.ThresholdQualifier.Slope, classifier.getThresholdQualifier());

        MarketStructureClassifier classifier2 = new MarketStructureClassifier(0.002, MarketStructureClassifier.ThresholdQualifier.Magnitude);
        Assertions.assertEquals(0.002, classifier2.getStrongTrendThreshold());
        Assertions.assertEquals(MarketStructureClassifier.ThresholdQualifier.Magnitude, classifier2.getThresholdQualifier());
    }

    @Test
    public void TestMarketStructureClassifier_getLastHighLowPivot() {
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
    public void TestMarketStructureClassifier_getLastHighLowPivotIndex() {
        MarketStructureClassifier classifier = new MarketStructureClassifier();

        Assertions.assertTrue(classifier.getLastHighPivotIndex().isEmpty());
        Assertions.assertTrue(classifier.getLastLowPivotIndex().isEmpty());

        List<PriceBar> bars = getBars();
        // generate an ambiguous structure
        bars.get(4).setHigh(24.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(16.0);
        bars.get(18).setLow(18.0);
        classifier.classifyMarketStructure(bars);

        Assertions.assertEquals(Optional.of(14), classifier.getLastHighPivotIndex());
        Assertions.assertEquals(Optional.of(18), classifier.getLastLowPivotIndex());
    }

    @Test
    public void TestMarketStructureClassifier_Flat() {
        List<PriceBar> bars = getBars();
        MarketStructureClassifier slopeClassifier = new MarketStructureClassifier(1.0, MarketStructureClassifier.ThresholdQualifier.Slope);
        MarketStructureClassifier magnitudeClassifier = new MarketStructureClassifier(1.0, MarketStructureClassifier.ThresholdQualifier.Magnitude);

        // base case, not enough pivots identified
        TrendClassification trend = slopeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Mixed, trend);

        // generate a perfectly flat structure
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(18.0);

        trend = slopeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Mixed, trend);
        Assertions.assertEquals(0.0, slopeClassifier.getHighPivotDiff());
        Assertions.assertEquals(0.0, slopeClassifier.getLowPivotDiff());

        trend = magnitudeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Mixed, trend);
        Assertions.assertEquals(0.0, magnitudeClassifier.getHighPivotDiff());
        Assertions.assertEquals(0.0, magnitudeClassifier.getLowPivotDiff());

        // generate an ambiguous structure
        bars.get(4).setHigh(24.0);
        bars.get(14).setHigh(22.0);
        bars.get(8).setLow(16.0);
        bars.get(18).setLow(18.0);

        trend = slopeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Mixed, trend);
        Assertions.assertEquals(-0.95, slopeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(0.76, slopeClassifier.getLowPivotDiff(), 0.01);

        trend = magnitudeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.Mixed, trend);
        Assertions.assertEquals(-2.0, magnitudeClassifier.getHighPivotDiff());
        Assertions.assertEquals(2.0, magnitudeClassifier.getLowPivotDiff());
    }

    @Test
    public void TestMarketStructureClassifier_WeakUp() {
        MarketStructureClassifier slopeClassifier = new MarketStructureClassifier(1.0, MarketStructureClassifier.ThresholdQualifier.Slope);
        MarketStructureClassifier magnitudeClassifier = new MarketStructureClassifier(1.0, MarketStructureClassifier.ThresholdQualifier.Magnitude);

        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(22.95); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(18.95); // just under 1.0

        TrendClassification trend = slopeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.WeakUp, trend);
        Assertions.assertEquals(0.61, slopeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(0.61, slopeClassifier.getLowPivotDiff(), 0.01);

        trend = magnitudeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.WeakUp, trend);
        Assertions.assertEquals(0.95, magnitudeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(0.95, magnitudeClassifier.getLowPivotDiff(), 0.01);
    }

    @Test
    public void TestMarketStructureClassifier_WeakDown() {
        MarketStructureClassifier slopeClassifier = new MarketStructureClassifier(1.0, MarketStructureClassifier.ThresholdQualifier.Slope);
        MarketStructureClassifier magnitudeClassifier = new MarketStructureClassifier(1.0, MarketStructureClassifier.ThresholdQualifier.Magnitude);

        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(21.05); // just under 1.0
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(17.05); // just under 1.0

        TrendClassification trend = slopeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.WeakDown, trend);
        Assertions.assertEquals(-0.91, slopeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(-0.46, slopeClassifier.getLowPivotDiff(), 0.01);

        trend = magnitudeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.WeakDown, trend);
        Assertions.assertEquals(-0.95, magnitudeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(-0.95, magnitudeClassifier.getLowPivotDiff(), 0.01);
    }

    @Test
    public void TestMarketStructureClassifier_StrongUp() {
        MarketStructureClassifier slopeClassifier = new MarketStructureClassifier(0.1, MarketStructureClassifier.ThresholdQualifier.Slope);
        MarketStructureClassifier magnitudeClassifier = new MarketStructureClassifier(0.1, MarketStructureClassifier.ThresholdQualifier.Magnitude);

        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(23.05); // just over 0.1
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(19.05); // just under 0.1

        TrendClassification trend = slopeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.StrongUp, trend);
        Assertions.assertEquals(0.65, slopeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(0.67, slopeClassifier.getLowPivotDiff(), 0.01);

        trend = magnitudeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.StrongUp, trend);
        Assertions.assertEquals(1.05, magnitudeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(1.05, magnitudeClassifier.getLowPivotDiff(), 0.01);
    }

    @Test
    public void TestMarketStructureClassifier_StrongDown() {
        MarketStructureClassifier slopeClassifier = new MarketStructureClassifier(0.1, MarketStructureClassifier.ThresholdQualifier.Slope);
        MarketStructureClassifier magnitudeClassifier = new MarketStructureClassifier(0.1, MarketStructureClassifier.ThresholdQualifier.Magnitude);

        List<PriceBar> bars = getBars();
        bars.get(4).setHigh(22.0);
        bars.get(14).setHigh(20.95); // just under 0.1
        bars.get(8).setLow(18.0);
        bars.get(18).setLow(16.95); // just under 0.1

        TrendClassification trend = slopeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.StrongDown, trend);
        Assertions.assertEquals(-1.00, slopeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(-0.50, slopeClassifier.getLowPivotDiff(), 0.01);

        trend = magnitudeClassifier.classifyMarketStructure(bars);
        Assertions.assertEquals(TrendClassification.StrongDown, trend);
        Assertions.assertEquals(-1.05, magnitudeClassifier.getHighPivotDiff(), 0.01);
        Assertions.assertEquals(-1.05, magnitudeClassifier.getLowPivotDiff(), 0.01);
    }

}
