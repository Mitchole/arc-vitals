package com.arcvitals;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BarAnimatorTest {

    // BarAnimator settles within 5e-4 of target; assert a hair looser than that.
    private static final double TOL = 1e-3;

    @Test
    public void glideZeroJumpsStraightToTarget() {
        assertEquals(0.7, BarAnimator.step(0.2, 0.7, 16, 0), 0.0);
        assertEquals(0.7, BarAnimator.step(0.2, 0.7, 16, -5), 0.0);
    }

    @Test
    public void atTargetDoesNotMove() {
        assertEquals(0.5, BarAnimator.step(0.5, 0.5, 16, 300), 0.0);
    }

    @Test
    public void zeroDeltaHoldsDisplayed() {
        assertEquals(0.3, BarAnimator.step(0.3, 0.7, 0, 300), 0.0);
    }

    @Test
    public void oneGainStepMovesPartwayUp() {
        double next = BarAnimator.step(0.2, 0.7, 16, 350);
        assertTrue("moved up", next > 0.2);
        assertTrue("did not overshoot", next < 0.7);
    }

    @Test
    public void oneDrainStepMovesPartwayDown() {
        double next = BarAnimator.step(0.7, 0.2, 16, 350);
        assertTrue("moved down", next < 0.7);
        assertTrue("did not overshoot", next > 0.2);
    }

    @Test
    public void hugeDeltaSettlesExactlyOnTargetWithoutOvershoot() {
        double next = BarAnimator.step(0.2, 0.7, 100_000, 350);
        assertEquals(0.7, next, 0.0);
        assertTrue(next <= 0.7);
    }

    @Test
    public void convergesMonotonicallyToTarget() {
        double d = 1.0;
        double last = d;
        // 5 glide-times of 16ms steps is far more than enough to settle.
        for (int i = 0; i < 63; i++) {
            d = BarAnimator.step(d, 0.0, 16, 200);
            assertTrue("monotonic non-increasing", d <= last + 1e-9);
            last = d;
        }
        assertEquals(0.0, d, TOL);
    }

    @Test
    public void frameRateIndependent() {
        double one = BarAnimator.step(0.2, 0.7, 100, 300);
        double many = 0.2;
        for (int i = 0; i < 10; i++) {
            many = BarAnimator.step(many, 0.7, 10, 300);
        }
        assertEquals(one, many, 1e-9);
    }
}
