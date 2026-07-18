package com.arcvitals;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

// Per-vital "detached" configuration, grouped so the Vital constructor does not grow by five extra
// arguments. Holds the three config accessors plus the two config keys used to write the bar's
// position back on drag release.
final class DetachSpec {

    private final Predicate<ArcVitalsConfig> detached;
    private final ToIntFunction<ArcVitalsConfig> x;
    private final ToIntFunction<ArcVitalsConfig> y;
    private final String keyX;
    private final String keyY;

    DetachSpec(Predicate<ArcVitalsConfig> detached, ToIntFunction<ArcVitalsConfig> x,
               ToIntFunction<ArcVitalsConfig> y, String keyX, String keyY) {
        this.detached = detached;
        this.x = x;
        this.y = y;
        this.keyX = keyX;
        this.keyY = keyY;
    }

    boolean detached(ArcVitalsConfig config) {
        return detached.test(config);
    }

    int x(ArcVitalsConfig config) {
        return x.applyAsInt(config);
    }

    int y(ArcVitalsConfig config) {
        return y.applyAsInt(config);
    }

    String keyX() {
        return keyX;
    }

    String keyY() {
        return keyY;
    }
}
