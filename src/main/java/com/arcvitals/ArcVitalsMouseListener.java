package com.arcvitals;

import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.MouseAdapter;

// Drives the HUD's offsetX/offsetY from an Alt + left-drag. All grab state lives in the
// HudDragController; this shell only translates mouse events, consumes them during a grab so the
// game does not react, and writes the final offset to config once on release.
public class ArcVitalsMouseListener extends MouseAdapter {

    // Mirror the @Range on offsetX/offsetY in ArcVitalsConfig; kept in sync by hand because the
    // annotation values cannot be read at runtime without reflection (banned in src/main).
    private static final int OFFSET_MIN = -500;
    private static final int OFFSET_MAX = 500;

    private final ArcVitalsConfig config;
    private final ConfigManager configManager;
    private final HudDragController controller;

    @Inject
    ArcVitalsMouseListener(ArcVitalsConfig config, ConfigManager configManager, HudDragController controller) {
        this.config = config;
        this.configManager = configManager;
        this.controller = controller;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent e) {
        if (config.dragToMove() && e.isAltDown() && SwingUtilities.isLeftMouseButton(e)
            && controller.begin(e.getX(), e.getY(), config.offsetX(), config.offsetY(), OFFSET_MIN, OFFSET_MAX)) {
            e.consume();
        }
        return e;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent e) {
        if (controller.isDragging()) {
            controller.update(e.getX(), e.getY(), OFFSET_MIN, OFFSET_MAX);
            e.consume();
        }
        return e;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent e) {
        if (controller.isDragging()) {
            int[] fin = controller.end();
            configManager.setConfiguration("arcvitals", "offsetX", fin[0]);
            configManager.setConfiguration("arcvitals", "offsetY", fin[1]);
            e.consume();
        }
        return e;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent e) {
        controller.setArmed(config.dragToMove() && e.isAltDown()
            && controller.hitsBounds(e.getX(), e.getY()));
        return e;
    }
}
