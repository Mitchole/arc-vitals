package com.arcvitals;

import java.awt.Shape;
import java.awt.geom.Area;

// Per-bar geometry shared by every BarShape. Immutable once built. The bar runs between two
// tips; f = 0 is the geometric BOTTOM tip and f = 1 the TOP tip, independent of fill direction.
interface Geometry {

    // The full stroked outline: filled for the track, stroked for the outline, and the
    // intersection target for every fill region.
    Shape body();

    // The sub-area of body() covering the fraction band [lo, hi], measured from the anchored
    // end chosen by dir (bottom for BOTTOM_UP, top for TOP_DOWN). Empty when hi <= lo.
    Area fillRegion(double lo, double hi, FillDirection dir);

    // The centreline down the middle of the stroke, for styles that draw along the bar.
    Shape centerline();

    // Stroke width of the bar in pixels.
    int thickness();

    // On-screen point {x, y} at fraction f along the bar (f = 0 bottom tip, f = 1 top tip).
    double[] pointAt(double f);

    // Unit normal {nx, ny} at fraction f, perpendicular to the centreline, for tick orientation.
    double[] normalAt(double f);
}
