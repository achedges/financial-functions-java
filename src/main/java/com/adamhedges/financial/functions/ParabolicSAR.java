package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ParabolicSAR {

    private boolean isLong;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal stop;

    private final BigDecimal rate = BigDecimal.valueOf(0.02);
    private BigDecimal alpha = rate;
    private final BigDecimal maxAlpha = BigDecimal.valueOf(0.2);

    private boolean reversalSignal = false;

    public ParabolicSAR(PriceBar priceBar) {
        isLong = priceBar.isUp();
        high = BigDecimal.valueOf(priceBar.getHigh());
        low = BigDecimal.valueOf(priceBar.getLow());
        stop = isLong ? low : high;
    }

    private void updateAlpha() {
        alpha = alpha.add(rate);
        alpha = alpha.min(maxAlpha);
    }

    private void reverse(BigDecimal newStop) {
        isLong = !isLong;
        stop = newStop;
        alpha = rate;
        reversalSignal = true;
    }

    public void setIsLong(boolean isLong) {
        this.isLong = isLong;
    }

    public void slide(PriceBar newBar) {
        BigDecimal barLow = BigDecimal.valueOf(newBar.getLow());
        BigDecimal barHigh = BigDecimal.valueOf(newBar.getHigh());

        if (isLong) {
            if (barLow.compareTo(stop) < 0) {
                reverse(high.max(barHigh));
                low = barLow;
            } else {
                stop = stop.add(alpha.multiply(barHigh.subtract(stop)));
                reversalSignal = false;
                if (barHigh.compareTo(high) > 0) {
                    high = barHigh;
                    updateAlpha();
                }
            }
        } else {
            if (barHigh.compareTo(stop) > 0) {
                reverse(low.min(barLow));
                high = barHigh;
            } else {
                stop = stop.subtract(alpha.multiply(stop.subtract(barLow)));
                reversalSignal = false;
                if (barLow.compareTo(low) < 0) {
                    low = barLow;
                    updateAlpha();
                }
            }
        }
    }

}
