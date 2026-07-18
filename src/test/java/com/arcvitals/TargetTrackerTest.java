package com.arcvitals;

import net.runelite.api.Actor;
import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class TargetTrackerTest {

    @Test
    public void interactingSetsTheCurrentTarget() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        t.onInteracting(a, 100);
        assertSame(a, t.current());
    }

    @Test
    public void nullInteractingDoesNotClearImmediately() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        t.onInteracting(a, 100);
        t.onInteracting(null, 101);
        assertSame(a, t.current());
    }

    @Test
    public void targetPersistsThroughTheLingerWindow() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        t.onInteracting(a, 100);
        // no longer interacting; still within LINGER_TICKS (8) of tick 100
        t.onGameTick(null, 108);
        assertSame(a, t.current());
    }

    @Test
    public void targetClearsAfterTheLingerWindow() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        t.onInteracting(a, 100);
        t.onGameTick(null, 109); // 9 > 8
        assertNull(t.current());
    }

    @Test
    public void stillInteractingKeepsTheTargetFreshAndResetsTheTimer() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        t.onInteracting(a, 100);
        t.onGameTick(a, 120);    // still interacting -> lastSeen = 120
        t.onGameTick(null, 128); // 8 since 120, within window
        assertSame(a, t.current());
        t.onGameTick(null, 129); // 9 since 120
        assertNull(t.current());
    }

    @Test
    public void aNewTargetReplacesTheOldOne() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        Actor b = mock(Actor.class);
        t.onInteracting(a, 100);
        t.onInteracting(b, 103);
        assertSame(b, t.current());
    }

    @Test
    public void resetClearsImmediately() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        t.onInteracting(a, 100);
        t.reset();
        assertNull(t.current());
    }

    @Test
    public void gameTickFromANewTargetSetsItEvenWithoutAnEvent() {
        TargetTracker t = new TargetTracker();
        Actor a = mock(Actor.class);
        t.onGameTick(a, 100);
        assertSame(a, t.current());
    }
}
