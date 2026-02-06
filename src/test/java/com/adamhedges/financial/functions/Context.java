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

    public static final double[] pivots = {1.18501,1.18506,1.18503,1.18500,1.18502,1.18499,1.18492,1.18496,1.18498,1.18492,1.18496,1.18501,1.18496,1.18522,1.18520,1.18536,1.18512,1.18512,1.18509,1.18506,1.18515,1.18516,1.18513,1.18513,1.18512,1.18512,1.18512,1.18506,1.18504,1.18504,1.18494,1.18488,1.18483,1.18486,1.18485,1.18478,1.18475,1.18475,1.18475,1.18506,1.18508,1.18513,1.18510,1.18512,1.18474,1.18473,1.18474,1.18474,1.18462,1.18452,1.18419,1.18427,1.18424,1.18420,1.18418,1.18454,1.18472,1.18462,1.18421,1.18423};
    public static final List<Integer> expectedHighPivots = List.of(1,4,8,11,13,15,17,21,23,25,26,29,33,37,41,43,46,47,51,56,59); // 13, 15, 29
    public static final List<Integer> expectedLowPivots = List.of(0,3,6,9,12,14,16,19,22,24,25,28,32,36,37,38,42,45,50,54,58);
    public static final List<Double> expectedHighDispersions = List.of(0.00014,0.00010,0.00006,0.00009,0.00030,0.00044,0.00029,0.00041,0.00038,0.00037,0.00037,0.00030,0.00024,0.00056,0.00095,0.00094,0.00056,0.00056,0.00009,0.00054,0.00005);
    public static final List<Double> expectedLowDispersions = List.of(0.00035,0.00036,0.00044,0.00044,0.00040,0.00016,0.00024,0.00030,0.00023,0.00024,0.00024,0.00032,0.00033,0.00041,0.00038,0.00038,0.00003,0.00040,0.00094,0.00095,0.00091);
    public static final List<Integer> expectedHighConsolidations = List.of(15,41);
    public static final List<Integer> expectedLowConsolidations = List.of(9, 54);

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
