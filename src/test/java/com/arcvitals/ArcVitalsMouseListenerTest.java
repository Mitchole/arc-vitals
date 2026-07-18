package com.arcvitals;

import java.awt.Canvas;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import net.runelite.client.config.ConfigManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArcVitalsMouseListenerTest {

    private final Canvas source = new Canvas();
    private ArcVitalsConfig config;
    private ConfigManager configManager;
    private HudDragController controller;
    private ArcVitalsMouseListener listener;

    @Before
    public void setUp() {
        config = mock(ArcVitalsConfig.class);
        configManager = mock(ConfigManager.class);
        controller = new HudDragController();
        controller.setBounds(new Rectangle(100, 100, 60, 80));
        when(config.dragToMove()).thenReturn(true);
        when(config.offsetX()).thenReturn(0);
        when(config.offsetY()).thenReturn(0);
        listener = new ArcVitalsMouseListener(config, configManager, controller);
    }

    private MouseEvent press(int mods, int x, int y) {
        return new MouseEvent(source, MouseEvent.MOUSE_PRESSED, 0L, mods, x, y, 1, false, MouseEvent.BUTTON1);
    }

    private MouseEvent drag(int x, int y) {
        return new MouseEvent(source, MouseEvent.MOUSE_DRAGGED, 0L, InputEvent.BUTTON1_DOWN_MASK, x, y, 0, false, MouseEvent.NOBUTTON);
    }

    private MouseEvent release(int x, int y) {
        return new MouseEvent(source, MouseEvent.MOUSE_RELEASED, 0L, 0, x, y, 1, false, MouseEvent.BUTTON1);
    }

    @Test
    public void altLeftPressOnHudBeginsAndConsumes() {
        MouseEvent e = press(InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK, 120, 120);
        listener.mousePressed(e);
        assertTrue(controller.isDragging());
        assertTrue(e.isConsumed());
    }

    @Test
    public void pressWithoutAltIsIgnored() {
        MouseEvent e = press(InputEvent.BUTTON1_DOWN_MASK, 120, 120);
        listener.mousePressed(e);
        assertFalse(controller.isDragging());
        assertFalse(e.isConsumed());
    }

    @Test
    public void pressOffTheHudIsIgnored() {
        MouseEvent e = press(InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK, 5, 5);
        listener.mousePressed(e);
        assertFalse(controller.isDragging());
        assertFalse(e.isConsumed());
    }

    @Test
    public void pressIgnoredWhenFeatureDisabled() {
        when(config.dragToMove()).thenReturn(false);
        MouseEvent e = press(InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK, 120, 120);
        listener.mousePressed(e);
        assertFalse(controller.isDragging());
        assertFalse(e.isConsumed());
    }

    @Test
    public void releaseCommitsTheClampedOffsetAndConsumes() {
        listener.mousePressed(press(InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK, 120, 120));
        listener.mouseDragged(drag(150, 140)); // +30 x, +20 y from the grab
        MouseEvent up = release(150, 140);
        listener.mouseReleased(up);
        verify(configManager).setConfiguration("arcvitals", "offsetX", 30);
        verify(configManager).setConfiguration("arcvitals", "offsetY", 20);
        assertTrue(up.isConsumed());
        assertFalse(controller.isDragging());
    }

    @Test
    public void releaseWithoutADragDoesNotWriteConfig() {
        MouseEvent up = release(150, 140);
        listener.mouseReleased(up);
        verify(configManager, never()).setConfiguration(eq("arcvitals"), eq("offsetX"), anyInt());
        assertFalse(up.isConsumed());
    }

    @Test
    public void altMoveOnHudArmsTheOutline() {
        MouseEvent e = new MouseEvent(source, MouseEvent.MOUSE_MOVED, 0L, InputEvent.ALT_DOWN_MASK, 120, 120, 0, false, MouseEvent.NOBUTTON);
        listener.mouseMoved(e);
        assertTrue(controller.showOutline());
        assertFalse(e.isConsumed());
    }
}
