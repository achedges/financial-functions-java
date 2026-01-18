package com.adamhedges.financial.utils;

public class ComparisonUtils {

    public static <T extends Comparable<T>> boolean between(T value, T lowerBound, T upperBound) {
        return lowerBound.compareTo(value) <= 0 && upperBound.compareTo(value) >= 0;
    }

}
