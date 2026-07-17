package com.arcvitals;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

// Resolves a (pattern, colour) to the base fill Paint. NONE is a solid colour; every other pattern
// is a TexturePaint wrapping the pattern's tinted tile, cached by (pattern, colour) since the tile
// is thickness-independent. The cache is bounded by the handful of distinct bar colours in play.
final class PatternPaints {

    private final Map<Long, TexturePaint> cache = new HashMap<>();

    Paint resolve(BarPattern pattern, Color color) {
        if (pattern == BarPattern.NONE) {
            return color;
        }
        long key = ((long) pattern.ordinal() << 32) | (color.getRGB() & 0xFFFFFFFFL);
        return cache.computeIfAbsent(key, k -> {
            BufferedImage tile = pattern.tile(color);
            return new TexturePaint(tile, new Rectangle(0, 0, tile.getWidth(), tile.getHeight()));
        });
    }
}
