package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Context {

    public static final int period = 4;
    public static final double[] data = {303,305.12,303.17,302.09,306.17,309.01,310.24,311.26,310.35,303.26,300.42,306.41,307.4,303.5,305.57,300.8};
    public static final double[] sma  = {303.3450,304.1375,305.1100,306.8775,309.1700,310.2150,308.7775,306.3225,305.1100,304.3725,304.4325,305.7200,304.3175,303.2175,303.6225,303.0225};
    public static final double[] ema  = {303.3450,304.4750,306.2890,307.8694,309.2256,309.6754,307.1092,304.4335,305.2241,306.0945,305.0567,305.2620,303.4772,303.2863,304.0198,303.6799};
    public static final double[] dev  = {1.1040,1.5988,2.7027,3.1335,1.9065,0.8005,3.2100,4.6048,3.6934,2.7467,2.7243,1.4361,2.4552,1.6960,1.8967,1.5298};
    public static final double[] trnd = {-0.003,0.003,0.019,0.027,0.017,0.004,-0.022,-0.035,-0.013,0.014,0.010,-0.003,-0.021,-0.002,-0.001,0.008};

    public static List<PriceBar> getPriceBarList(int count) {
        List<PriceBar> bars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            bars.add(new PriceBar("TEST", data[i]));
        }
        return bars;
    }

    public static List<BigDecimal> mapClosePricesFromBars(List<PriceBar> bars) {
        return bars.stream().map(b -> BigDecimal.valueOf(b.getClose())).collect(Collectors.toList());
    }

}
