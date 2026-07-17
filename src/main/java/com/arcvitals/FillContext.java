package com.arcvitals;

import java.awt.Color;
import java.awt.Paint;

// Inputs a FillStyle needs to paint the fill region: the base paint (solid colour, or a tinted
// pattern texture), the solid bar colour used to derive highlights, and the Segmented pip count.
final class FillContext {

    final Paint base;
    final Color color;
    final int segments;

    FillContext(Paint base, Color color, int segments) {
        this.base = base;
        this.color = color;
        this.segments = segments;
    }
}
