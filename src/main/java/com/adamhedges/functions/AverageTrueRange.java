package com.adamhedges.daytrading.library.functions;

import com.adamhedges.daytrading.library.bars.PriceBar;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.TreeSet;

@Getter
public class AverageTrueRange {

    private final BigDecimal n;

    private BigDecimal averageTrueRange = BigDecimal.ZERO;
    private PriceBar currentBar = null;
    private PriceBar previousBar = null;

    public AverageTrueRange(List<PriceBar> prices) {
        n = BigDecimal.valueOf(prices.size());
        for (int i = 0; i < n.intValue(); i++) {
            PriceBar prevbar = i > 0 ? prices.get(i-1) : null;
            averageTrueRange = averageTrueRange.add(calculateTrueRange(prices.get(i), prevbar));
        }
        averageTrueRange = averageTrueRange.divide(n, 6, RoundingMode.HALF_DOWN);
        currentBar = prices.get(n.intValue() - 1);
        previousBar = prices.get(n.intValue() - 2);
    }

    public void slide(PriceBar newBar) {
        previousBar = currentBar;
        currentBar = newBar;
        BigDecimal trueRange = calculateTrueRange(currentBar, previousBar);
        BigDecimal decay = averageTrueRange.multiply(n.subtract(BigDecimal.ONE));
        averageTrueRange = decay.add(trueRange).divide(n, 6, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateTrueRange(PriceBar currentBar, PriceBar previousBar) {
        // build a tree set from each diff, return the max (last)
        BigDecimal currentHigh = BigDecimal.valueOf(currentBar.getHigh());
        BigDecimal currentLow = BigDecimal.valueOf(currentBar.getLow());

        TreeSet<BigDecimal> ranges = new TreeSet<>();
        ranges.add(currentHigh.subtract(currentLow));

        if (previousBar != null) {
            BigDecimal previousClose = BigDecimal.valueOf(previousBar.getClose());
            ranges.add(currentHigh.subtract(previousClose).abs());
            ranges.add(currentLow.subtract(previousClose).abs());
        }

        return ranges.last();
    }

}
