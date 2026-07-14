package com.arcvitals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;

final class ArcBar {

    private ArcBar() {
    }

    // Immutable per-bar geometry: the stroked capsule plus the data draw() needs to fill by angle.
    // Angles are Arc2D degrees (0 = east, positive counterclockwise on screen).
    static final class Geometry {
        final Shape capsule;
        final double centerX;
        final double centerY;
        final double radius;
        final int thickness;
        final double topAngle;
        final double bottomAngle;

        Geometry(Shape capsule, double centerX, double centerY, double radius, int thickness,
                 double topAngle, double bottomAngle) {
            this.capsule = capsule;
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            this.thickness = thickness;
            this.topAngle = topAngle;
            this.bottomAngle = bottomAngle;
        }
    }

    // Builds the geometry for a bar nested at the given index on its side.
    // Nested bars are concentric: they share the innermost bar's circle centre and grow in
    // radius by (thickness + spacing) per index, so the gap between them is uniform along the
    // whole arc (no overlap at the ends). Each bar's sweep is chosen so every bar keeps the
    // same height. At index 0 this reduces exactly to the single-bar geometry.
    // Positioned absolutely at (cx, cy); callers that cache the result must invalidate when
    // cx/cy or any layout param changes.
    static Geometry geometry(int cx, int cy, int size, int thickness, int baseGap, int spacing,
                             int curveDegrees, int index, boolean leftSide, boolean flatEnds) {
        double half = Math.toRadians(curveDegrees) / 2.0;
        double sinHalf = Math.sin(half);
        if (sinHalf < 1e-4) {
            sinHalf = 1e-4;
        }
        // Innermost radius and the shared circle centre both come from the base gap + curve.
        double baseRadius = (size / 2.0) / sinHalf;
        double circleCenterX = leftSide ? (cx - baseGap + baseRadius) : (cx + baseGap - baseRadius);
        double radius = baseRadius + index * (double) (thickness + spacing);

        // Hold the height constant: narrow the sweep as the radius grows so the arc still spans
        // `size` vertically. clamp guards a radius smaller than half the height (never at index 0).
        double sinSweepHalf = Math.min(1.0, (size / 2.0) / radius);
        double sweepDegrees = Math.toDegrees(Math.asin(sinSweepHalf) * 2.0);

        double boundX = circleCenterX - radius;
        double boundY = cy - radius;
        double diameter = radius * 2.0;
        double centreAngle = leftSide ? 180.0 : 0.0;
        double startAngle = centreAngle - (sweepDegrees / 2.0);
        double endAngle = centreAngle + (sweepDegrees / 2.0);
        Arc2D arc = new Arc2D.Double(boundX, boundY, diameter, diameter, startAngle, sweepDegrees, Arc2D.OPEN);

        int cap = flatEnds ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        Shape capsule = new BasicStroke(thickness, cap, BasicStroke.JOIN_ROUND).createStrokedShape(arc);

        // On the left the smaller angle (start) is the top tip; on the right it is the bottom tip.
        double topAngle = leftSide ? startAngle : endAngle;
        double bottomAngle = leftSide ? endAngle : startAngle;
        return new Geometry(capsule, circleCenterX, cy, radius, thickness, topAngle, bottomAngle);
    }

    static void draw(Graphics2D g, Geometry geo, FillDirection dir, double fraction,
                     Color fill, Color track, Color outlineColor, int outlineWidth,
                     double previewFraction, Color previewColor) {
        Object oldAa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = g.getStroke();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track: the whole capsule.
        g.setColor(track);
        g.fill(geo.capsule);

        double frac = clamp01(fraction);
        // Current fill: from the anchored end up to the current level, measured along the arc.
        fillWedge(g, geo, dir, 0.0, frac, fill);

        // Preview: from the current level up to the projected level.
        double prev = clamp01(previewFraction);
        if (previewColor != null && prev > frac) {
            fillWedge(g, geo, dir, frac, prev, previewColor);
        }

        // Outline: a border around the whole capsule perimeter, including the ends.
        if (outlineColor != null && outlineWidth > 0) {
            g.setColor(outlineColor);
            g.setStroke(new BasicStroke(outlineWidth));
            g.draw(geo.capsule);
        }

        g.setStroke(oldStroke);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            oldAa == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : oldAa);
    }

    // Fills the capsule within the angular band covering fractions [lo, hi] of the arc sweep,
    // measured from the anchored end (bottom for BOTTOM_UP, top for TOP_DOWN). The band is a pie
    // wedge from the circle centre, so both boundaries are radial (perpendicular to the arc) - a
    // crisp cut, not a horizontal slice. The true ends (lo<=0 / hi>=1) are padded by the stroke's
    // half-thickness in angle so the round/flat end caps fill cleanly; interior boundaries stay a
    // clean radial cut so the current fill and the preview band abut without overlap.
    private static void fillWedge(Graphics2D g, Geometry geo, FillDirection dir,
                                  double lo, double hi, Color color) {
        if (hi <= lo) {
            return;
        }
        double anchorAngle;
        double fullExtent;
        if (dir == FillDirection.BOTTOM_UP) {
            anchorAngle = geo.bottomAngle;
            fullExtent = geo.topAngle - geo.bottomAngle;
        } else {
            anchorAngle = geo.topAngle;
            fullExtent = geo.bottomAngle - geo.topAngle;
        }
        double sign = Math.signum(fullExtent);
        if (sign == 0.0) {
            return;
        }

        // Angular width (degrees) of the stroke's end cap at this radius.
        double padDeg = Math.toDegrees((geo.thickness / 2.0) / geo.radius) + 0.5;

        double startDeg = anchorAngle + fullExtent * lo;
        double endDeg = anchorAngle + fullExtent * hi;
        if (lo <= 0.0) {
            startDeg -= sign * padDeg;
        }
        if (hi >= 1.0) {
            endDeg += sign * padDeg;
        }

        double r = geo.radius + geo.thickness; // safely beyond the capsule's outer edge
        Arc2D wedge = new Arc2D.Double(geo.centerX - r, geo.centerY - r, r * 2.0, r * 2.0,
            startDeg, endDeg - startDeg, Arc2D.PIE);

        Area fillArea = new Area(geo.capsule);
        fillArea.intersect(new Area(wedge));
        g.setColor(color);
        g.fill(fillArea);
    }

    private static double clamp01(double v) {
        if (v < 0.0) {
            return 0.0;
        }
        return v > 1.0 ? 1.0 : v;
    }
}
