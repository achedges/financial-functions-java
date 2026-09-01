package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestRange {

    @Test
    public void TestRange_MinMax() {
        int period = 4;
        Range range = new Range(period, Context.getPriceBarList(period));

        Assertions.assertEquals(305.12, range.getRangeMax().orElse(0.0), 0.001);
        Assertions.assertEquals(302.09, range.getRangeMin().orElse(0.0), 0.001);

        double[] expectedMax = {306.17, 309.01, 310.24, 311.26, 311.26, 311.26, 311.26, 310.35, 307.40, 307.40, 307.40, 307.40};
        double[] expectedMin = {302.09, 302.09, 302.09, 306.17, 309.01, 303.26, 300.42, 300.42, 300.42, 300.42, 303.50, 300.80};

        for (int i = 0; i < Context.data.length - period; i++) {
            range.slide(new PriceBar("TEST", Context.data[i+period]));
            Assertions.assertEquals(expectedMax[i], range.getRangeMax().orElse(0.0), 0.001);
            Assertions.assertEquals(expectedMin[i], range.getRangeMin().orElse(0.0), 0.001);
        }
    }

}
