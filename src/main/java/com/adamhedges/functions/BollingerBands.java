package com.adamhedges.daytrading.library.functions;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BollingerBands extends WindowFunction {

    public final StandardDeviation standardDeviation;

    private BigDecimal upperBand = BigDecimal.ZERO;
    private BigDecimal lowerBand = BigDecimal.ZERO;

    public BollingerBands(int period, List<BigDecimal> prices) {
        super(period, prices);
        standardDeviation = new StandardDeviation(period, new ArrayList<>(prices));
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
        BigDecimal two  = BigDecimal.valueOf(2);
        upperBand = mavg.add(sdev.multiply(two));
        lowerBand = mavg.subtract(sdev.multiply(two));
    }

    @Override
    public BigDecimal getValue() {
        return standardDeviation.getValue();
    }

}
