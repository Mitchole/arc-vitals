package com.arcvitals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Structural invariants for the config panel. These hold before AND after the settings
// reorganisation, so the test guards the reshuffle: a wrong/duplicate/gapped position or an item
// left pointing at a deleted section fails here rather than silently mis-ordering the panel.
public class ConfigIntegrityTest {

    @Test
    public void sectionPositionsAreUniqueAndContiguous() {
        List<Integer> positions = new ArrayList<>();
        for (Field f : ArcVitalsConfig.class.getDeclaredFields()) {
            ConfigSection s = f.getAnnotation(ConfigSection.class);
            if (s != null) {
                positions.add(s.position());
            }
        }
        // Non-vacuity guard: reflection must actually find the sections, else the checks below pass
        // over nothing. The floor is well under the real count so a legitimate reshuffle never trips it.
        assertTrue("expected the config's @ConfigSection fields; reflection found " + positions.size(),
            positions.size() >= 10);
        assertContiguousFromZero("sections", positions);
    }

    @Test
    public void itemKeyNamesAreUnique() {
        Set<String> seen = new HashSet<>();
        for (Method m : ArcVitalsConfig.class.getMethods()) {
            ConfigItem it = m.getAnnotation(ConfigItem.class);
            if (it != null) {
                assertTrue("duplicate keyName: " + it.keyName(), seen.add(it.keyName()));
            }
        }
        // Non-vacuity guard: reflection must actually find the items (see section note above).
        assertTrue("expected many @ConfigItem methods; reflection found " + seen.size(), seen.size() >= 50);
    }

    @Test
    public void everyItemSectionIsDeclared() throws Exception {
        Set<String> declared = new HashSet<>();
        for (Field f : ArcVitalsConfig.class.getDeclaredFields()) {
            if (f.getAnnotation(ConfigSection.class) != null) {
                declared.add((String) f.get(null));
            }
        }
        for (Method m : ArcVitalsConfig.class.getMethods()) {
            ConfigItem it = m.getAnnotation(ConfigItem.class);
            if (it != null) {
                // Every item must belong to a section, and that section must be declared. Requiring a
                // non-empty section here also catches an item that loses its section= during a reshuffle.
                assertTrue("item " + it.keyName() + " has no section", !it.section().isEmpty());
                assertTrue("item " + it.keyName() + " references undeclared section '" + it.section() + "'",
                    declared.contains(it.section()));
            }
        }
    }

    @Test
    public void itemPositionsPerSectionAreUniqueAndContiguous() {
        Map<String, List<Integer>> bySection = new HashMap<>();
        for (Method m : ArcVitalsConfig.class.getMethods()) {
            ConfigItem it = m.getAnnotation(ConfigItem.class);
            if (it != null && !it.section().isEmpty()) {
                bySection.computeIfAbsent(it.section(), k -> new ArrayList<>()).add(it.position());
            }
        }
        for (Map.Entry<String, List<Integer>> e : bySection.entrySet()) {
            assertContiguousFromZero("section " + e.getKey(), e.getValue());
        }
    }

    private static void assertContiguousFromZero(String what, List<Integer> positions) {
        Set<Integer> unique = new TreeSet<>(positions);
        assertEquals(what + " has duplicate positions: " + positions, positions.size(), unique.size());
        int expected = 0;
        for (int p : unique) {
            assertEquals(what + " positions not contiguous from 0: " + unique, expected, p);
            expected++;
        }
    }
}
