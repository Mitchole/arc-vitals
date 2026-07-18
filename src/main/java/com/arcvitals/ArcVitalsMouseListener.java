package com.arcvitals;

import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.MouseAdapter;

// Drives the offset of whichever HUD unit is grabbed - the main group or a detached bar - from an
// Alt + left-drag. All grab state and target selection live in the HudDragController; this shell only
// translates mouse events, consumes them during a grab so the game does not react, and writes the
// dragged target's final offset to its config keys once on release.
public class ArcVitalsMouseListener extends MouseAdapter {

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
            && controller.begin(e.getX(), e.getY())) {
            e.consume();
        }
        return e;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent e) {
        if (controller.isDragging()) {
            controller.update(e.getX(), e.getY());
            e.consume();
        }
        return e;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent e) {
        if (controller.isDragging()) {
            // Commit the dragged target's final offset while still dragging so the overlay never reads
            // a stale config value in the frame between clearing the drag and the config write landing.
            configManager.setConfiguration("arcvitals", controller.activeKeyX(), controller.liveOffsetX());
            configManager.setConfiguration("arcvitals", controller.activeKeyY(), controller.liveOffsetY());
            controller.end();
            e.consume();
        }
        return e;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent e) {
        if (config.dragToMove() && e.isAltDown()) {
            controller.updateArmed(e.getX(), e.getY());
        } else {
            controller.clearArmed();
        }
        return e;
    }
}
