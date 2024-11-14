package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class TestBollingerBands {

    private void checkStats(BollingerBands func, int newIndex, int deviationMultiplier) {
        func.slide(BigDecimal.valueOf(Context.data[newIndex]));
        int testIndex = (func.buffer.getIndex() - func.buffer.getPeriod() + 1) % Context.dev.length;

        BigDecimal mavg = func.standardDeviation.movingAverage.getValue();
        BigDecimal sdev = func.standardDeviation.getValue();
        BigDecimal ndev  = BigDecimal.valueOf(deviationMultiplier);
        BigDecimal upperBand = mavg.add(sdev.multiply(ndev));
        BigDecimal lowerBand = mavg.subtract(sdev.multiply(ndev));

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
            checkStats(bb, newIndex, 2);
        }

        BollingerBands bb2 = new BollingerBands(Context.period, Context.mapClosePricesFromBars(bars), 3);
        while (bb2.buffer.getIndex() < Context.data.length + 10) {
            int newIndex = (bb2.buffer.getIndex() + 1) % bb2.buffer.getLength();
            checkStats(bb2, newIndex, 3);
        }
    }

    @Test
    public void TestBollingerBands_ringBuffer() {
        List<PriceBar> bars = Context.getPriceBarList(Context.period);
        BollingerBands bb = new BollingerBands(Context.period, Context.mapClosePricesFromBars(bars));
        while (bb.buffer.getIndex() < Context.data.length + 10) {
            int newIndex = (bb.buffer.getIndex() + 1) % Context.data.length;
            checkStats(bb, newIndex, 2);
        }

        BollingerBands bb2 = new BollingerBands(Context.period, Context.mapClosePricesFromBars(bars), 3);
        while (bb2.buffer.getIndex() < Context.data.length + 10) {
            int newIndex = (bb2.buffer.getIndex() + 1) % Context.data.length;
            checkStats(bb2, newIndex, 3);
        }
    }

}
