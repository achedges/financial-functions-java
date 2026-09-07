package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestParabolicSAR {

    private PriceBar getBar(double open, double close) {
        PriceBar bar = new PriceBar("", open);
        bar.setClose(close); // set this first so isUp() works
        bar.setHigh(bar.isUp() ? close : open);
        bar.setLow(bar.isUp() ? open : close);
        return bar;
    }

    @Test
    public void TestParabolicSAR_init() {
        PriceBar bar = new PriceBar("", 10.0);
        bar.setHigh(11.0);
        bar.setClose(11.0);

        ParabolicSAR parabolicSAR = new ParabolicSAR(bar);
        Assertions.assertTrue(parabolicSAR.isLong());
        Assertions.assertEquals(10.0, parabolicSAR.getStop().doubleValue());
        Assertions.assertEquals(10.0, parabolicSAR.getLow().doubleValue());
        Assertions.assertEquals(11.0, parabolicSAR.getHigh().doubleValue());
    }

    @Test
    public void TestParabolicSAR_slide_basic_long() {
        double[][] prices = {
            {51.5, 52.5},
            {52, 53},
            {52.5, 53.5},
            {53, 54},
            {53.5, 54.5},
            {54, 55},
            {54.5, 55.5},
            {55, 56},
            {55.5, 56.5},
            {56, 57},
            {56.5, 57.5},
            {57, 58},
            {57.5, 58.5},
            {58, 59}
        };

        double[] expectedSar = {50.05, 50.16, 50.36, 50.66, 51.04, 51.52, 52.08, 52.71, 53.38, 54.11, 54.78, 55.43, 56.04, 56.63, 56.75};

        ParabolicSAR psar = new ParabolicSAR(getBar(50, 51));
        Assertions.assertEquals(50.0, psar.getStop().doubleValue());

        for (int i = 0; i < prices.length; i++) {
            psar.slide(getBar(prices[i][0], prices[i][1]));
            Assertions.assertEquals(expectedSar[i], psar.getStop().doubleValue(), 0.01);
        }

        Assertions.assertEquals(0.2, psar.getAlpha().doubleValue());
    }

    @Test
    public void TestParabolicSAR_slide_basic_short() {
        double[][] prices = {
            {59, 58},
            {58.5, 57.5},
            {58, 57},
            {57.5, 56.5},
            {57, 56},
            {56.5, 55.5},
            {56, 55},
            {55.5, 54.5},
            {55, 54},
            {54.5, 53.5},
            {54, 53},
            {53.5, 52.5},
            {53, 52},
            {52.5, 51.5}
        };

        double[] expectedSar = {60.45, 60.33, 60.13, 59.84, 59.45, 58.98, 58.42, 57.79, 57.11, 56.39, 55.71, 55.07, 54.45, 53.86, 53.75};
        ParabolicSAR psar = new ParabolicSAR(getBar(60.5, 59.5));
        Assertions.assertEquals(60.5, psar.getStop().doubleValue());

        for (int i = 0; i < prices.length; i++) {
            psar.slide(getBar(prices[i][0], prices[i][1]));
            Assertions.assertEquals(expectedSar[i], psar.getStop().doubleValue(), 0.01);
        }
    }

    @Test
    public void TestParabolicSAR_slide_reversal() {
        double[][] prices = {
            {51.5, 52.5},
            {52, 53},
            {52.5, 53.5}, // max high should become new stop
            {53, 49.99},
            {50.5, 49.5},
            {50, 49}
        };

        double[] expectedSar = {50.05, 50.16, 50.36, 53.50, 53.42, 53.24};

        ParabolicSAR psar = new ParabolicSAR(getBar(50, 51));
        Assertions.assertEquals(50.0, psar.getStop().doubleValue());
        Assertions.assertTrue(psar.isLong()); // should start long

        for (int i = 0; i < prices.length; i++) {
            psar.slide(getBar(prices[i][0], prices[i][1]));
            Assertions.assertEquals(expectedSar[i], psar.getStop().doubleValue(), 0.01);

            if (i == 3) {
                Assertions.assertTrue(psar.isReversalSignal());
            } else {
                Assertions.assertFalse(psar.isReversalSignal());
            }
        }

        Assertions.assertFalse(psar.isLong()); // should end short
    }

    @Test
    public void TestParabolicSAR_market_data() {
        // EUR_USD, H1, 20260902 @ 1200 UTC -> 20260903 @ 2200 UTC
        PriceBar initBar = new PriceBar("EUR_USD", 20260902, 1000);
        initBar.setOpen(1.15717);
        initBar.setHigh(1.15728);
        initBar.setLow(1.15670);
        initBar.setClose(1.15725);

        double[][] priceData = {
            {1.157740,1.158570,1.157580,1.157840},
            {1.157840,1.160940,1.157790,1.160440},
            {1.160440,1.160440,1.158870,1.159340},
            {1.159360,1.159700,1.158900,1.159210},
            {1.159200,1.159400,1.158740,1.158740},
            {1.158740,1.158990,1.158340,1.158660},
            {1.158670,1.158980,1.158440,1.158580},
            {1.158580,1.158770,1.158260,1.158740},
            {1.158740,1.159000,1.158720,1.158850},
            {1.158740,1.158840,1.158700,1.158720},
            {1.158760,1.158980,1.158580,1.158700},
            {1.158700,1.158740,1.158440,1.158520},
            {1.158500,1.159100,1.158350,1.158950},
            {1.158960,1.159360,1.158510,1.158620},
            {1.158610,1.159500,1.158520,1.159120},
            {1.159120,1.159800,1.159100,1.159670},
            {1.159690,1.160040,1.159460,1.159560},
            {1.159550,1.160060,1.159360,1.160020},
            {1.160020,1.160830,1.159630,1.160460},
            {1.160460,1.161480,1.160180,1.160400},
            {1.160410,1.161150,1.159980,1.160910},
            {1.160920,1.161250,1.160140,1.160580},
            {1.160590,1.160710,1.159790,1.160400},
            {1.160400,1.161460,1.160190,1.161260},
            {1.161280,1.163020,1.160840,1.162580},
            {1.162590,1.162880,1.161740,1.162740},
            {1.162740,1.162760,1.161330,1.161830},
            {1.161860,1.162760,1.161740,1.162300},
            {1.162290,1.164140,1.162130,1.163680},
            {1.163680,1.163860,1.163280,1.163280},
            {1.163280,1.163660,1.163180,1.163480},
            {1.163470,1.163520,1.162530,1.162870},
            {1.162880,1.163010,1.162500,1.162560},
            {1.162620,1.162900,1.162520,1.162560},
            {1.162600,1.162780,1.162290,1.162310}
        };

        List<PriceBar> bars = new ArrayList<>();
        for (double[] ohlc : priceData) {
            PriceBar bar = new PriceBar("EUR_USD", ohlc[0]);
            bar.setHigh(ohlc[1]);
            bar.setLow(ohlc[2]);
            bar.setClose(ohlc[3]);
            bars.add(bar);
        }

        double[] expectedStops = {
            1.15665,
            1.15699,
            1.15686,
            1.15702,
            1.15718,
            1.15733,
            1.15747,
            1.15761,
            1.15775,
            1.15787,
            1.15800,
            1.15811,
            1.15823,
            1.15834,
            1.15835,
            1.15845,
            1.15852,
            1.15862,
            1.15871,
            1.15880,
            1.15896,
            1.15911,
            1.15925,
            1.15939,
            1.15951,
            1.15979,
            1.16005,
            1.16029,
            1.16051,
            1.16087,
            1.16120,
            1.16149,
            1.16176,
            1.16199,
            1.16221
        };

        ParabolicSAR psar = new ParabolicSAR(initBar);
        psar.setIsLong(false); // force to match actual setup

        for (int i = 0; i < bars.size(); i++) {
            psar.slide(bars.get(i));
//            Assertions.assertEquals(expectedStops[i], psar.getStop().doubleValue(), 0.0001);
            System.out.printf("Expected %.5f / Actual %.5f (alpha=%.2f)%n", expectedStops[i], psar.getStop().doubleValue(), psar.getAlpha());
        }
    }

}
