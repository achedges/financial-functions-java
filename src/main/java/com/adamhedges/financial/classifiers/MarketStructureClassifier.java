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

    private final double strongTrendThreshold;

    public MarketStructureClassifier() {
        strongTrendThreshold = 0.0005;
    }

    public MarketStructureClassifier(double strongTrendThreshold) {
        this.strongTrendThreshold = strongTrendThreshold;
    }

    public TrendClassification classifyMarketStructure(List<PriceBar> bars) {
        this.bars = bars;
        this.highPivots = Pivots.get(bars, (a, b) -> a.getHigh() >= b.getHigh(), (a, b) -> a.getHigh() - b.getLow());
        this.lowPivots = Pivots.get(bars, (a, b) -> a.getLow() <= b.getLow(), (a, b) -> b.getHigh() - a.getLow());

        if (highPivots.size() <= 1 || lowPivots.size() <= 1) {
            return TrendClassification.Flat;
        }

        double diff_h = bars.get(highPivots.getLast()).getHigh() - bars.get(highPivots.getFirst()).getHigh();
        double diff_l = bars.get(lowPivots.getLast()).getLow() - bars.get(lowPivots.getFirst()).getLow();

        if (diff_h >= strongTrendThreshold && diff_l >= strongTrendThreshold) {
            this.trendClassification = TrendClassification.StrongUp;
        } else if (diff_h > 0.0 && diff_l > 0.0) {
            this.trendClassification = TrendClassification.WeakUp;
        } else if (diff_h <= -strongTrendThreshold && diff_l <= -strongTrendThreshold) {
            this.trendClassification = TrendClassification.StrongDown;
        } else if (diff_h < 0.0 && diff_l < 0.0) {
            this.trendClassification = TrendClassification.WeakDown;
        } else {
            this.trendClassification = TrendClassification.Flat;
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

}
