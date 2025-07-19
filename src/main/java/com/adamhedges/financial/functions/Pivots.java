package com.adamhedges.financial.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Pivots {

    public static <T> List<Integer> getPivots(List<T> values, BiFunction<T, T, Boolean> comparison) {
        int nbars = values.size();
        List<Integer> pivots = new ArrayList<>();

        for (int i = 0; i < nbars; i++) {
            T c = values.get(i);
            T l = i == 0 ? null : values.get(i-1);
            T r = i == nbars - 1 ? null : values.get(i+1);

            if (l == null && r == null) {
                return pivots;
            } else if (l != null & r != null) {
                if (comparison.apply(c, l) && comparison.apply(c, r)) {
                    pivots.add(i);
                }
            } else if (l == null && comparison.apply(c, r)) {
                pivots.add(i);
            } else if (r == null && comparison.apply(c, l)) {
                pivots.add(i);
            }
        }

        return pivots;
    }

    public static <T> List<Double> getDispersions(List<T> values, List<Integer> pivotIndexes, int span, BiFunction<T, T, Double> diff) {
        int nbars = values.size();
        List<Double> dispersions = new ArrayList<>(pivotIndexes.size());

        for (Integer pivotIndex : pivotIndexes) {
            double dispersion = 0.0;
            T peakBar = values.get(pivotIndex);

            for (int i = 1; i <= span; i++) {
                double leftspan = pivotIndex - i < 0 ? 0.0 : diff.apply(peakBar, values.get(pivotIndex-i));
                double rightspan = pivotIndex + i >= nbars ? 0.0 : diff.apply(peakBar, values.get(pivotIndex+i));
                double maxspan = Math.max(leftspan, rightspan);
                if (maxspan > dispersion) {
                    dispersion = maxspan;
                }
            }

            dispersions.add(dispersion);
        }

        return dispersions;
    }

    public static List<Integer> consolidatePivots(List<Integer> pivotIndexes, List<Double> dispersions, int span) {
        if (pivotIndexes.size() != dispersions.size()) {
            throw new RuntimeException("'pivotIndexes' and 'dispersions' arguments are not the same size");
        }

        List<Integer> consolidated = new ArrayList<>(pivotIndexes);
        for (int i = 0; i < consolidated.size() - 1; i++) {
            if (consolidated.get(i) == -1) {
                continue;
            }

            if (consolidated.get(i+1) - consolidated.get(i) <= span) {
                if (dispersions.get(i) > dispersions.get(i+1)) {
                    consolidated.set(i+1, -1);
                } else {
                    consolidated.set(i, -1);
                }
            }
        }

        return consolidated.stream().filter(c -> c >= 0).toList();
    }

    public static <T> List<Integer> get(
        List<T> values,
        BiFunction<T, T, Boolean> comparison,
        BiFunction<T, T, Double> dispersion,
        int dispersionSpan,
        int consolidationSpan
    ) {
        List<Integer> rawPivots = Pivots.getPivots(values, comparison);
        List<Double> dispersions = Pivots.getDispersions(values, rawPivots, dispersionSpan, dispersion);
        return Pivots.consolidatePivots(rawPivots, dispersions, consolidationSpan);
    }

}
