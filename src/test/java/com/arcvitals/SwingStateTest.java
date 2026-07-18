package com.arcvitals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SwingStateTest {

    @Test
    public void fractionIsElapsedOverCooldown() {
        // 4-tick weapon = 2400ms cooldown.
        assertEquals(0.0, SwingState.fraction(0, 4), 1e-9);
        assertEquals(0.5, SwingState.fraction(1200, 4), 1e-9);
        assertEquals(1.0, SwingState.fraction(2400, 4), 1e-9);
    }

    @Test
    public void fractionClampsAndGuards() {
        assertEquals(1.0, SwingState.fraction(9999, 4), 1e-9);
        assertEquals(0.0, SwingState.fraction(-50, 4), 1e-9);
        assertEquals(1.0, SwingState.fraction(100, 0), 1e-9); // no cooldown = full
    }

    @Test
    public void readyOnceCooldownElapsed() {
        assertFalse(SwingState.ready(2399, 4));
        assertTrue(SwingState.ready(2400, 4));
    }

    @Test
    public void showingWithinLingerWindow() {
        assertTrue(SwingState.showing(100, 100, 8));  // same tick
        assertTrue(SwingState.showing(108, 100, 8));  // edge of linger
        assertFalse(SwingState.showing(109, 100, 8)); // past linger
        assertFalse(SwingState.showing(50, 100, 8));  // negative (post-hop) = not showing
    }
}
