package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestRange {

    @Test
    public void TestRange_MinMax() {
        List<PriceBar> bars = Context.getPriceBarList(10);
        Range range = new Range(10, bars);

        Assertions.assertEquals(311.26, range.getRangeMax().orElse(0.0), 0.001);
        Assertions.assertEquals(302.09, range.getRangeMin().orElse(0.0), 0.001);

        for (int i = 10; i < Context.data.length; i++) {
            range.slide(new PriceBar("TEST", Context.data[i]));
        }

        Assertions.assertEquals(311.26, range.getRangeMax().orElse(0.0), 0.001);
        Assertions.assertEquals(300.42, range.getRangeMin().orElse(0.0), 0.001);
    }

}
