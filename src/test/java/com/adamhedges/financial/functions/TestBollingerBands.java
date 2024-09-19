package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class TestBollingerBands {

    private void checkStats(BollingerBands func, int newIndex) {
        func.slide(BigDecimal.valueOf(Context.data[newIndex]));
        int testIndex = (func.buffer.getIndex() - func.buffer.getPeriod() + 1) % Context.dev.length;

        BigDecimal mavg = func.standardDeviation.movingAverage.getValue();
        BigDecimal sdev = func.standardDeviation.getValue();
        BigDecimal two  = BigDecimal.valueOf(2);
        BigDecimal upperBand = mavg.add(sdev.multiply(two));
        BigDecimal lowerBand = mavg.subtract(sdev.multiply(two));

        double assertThreshold = 0.0001;
        Assertions.assertEquals(Context.sma[testIndex], mavg.doubleValue(), assertThreshold);
        Assertions.assertEquals(Context.dev[testIndex], sdev.doubleValue(), assertThreshold);
        Assertions.assertEquals(upperBand.doubleValue(), func.getUpperBand().doubleValue(), assertThreshold);
        Assertions.assertEquals(lowerBand.doubleValue(), func.getLowerBand().doubleValue(), assertThreshold);
    }

    @Test
    public void TestBollingerBands_linearBuffer() {
        List<PriceBar> bars = Context.getPriceBarList(Context.data.length);
        BollingerBands bb = new BollingerBands(Context.period, Context.mapClosePricesFromBars(bars));
        while (bb.buffer.getIndex() < Context.data.length + 10) {
            int newIndex = (bb.buffer.getIndex() + 1) % bb.buffer.getLength();
            checkStats(bb, newIndex);
        }
    }

    @Test
    public void TestBollingerBands_ringBuffer() {
        List<PriceBar> bars = Context.getPriceBarList(Context.period);
        BollingerBands bb = new BollingerBands(Context.period, Context.mapClosePricesFromBars(bars));
        while (bb.buffer.getIndex() < Context.data.length + 10) {
            int newIndex = (bb.buffer.getIndex() + 1) % Context.data.length;
            checkStats(bb, newIndex);
        }
    }

}
