package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import lombok.Getter;

@Getter
public class ParabolicSAR {

    private boolean isLong;
    private double high;
    private double low;
    private double stop;
    private double alpha;

    private final double rate = 0.02;
    private final double maxAlpha = 0.2;

    private boolean reversalSignal = false;

    public ParabolicSAR(PriceBar priceBar) {
        isLong = priceBar.isUp();
        high = priceBar.getHigh();
        low = priceBar.getLow();
        stop = isLong ? priceBar.getLow() : priceBar.getHigh();
    }

    private void updateAlpha() {
        alpha += rate;
        alpha = Math.min(alpha, maxAlpha);
    }

    private void reverse(double newStop) {
        isLong = !isLong;
        stop = newStop;
        alpha = 0.0;
        reversalSignal = true;
    }

    public void slide(PriceBar newBar) {
        if (isLong) {
            if (newBar.getLow() < stop) {
                reverse(Math.max(high, newBar.getHigh()));
                low = newBar.getLow();
            } else {
                high = Math.max(high, newBar.getHigh()); // maintain for the next reversal
                stop += alpha * (newBar.getHigh() - stop);
                reversalSignal = false;
            }
        } else {
            if (newBar.getHigh() > stop) {
                reverse(Math.min(low, newBar.getLow()));
                high = newBar.getHigh();
            } else {
                low = Math.min(low, newBar.getLow()); // maintain for the next reversal
                stop -= alpha * (stop - newBar.getLow());
                reversalSignal = false;
            }
        }

        updateAlpha();
    }

}
