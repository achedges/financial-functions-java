package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import com.adamhedges.financial.functions.Pivots;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
public class MarketStructureClassifier {

    private List<PriceBar> bars = null;
    private List<Integer> highPivots = null;
    private List<Integer> lowPivots = null;
    private TrendClassification trendClassification;

    @Getter
    private double highPivotDiff = 0;

    @Getter
    private double lowPivotDiff = 0;

    private final double strongTrendThreshold;
    private final ThresholdQualifier thresholdQualifier;

    public MarketStructureClassifier() {
        strongTrendThreshold = 0.25; // 1.0 is a 45-deg slope
        thresholdQualifier = ThresholdQualifier.SlopeDouble;
    }

    public MarketStructureClassifier(double strongTrendThreshold, ThresholdQualifier qualifier) {
        this.strongTrendThreshold = strongTrendThreshold;
        this.thresholdQualifier = qualifier;
    }

    public TrendClassification classifyMarketStructure(List<PriceBar> bars) {
        this.bars = bars;

        highPivotDiff = 0;
        lowPivotDiff = 0;
        highPivots = Pivots.get(bars, (a, b) -> a.getHigh() >= b.getHigh(), (a, b) -> a.getHigh() - b.getHigh());
        lowPivots = Pivots.get(bars, (a, b) -> a.getLow() <= b.getLow(), (a, b) -> b.getLow() - a.getLow());

        if (highPivots.size() <= 1 || lowPivots.size() <= 1) {
            return TrendClassification.Mixed;
        }

        highPivotDiff = switch (thresholdQualifier) {
            case ThresholdQualifier.SlopeSingle, ThresholdQualifier.SlopeDouble -> getSlope(bars, PriceBar::getHigh, highPivots);
            case ThresholdQualifier.MagnitudeSingle, ThresholdQualifier.MagnitudeDouble -> bars.get(highPivots.getLast()).getHigh() - bars.get(highPivots.getFirst()).getHigh();
        };

        lowPivotDiff = switch (thresholdQualifier) {
            case ThresholdQualifier.SlopeSingle, ThresholdQualifier.SlopeDouble -> getSlope(bars, PriceBar::getLow, lowPivots);
            case ThresholdQualifier.MagnitudeSingle, ThresholdQualifier.MagnitudeDouble -> bars.get(lowPivots.getLast()).getLow() - bars.get(lowPivots.getFirst()).getLow();
        };

        boolean isStrongUp = switch (thresholdQualifier) {
            case ThresholdQualifier.SlopeSingle, ThresholdQualifier.MagnitudeSingle -> highPivotDiff >= strongTrendThreshold;
            case ThresholdQualifier.SlopeDouble, ThresholdQualifier.MagnitudeDouble -> highPivotDiff >= strongTrendThreshold && lowPivotDiff >= strongTrendThreshold;
        };

        boolean isStrongDown = switch (thresholdQualifier) {
            case ThresholdQualifier.SlopeSingle, ThresholdQualifier.MagnitudeSingle -> lowPivotDiff <= -strongTrendThreshold;
            case ThresholdQualifier.SlopeDouble, ThresholdQualifier.MagnitudeDouble -> lowPivotDiff <= -strongTrendThreshold && highPivotDiff <= -strongTrendThreshold;
        };

        if (isStrongUp) {
            this.trendClassification = TrendClassification.StrongUp;
        } else if (highPivotDiff > 0.0 && lowPivotDiff > 0.0) {
            this.trendClassification = TrendClassification.WeakUp;
        } else if (isStrongDown) {
            this.trendClassification = TrendClassification.StrongDown;
        } else if (highPivotDiff < 0.0 && lowPivotDiff < 0.0) {
            this.trendClassification = TrendClassification.WeakDown;
        } else {
            this.trendClassification = TrendClassification.Mixed;
        }

        return this.trendClassification;
    }

    private Optional<PriceBar> getLastPivot(List<Integer> pivots) {
        if (pivots == null || pivots.isEmpty()) {
            return Optional.empty();
        }

        int lastPivot = pivots.getLast();
        if (bars == null || lastPivot >= bars.size()) {
            return Optional.empty();
        }

        return Optional.of(bars.get(lastPivot));
    }

    private Optional<PriceBar> getFirstPivot(List<Integer> pivots) {
        if (pivots == null || pivots.isEmpty()) {
            return Optional.empty();
        }

        int firstPivot = pivots.getFirst();
        if (bars == null || firstPivot >= bars.size()) {
            return Optional.empty();
        }

        return Optional.of(bars.get(firstPivot));
    }

    private double getSlope(List<PriceBar> bars, Function<PriceBar, Double> valueAccessor, List<Integer> pivots) {
        double highestVal = valueAccessor.apply(bars.getFirst());
        double lowestVal = valueAccessor.apply(bars.getFirst());

        for (PriceBar bar : bars) {
            highestVal = Math.max(highestVal, valueAccessor.apply(bar));
            lowestVal = Math.min(lowestVal, valueAccessor.apply(bar));
        }

        double valueScaleDenom = highestVal - lowestVal;
        double firstPivotNum = valueAccessor.apply(bars.get(pivots.getFirst())) - lowestVal;
        double lastPivotNum = valueAccessor.apply(bars.get(pivots.getLast())) - lowestVal;
        double pivotDiff = lastPivotNum - firstPivotNum;
        double pivotDiffScaled = pivotDiff / valueScaleDenom;
        double timeScaled = (pivots.getLast() - pivots.getFirst()) / ((double)bars.size() - 1);

        return pivotDiffScaled / timeScaled;
    }

    public Optional<PriceBar> getLastHighPivot() {
        return getLastPivot(highPivots);
    }

    public Optional<PriceBar> getFirstHighPivot() {
        return getFirstPivot(highPivots);
    }

    public Optional<PriceBar> getLastLowPivot() {
        return getLastPivot(lowPivots);
    }

    public Optional<PriceBar> getFirstLowPivot() {
        return getFirstPivot(lowPivots);
    }

    public Optional<Integer> getLastHighPivotIndex() {
        if (highPivots == null || highPivots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(highPivots.getLast());
    }

    public Optional<Integer> getFirstHighPivotIndex() {
        if (highPivots == null || highPivots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(highPivots.getFirst());
    }

    public Optional<Integer> getLastLowPivotIndex() {
        if (lowPivots == null || lowPivots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(lowPivots.getLast());
    }

    public Optional<Integer> getFirstLowPivotIndex() {
        if (lowPivots == null || lowPivots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(lowPivots.getFirst());
    }

}
