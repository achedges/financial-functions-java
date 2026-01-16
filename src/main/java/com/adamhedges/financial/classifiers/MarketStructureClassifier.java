package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import com.adamhedges.financial.functions.Pivots;

import java.util.List;

public class MarketStructureClassifier {

    public static TrendClassification classifyMarketStructure(List<PriceBar> bars, double strongTrendThreshold) {
        List<Integer> highPivots = Pivots.get(bars, (a, b) -> a.getHigh() >= b.getHigh(), (a, b) -> a.getHigh() - b.getLow());
        List<Integer> lowPivots = Pivots.get(bars, (a, b) -> a.getLow() <= b.getLow(), (a, b) -> b.getHigh() - a.getLow());

        if (highPivots.size() <= 1 || lowPivots.size() <= 1) {
            return TrendClassification.Flat;
        }

        double diff_h = bars.get(highPivots.getLast()).getHigh() - bars.get(highPivots.getFirst()).getHigh();
        double diff_l = bars.get(lowPivots.getLast()).getLow() - bars.get(lowPivots.getFirst()).getLow();

        if (diff_h >= strongTrendThreshold && diff_l >= strongTrendThreshold) {
            return TrendClassification.StrongUp;
        } else if (diff_h > 0.0 && diff_l > 0.0) {
            return TrendClassification.WeakUp;
        } else if (diff_h <= -strongTrendThreshold && diff_l <= -strongTrendThreshold) {
            return TrendClassification.StrongDown;
        } else if (diff_h < 0.0 && diff_l < 0.0) {
            return TrendClassification.WeakDown;
        } else {
            return TrendClassification.Flat;
        }
    }

}
