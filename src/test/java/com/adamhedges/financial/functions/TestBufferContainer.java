package com.adamhedges.financial.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestBufferContainer {

    // Note: We're always working with a buffer that's been backfilled, so the initial index should always be the end of the buffer array.

    @Test
    public void TestBufferContainer_getLowerBound() {
        BufferContainer linear = new BufferContainer(3, 4, 10);
        int[] expectedLinear = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2 };
        for (int e : expectedLinear) {
            Assertions.assertEquals(e, linear.getLowerBound());
            linear.advance();
        }

        BufferContainer ring = new BufferContainer(3, 4, 4);
        int[] expectedRing = { 0, 1, 2, 3, 0, 1, 2, 3, 0, 1 };
        for (int e : expectedRing) {
            Assertions.assertEquals(e, ring.getLowerBound());
            ring.advance();
        }
    }

    @Test
    public void TestBufferContainer_getUpperBound() {
        BufferContainer linear = new BufferContainer(3, 4, 10);
        int[] expectedLinear = { 3, 4, 5, 6, 7, 8, 9, 0, 1, 2 };
        for (int e : expectedLinear) {
            Assertions.assertEquals(e, linear.getUpperBound());
            linear.advance();
        }

        linear.setIndex(3);

        int[] expectedLinearPad = { 4, 5, 6, 7, 8, 9, 10, 1, 2, 3 };
        for (int e : expectedLinearPad) {
            Assertions.assertEquals(e, linear.getUpperBound(1));
            linear.advance();
        }

        BufferContainer ring = new BufferContainer(3, 4, 4);
        int[] expectedRing = { 3, 0, 1, 2 };
        for (int e : expectedRing) {
            Assertions.assertEquals(e, ring.getUpperBound());
            ring.advance();
        }

        ring.setIndex(3);

        int[] expectedRingPad = { 4, 1, 2, 3 };
        for (int e : expectedRingPad) {
            Assertions.assertEquals(e, ring.getUpperBound(1));
            ring.advance();
        }
    }

    @Test
    public void TestBufferContainer_isRingBuffer() {
        Assertions.assertTrue(new BufferContainer(9, 10, 10).isRingBuffer());
        Assertions.assertFalse(new BufferContainer(9, 10, 20).isRingBuffer());
    }

    @Test
    public void TestBufferContainer_advance() {
        BufferContainer container = new BufferContainer(9, 10, 10);
        int[] expected = { 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 };
        for (int i : expected) {
            Assertions.assertEquals(i, container.getIndex());
            container.advance();
        }
    }

}
