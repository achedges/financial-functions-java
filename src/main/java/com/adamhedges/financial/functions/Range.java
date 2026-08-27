package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;

import java.util.List;
import java.util.Optional;

public class Range {

    private final int period;
    private final List<PriceBar> bars;

    private Double min = null;
    private Double max = null;

    public Range(int period, List<PriceBar> bars) {
        this.period = period;
        this.bars = bars;
        getRange();
    }

    private void getRange() {
        for (PriceBar bar : bars) {
            if (min == null || bar.getLow() < min) {
                min = bar.getLow();
            }
            if (max == null || bar.getHigh() > max) {
                max = bar.getHigh();
            }
        }
    }

    public Optional<Double> getRangeMin() {
        return min == null ? Optional.empty() : Optional.of(min);
    }

    public Optional<Double> getRangeMax() {
        return max == null ? Optional.empty() : Optional.of(max);
    }

    public void slide(PriceBar newBar) {
        if (bars.size() == period) {
            bars.removeFirst();
        }

        bars.add(newBar);
        getRange();
    }

}
