package com.adamhedges.daytrading.library.functions;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BufferContainer {

    private int index;
    private int period;
    private int length;

    public int getLowerBound() {
        return (index - period + 1) % length;
    }

    public int getUpperBound() {
        return getUpperBound(0);
    }
    public int getUpperBound(int pad) {
        // pad is used to generate an exclusive upper bound, useful for feeding directly into subList()
        return (index % length) + pad;
    }

    public boolean isRingBuffer() {
        return period == length;
    }

    public void advance() {
        index++;
    }

}
