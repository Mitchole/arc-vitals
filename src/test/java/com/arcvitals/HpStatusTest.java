package com.arcvitals;

import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HpStatusTest {

    private static final Color BASE = new Color(0, 200, 0);
    private static final Color POISON = new Color(0, 145, 0);
    private static final Color VENOM = new Color(0, 65, 0);

    @Test
    public void immunityTicksReadAsNone() {
        assertEquals(HpStatus.NONE, HpStatus.of(-5));
    }

    @Test
    public void zeroReadsAsNone() {
        assertEquals(HpStatus.NONE, HpStatus.of(0));
    }

    @Test
    public void positiveReadsAsPoisoned() {
        assertEquals(HpStatus.POISONED, HpStatus.of(1));
        assertEquals(HpStatus.POISONED, HpStatus.of(999_999));
    }

    @Test
    public void millionAndUpReadsAsVenomed() {
        assertEquals(HpStatus.VENOMED, HpStatus.of(1_000_000));
        assertEquals(HpStatus.VENOMED, HpStatus.of(1_000_040));
    }

    @Test
    public void resolvePicksTheMatchingColour() {
        assertEquals(BASE, HpStatus.resolve(HpStatus.NONE, true, BASE, POISON, VENOM));
        assertEquals(POISON, HpStatus.resolve(HpStatus.POISONED, true, BASE, POISON, VENOM));
        assertEquals(VENOM, HpStatus.resolve(HpStatus.VENOMED, true, BASE, POISON, VENOM));
    }

    @Test
    public void resolveDisabledKeepsBase() {
        assertEquals(BASE, HpStatus.resolve(HpStatus.VENOMED, false, BASE, POISON, VENOM));
    }
}
