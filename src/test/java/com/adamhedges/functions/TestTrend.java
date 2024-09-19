package com.adamhedges.daytrading.library.functions;

import com.adamhedges.daytrading.library.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class TestTrend {

    private final double assertThreshold = 0.001;

    private void checkStats(Trend func, int newIndex) {
        func.slide(BigDecimal.valueOf(Context.data[newIndex]));
        int testIndex = (func.buffer.getIndex() - func.buffer.getPeriod() + 1) % Context.trnd.length;
        Assertions.assertEquals(Context.trnd[testIndex], func.getValue().doubleValue(), assertThreshold);
    }

    @Test
    public void TestTrend_linearBuffer() {
        List<PriceBar> bars = Context.getPriceBarList(Context.data.length);
        Trend trend = new Trend(Context.period, Context.mapClosePricesFromBars(bars));

        // check initial function state
        Assertions.assertEquals(Context.trnd[0], trend.getValue().doubleValue(), assertThreshold);

        while (trend.buffer.getIndex() < Context.data.length + 10) {
            int newIndex = (trend.buffer.getIndex() + 1) % trend.buffer.getLength();
            checkStats(trend, newIndex);
        }
    }

    @Test
    public void TestTrend_ringBuffer() {
        List<PriceBar> bars = Context.getPriceBarList(Context.period);
        Trend trend = new Trend(Context.period, Context.mapClosePricesFromBars(bars));

        // check initial function state
        Assertions.assertEquals(Context.trnd[0], trend.getValue().doubleValue(), assertThreshold);

        while (trend.buffer.getIndex() < Context.data.length + 10) {
            int newIndex = (trend.buffer.getIndex() + 1) % Context.data.length;
            checkStats(trend, newIndex);
        }
    }

}
