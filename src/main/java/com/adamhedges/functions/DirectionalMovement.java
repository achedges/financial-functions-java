package com.adamhedges.daytrading.library.functions;

import com.adamhedges.daytrading.library.bars.PriceBar;
import com.adamhedges.daytrading.library.util.Comparisons;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.TreeSet;

@Getter
public class DirectionalMovement {

    private final int p;

    private BigDecimal tr = BigDecimal.ZERO;
    private BigDecimal posDm = BigDecimal.ZERO;
    private BigDecimal negDm = BigDecimal.ZERO;
    private BigDecimal trueRangeSum = BigDecimal.ZERO;
    private BigDecimal posDmSum = BigDecimal.ZERO;
    private BigDecimal negDmSum = BigDecimal.ZERO;
    private BigDecimal dxSum = BigDecimal.ZERO;

    private BigDecimal positiveDi = BigDecimal.ZERO;
    private BigDecimal negativeDi = BigDecimal.ZERO;
    private BigDecimal dx = BigDecimal.ZERO;
    private BigDecimal adx = BigDecimal.ZERO;

    private PriceBar lastBar;

    public DirectionalMovement(int period, List<PriceBar> bars) {
        int n = bars.size();
        p = period;
        lastBar = bars.getFirst();

        for (int i = 1; i < n; i++) {
            tr = getTrueRange(bars.get(i), lastBar);
            posDm = getPositiveDm(bars.get(i), lastBar);
            negDm = getNegativeDm(bars.get(i), lastBar);

            if (i < p) {
                // in the first segment
                trueRangeSum = trueRangeSum.add(tr);
                posDmSum = posDmSum.add(Comparisons.gt(posDm, negDm) ? posDm : BigDecimal.ZERO);
                negDmSum = negDmSum.add(Comparisons.gt(negDm, posDm) ? negDm : BigDecimal.ZERO);
            } else {
                // in the second segment
                trueRangeSum = getSmoothedSum(trueRangeSum, tr);
                posDmSum = getSmoothedSum(posDmSum, Comparisons.gt(posDm, negDm) ? posDm : BigDecimal.ZERO);
                negDmSum = getSmoothedSum(negDmSum, Comparisons.gt(negDm, posDm) ? negDm : BigDecimal.ZERO);

                positiveDi = posDmSum.divide(trueRangeSum, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                negativeDi = negDmSum.divide(trueRangeSum, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                dx = positiveDi.subtract(negativeDi).abs().divide(positiveDi.add(negativeDi), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                dxSum = dxSum.add(dx);
            }

            lastBar = bars.get(i);
        }
    }

    public static BigDecimal getTrueRange(PriceBar currentBar, PriceBar previousBar) {
        BigDecimal currentHigh = BigDecimal.valueOf(currentBar.getHigh());
        BigDecimal currentLow = BigDecimal.valueOf(currentBar.getLow());
        BigDecimal previousClose = BigDecimal.valueOf(previousBar.getClose());

        TreeSet<BigDecimal> ranges = new TreeSet<>();
        ranges.add(currentHigh.subtract(currentLow));
        ranges.add(currentHigh.subtract(previousClose).abs());
        ranges.add(currentLow.subtract(previousClose).abs());

        return ranges.getLast();
    }

    public static BigDecimal getPositiveDm(PriceBar currentBar, PriceBar previousBar) {
        BigDecimal currentHigh = BigDecimal.valueOf(currentBar.getHigh());
        BigDecimal previousHigh = BigDecimal.valueOf(previousBar.getHigh());
        BigDecimal d = currentHigh.subtract(previousHigh);
        return Comparisons.gt(d, BigDecimal.ZERO) ? d : BigDecimal.ZERO;
    }

    public static BigDecimal getNegativeDm(PriceBar currentBar, PriceBar previousBar) {
        BigDecimal previousLow = BigDecimal.valueOf(previousBar.getLow());
        BigDecimal currentLow = BigDecimal.valueOf(currentBar.getLow());
        BigDecimal d = previousLow.subtract(currentLow);
        return Comparisons.gt(d, BigDecimal.ZERO) ? d : BigDecimal.ZERO;
    }

    public BigDecimal getSmoothedSum(BigDecimal currentSum, BigDecimal newValue) {
        return currentSum.subtract(currentSum.divide(BigDecimal.valueOf(p), RoundingMode.HALF_UP)).add(newValue);
    }

    public void slide(PriceBar newBar) {
        tr = getTrueRange(newBar, lastBar);
        posDm = getPositiveDm(newBar, lastBar);
        negDm = getNegativeDm(newBar, lastBar);

        trueRangeSum = getSmoothedSum(trueRangeSum, tr);
        posDmSum = getSmoothedSum(posDmSum, Comparisons.gt(posDm, negDm) ? posDm : BigDecimal.ZERO);
        negDmSum = getSmoothedSum(negDmSum, Comparisons.gt(negDm, posDm) ? negDm : BigDecimal.ZERO);

        positiveDi = posDmSum.divide(trueRangeSum, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        negativeDi = negDmSum.divide(trueRangeSum, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        dx = positiveDi.subtract(negativeDi).abs().divide(positiveDi.add(negativeDi), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        dxSum = getSmoothedSum(dxSum, dx);
        adx = dxSum.divide(BigDecimal.valueOf(p), RoundingMode.HALF_UP);

        lastBar = newBar;
    }

}
