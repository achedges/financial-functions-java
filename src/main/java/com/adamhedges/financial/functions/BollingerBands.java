package com.adamhedges.financial.functions;

import com.adamhedges.utilities.decimal.Comparisons;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BollingerBands extends WindowFunction {

    public final int DEFAULT_DEVIATION_MULTIPLIER = 2;
    public final StandardDeviation standardDeviation;

    private int deviationMultiplier = DEFAULT_DEVIATION_MULTIPLIER;
    private BigDecimal upperBand = BigDecimal.ZERO;
    private BigDecimal lowerBand = BigDecimal.ZERO;

    public BollingerBands(int period, List<BigDecimal> prices) {
        super(period, prices);
        standardDeviation = new StandardDeviation(period, new ArrayList<>(prices));
    }

    public BollingerBands(int period, List<BigDecimal> prices, int deviationMultiplier) {
        super(period, prices);
        standardDeviation = new StandardDeviation(period, new ArrayList<>(prices));
        this.deviationMultiplier = deviationMultiplier;
    }

    @Override
    public void setIndex(int newIndex) {
        standardDeviation.setIndex(newIndex);
        buffer.setIndex(newIndex);
    }

    @Override
    public void slide(BigDecimal value) {
        int oldIndex = buffer.getLowerBound();
        standardDeviation.slide(value);
        calculate();

        if (buffer.isRingBuffer()) {
            values.set(oldIndex, value);
        }

        buffer.advance();
    }

    @Override
    public void calculate() {
        BigDecimal mavg = standardDeviation.movingAverage.getValue();
        BigDecimal sdev = standardDeviation.getValue();
        BigDecimal ndev  = BigDecimal.valueOf(deviationMultiplier);
        upperBand = mavg.add(sdev.multiply(ndev));
        lowerBand = mavg.subtract(sdev.multiply(ndev));
    }

    @Override
    public BigDecimal getValue() {
        return standardDeviation.getValue();
    }

    @Override
    public double getDouble() {
        return standardDeviation.getValue().doubleValue();
    }

    public BigDecimal getBandWidthPriceRatio() {
        BigDecimal last = getLastValue();
        BigDecimal width = upperBand.subtract(lowerBand);

        return Comparisons.gt(last, BigDecimal.ZERO)
            ? width.divide(last, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    }

}
