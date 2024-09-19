package com.adamhedges.daytrading.library.functions;

import com.adamhedges.daytrading.library.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class TestAverageTrueRange {

    private final double[][] priceData = {
            {50.00, 51.20, 49.80, 50.90},
            {50.70, 51.80, 50.30, 51.50},
            {51.70, 52.90, 51.70, 52.80},
            {52.50, 53.70, 52.30, 53.50},
            {53.60, 54.80, 53.50, 54.70},
            {54.40, 54.40, 52.90, 53.00},
            {52.90, 53.20, 52.00, 52.00},
            {52.00, 52.70, 52.00, 52.20}
    };

    private final double[] knownRanges = {1.40, 1.50, 1.40, 1.40, 1.30, 1.80, 1.20, 0.70};

    private List<PriceBar> getPriceBars() {
        List<PriceBar> bars = new ArrayList<>();
        for (double[] prices : priceData) {
            PriceBar bar = new PriceBar("TEST");
            bar.setOpen(prices[0]);
            bar.setHigh(prices[1]);
            bar.setLow(prices[2]);
            bar.setClose(prices[3]);
            bars.add(bar);
        }
        return bars;
    }

    @Test
    public void TestAverageTrueRange_calculation() {
        List<PriceBar> bars = getPriceBars();
        for (int i = 0; i < bars.size(); i++) {
            PriceBar prevbar = i > 0 ? bars.get(i-1) : null;
            BigDecimal tr = AverageTrueRange.calculateTrueRange(bars.get(i), prevbar);
            Assertions.assertEquals(knownRanges[i], tr.doubleValue());
        }
    }

    @Test
    public void TestAverageTrueRange_slide() {
        List<PriceBar> bars = getPriceBars();

        AverageTrueRange atr = new AverageTrueRange(bars.subList(0, 7));
        Assertions.assertEquals(1.428571, atr.getAverageTrueRange().round(MathContext.DECIMAL64).doubleValue());

        atr.slide(bars.getLast());
        Assertions.assertEquals(1.324489, atr.getAverageTrueRange().round(MathContext.DECIMAL64).doubleValue());
    }

}
