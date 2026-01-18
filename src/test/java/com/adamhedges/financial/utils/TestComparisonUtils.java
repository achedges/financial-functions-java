package com.adamhedges.financial.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestComparisonUtils {

    @Test
    public void TestComparisonUtils_between() {
        Assertions.assertTrue(ComparisonUtils.between(2, 1, 3));
        Assertions.assertTrue(ComparisonUtils.between(1, 1, 1));
        Assertions.assertFalse(ComparisonUtils.between(3, 1, 2));
        Assertions.assertTrue(ComparisonUtils.between("b", "a", "c"));
        Assertions.assertFalse(ComparisonUtils.between("c", "a", "b"));
    }

}
