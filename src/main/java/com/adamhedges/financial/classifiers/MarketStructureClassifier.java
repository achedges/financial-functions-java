package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import com.adamhedges.financial.functions.Pivots;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class MarketStructureClassifier {

    private List<PriceBar> bars = null;
    private List<Integer> highPivots = null;
    private List<Integer> lowPivots = null;
    private TrendClassification trendClassification;

    @Getter
    private double highPivotDiff = 0;

    @Getter
    private double highPivotSlope = 0;

    @Getter
    private double lowPivotDiff = 0;

    @Getter
    private double lowPivotSlope = 0;

    private final double strongTrendThreshold;

    public MarketStructureClassifier() {
        strongTrendThreshold = 0.25; // 1.0 is a 45-deg slope
    }

    public MarketStructureClassifier(double strongTrendThreshold) {
        this.strongTrendThreshold = strongTrendThreshold;
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

        double highestHigh = bars.getFirst().getHigh();
        double lowestHigh = bars.getFirst().getHigh();
        double highestLow = bars.getFirst().getLow();
        double lowestLow = bars.getFirst().getLow();

        for (PriceBar b : bars) {
            highestHigh = Math.max(b.getHigh(), highestHigh);
            lowestHigh = Math.min(b.getHigh(), lowestHigh);
            highestLow = Math.max(b.getLow(), highestLow);
            lowestLow = Math.min(b.getLow(), lowestLow);
        }

        double highScaleDenom = highestHigh - lowestHigh;
        double lowScaleDenom = highestLow - lowestLow;

        double firstHighPivotNum = bars.get(highPivots.getFirst()).getHigh() - lowestHigh;
        double lastHighPivotNum = bars.get(highPivots.getLast()).getHigh() - lowestHigh;
        double firstLowPivotNum = bars.get(lowPivots.getFirst()).getLow() - lowestLow;
        double lastLowPivotNum = bars.get(lowPivots.getLast()).getLow() - lowestLow;

        highPivotDiff = lastHighPivotNum - firstHighPivotNum;
        lowPivotDiff = lastLowPivotNum - firstLowPivotNum;

        double highPivotDiffScaled = highPivotDiff / highScaleDenom;
        double lowPivotDiffScaled = lowPivotDiff / lowScaleDenom;

        double highTimeScaled = (highPivots.getLast() - highPivots.getFirst()) / ((double)bars.size() - 1);
        double lowTimeScaled = (lowPivots.getLast() - lowPivots.getFirst()) / ((double)bars.size() - 1);

        highPivotSlope = highPivotDiffScaled / highTimeScaled;
        lowPivotSlope = lowPivotDiffScaled / lowTimeScaled;

        if (highPivotSlope >= strongTrendThreshold && lowPivotSlope >= strongTrendThreshold) {
            this.trendClassification = TrendClassification.StrongUp;
        } else if (highPivotSlope > 0.0 && lowPivotSlope > 0.0) {
            this.trendClassification = TrendClassification.WeakUp;
        } else if (highPivotSlope <= -strongTrendThreshold && lowPivotSlope <= -strongTrendThreshold) {
            this.trendClassification = TrendClassification.StrongDown;
        } else if (highPivotSlope < 0.0 && lowPivotSlope < 0.0) {
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

    public Optional<PriceBar> getLastHighPivot() {
        return getLastPivot(highPivots);
    }

    public Optional<PriceBar> getLastLowPivot() {
        return getLastPivot(lowPivots);
    }

    public Optional<Integer> getLastHighPivotIndex() {
        if (highPivots == null || highPivots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(highPivots.getLast());
    }

    public Optional<Integer> getLastLowPivotIndex() {
        if (lowPivots == null || lowPivots.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(lowPivots.getLast());
    }

}
