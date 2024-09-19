package com.adamhedges.daytrading.library.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StandardDeviation extends WindowFunction {

    public final SimpleMovingAverage movingAverage;

    private BigDecimal sumSqValues = null;
    private BigDecimal deviation = null;

    public StandardDeviation(int period, List<BigDecimal> prices) {
        super(period, prices);
        movingAverage = new SimpleMovingAverage(period, new ArrayList<>(prices)); // for inner window functions, make sure to duplicate the price list
        setIndex(buffer.getIndex());
    }

    @Override
    public void setIndex(int newindex) {
        movingAverage.setIndex(newindex);
        buffer.setIndex(newindex);
        sumSqValues = getBufferSumSq();
        calculate();
    }

    @Override
    public void slide(BigDecimal value) {
        movingAverage.slide(value);
        int oldindex = buffer.getLowerBound();

        if (sumSqValues == null) {
            setIndex(buffer.getIndex());
        }

        sumSqValues = sumSqValues.add(value.pow(2).subtract(values.get(oldindex).pow(2)));
        calculate();

        if (buffer.isRingBuffer()) {
            values.set(oldindex, value);
        }

        buffer.advance();
    }

    @Override
    public void calculate() {
        BigDecimal mavg = movingAverage.getValue();
        BigDecimal p = BigDecimal.valueOf(buffer.getPeriod());
        BigDecimal variance = sumSqValues.subtract(mavg.pow(2).multiply(p));
        deviation = variance.abs().divide(p, RoundingMode.HALF_UP).sqrt(MathContext.DECIMAL64);
    }

    @Override
    public BigDecimal getValue() {
        return deviation;
    }

}
