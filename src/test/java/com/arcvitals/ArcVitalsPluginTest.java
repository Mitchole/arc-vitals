package com.arcvitals;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ArcVitalsPluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(ArcVitalsPlugin.class);
        RuneLite.main(args);
    }
}
