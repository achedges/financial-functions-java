package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RelativeStrengthIndex {

    private final int period;
    private final List<PriceBar> barsWindow;

    @Getter
    private double relativeStrength;

    @Getter
    private double RSI;

    public RelativeStrengthIndex(int period, List<PriceBar> bars) {
        this.period = period;
        this.barsWindow = new ArrayList<>(bars);

        if (this.barsWindow.size() == period) {
            calculate();
        }
    }

    private void calculate() {
        // This is a bit different than the original calculation.
        // For a 14-period measurement, Wilder's book includes a 15th price before calculating.
        // Since I'm not planning to use a period of 14 it seem unnecessary to adjust the backfill/period logic to account for this.

        double gains = 0.0;
        double losses = 0.0;

        for (int i = 1; i < barsWindow.size(); i++) {
            if (barsWindow.get(i).getClose() > barsWindow.get(i-1).getClose()) { // up
                gains += barsWindow.get(i).getClose() - barsWindow.get(i-1).getClose();
            } else if (barsWindow.get(i).getClose() < barsWindow.get(i-1).getClose()) { // down
                losses += barsWindow.get(i-1).getClose() - barsWindow.get(i).getClose();
            }
        }

        double avgGains = gains / barsWindow.size();
        double avgLosses = losses / barsWindow.size();

        relativeStrength = avgGains / avgLosses;
        RSI = 100 - (100 / (1.0 + relativeStrength));
    }

    public void slide(PriceBar bar) {
        if (barsWindow.size() == period) {
            barsWindow.removeFirst();
        }

        barsWindow.add(bar);

        if (barsWindow.size() == period) {
            calculate();
        }
    }

}
