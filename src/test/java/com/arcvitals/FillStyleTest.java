package com.arcvitals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FillStyleTest {

    private static final Color FILL = new Color(0, 200, 0);

    private static Geometry left() {
        return new ArcGeometry(200, 200, 140, 12, 70, 4, 110, 0, true, true);
    }

    @Test
    public void smoothPaintsInsideBodyOnly() {
        Geometry geo = left();
        BufferedImage img = render(geo, FillStyle.SMOOTH, FillDirection.BOTTOM_UP, 0.5, FILL);
        Rectangle b = geo.body().getBounds();
        int painted = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (opaque(img, x, y)) {
                    painted++;
                    assertTrue("painted pixel outside body bounds at " + x + "," + y,
                        x >= b.x - 2 && x <= b.x + b.width + 2 && y >= b.y - 2 && y <= b.y + b.height + 2);
                }
            }
        }
        assertTrue("smooth should paint some pixels", painted > 0);
    }

    @Test
    public void gradientIsBrighterAtTheAnchorEnd() {
        Geometry geo = left();
        BufferedImage img = render(geo, FillStyle.GRADIENT, FillDirection.BOTTOM_UP, 1.0, FILL);
        double[] bottom = geo.pointAt(0.05); // near the anchored (bottom) end
        double[] top = geo.pointAt(0.95);    // near the far (top) end
        int atBottom = avgBrightness(img, (int) bottom[0], (int) bottom[1], 3);
        int atTop = avgBrightness(img, (int) top[0], (int) top[1], 3);
        assertTrue("both ends should be painted", atBottom >= 0 && atTop >= 0);
        assertTrue("anchor end brighter than far end", atBottom > atTop);
    }

    @Test
    public void glossIsBrighterOnTheCentrelineThanTheEdge() {
        Geometry geo = left();
        BufferedImage img = render(geo, FillStyle.GLOSS, FillDirection.BOTTOM_UP, 1.0, FILL);
        double[] mid = geo.pointAt(0.5);
        double[] n = geo.normalAt(0.5);
        int t = geo.thickness();
        int edgeX = (int) (mid[0] + n[0] * (t / 2.0 - 1));
        int edgeY = (int) (mid[1] + n[1] * (t / 2.0 - 1));
        int centre = avgBrightness(img, (int) mid[0], (int) mid[1], 1);
        int edge = avgBrightness(img, edgeX, edgeY, 1);
        assertTrue("centreline and edge both painted", centre >= 0 && edge >= 0);
        assertTrue("gloss highlight brighter at centre", centre > edge);
    }

    @Test
    public void segmentedLeavesGapsAlongTheFilledSpan() {
        Geometry geo = left();
        BufferedImage img = render(geo, FillStyle.SEGMENTED, FillDirection.BOTTOM_UP, 1.0, FILL);
        boolean sawPip = false;
        boolean sawGap = false;
        for (int i = 1; i < 40; i++) {
            double f = i / 40.0;
            double[] p = geo.pointAt(f);
            if (opaque(img, (int) p[0], (int) p[1])) {
                sawPip = true;
            } else {
                sawGap = true;
            }
        }
        assertTrue("segmented should paint some pips", sawPip);
        assertTrue("segmented should leave some gaps", sawGap);
    }

    // ---- shared helpers (reused by Tasks 6-10) ----

    static BufferedImage render(Geometry geo, FillStyle style, FillDirection dir, double frac, Color color) {
        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        style.paint(g, geo, dir, frac, color, color);
        g.dispose();
        return img;
    }

    static boolean opaque(BufferedImage img, int x, int y) {
        if (x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight()) {
            return false;
        }
        return (img.getRGB(x, y) >>> 24) > 20;
    }

    // Average (r+g+b) over opaque pixels in a (2*rad+1) box around (x, y); -1 if none opaque.
    static int avgBrightness(BufferedImage img, int x, int y, int rad) {
        long sum = 0;
        int n = 0;
        for (int dx = -rad; dx <= rad; dx++) {
            for (int dy = -rad; dy <= rad; dy++) {
                int px = x + dx;
                int py = y + dy;
                if (!opaque(img, px, py)) {
                    continue;
                }
                int argb = img.getRGB(px, py);
                sum += ((argb >> 16) & 0xFF) + ((argb >> 8) & 0xFF) + (argb & 0xFF);
                n++;
            }
        }
        return n == 0 ? -1 : (int) (sum / n);
    }
}
