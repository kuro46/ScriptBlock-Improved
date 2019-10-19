package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import java.util.Collections;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptsTests {

    private Script newDummyScript() {
        return new Script(
                -1,
                TriggerName.of("trigger"),
                Author.console(),
                new BlockPosition("world", 0, 0, 0),
                new Options(Collections.emptyList()));
    }

    @Test
    void modify3Times() {
        final Scripts scripts = new Scripts();
        final MutableInt callCounter = new MutableInt();
        scripts.addListener(ignored -> callCounter.increment());

        final Script script = newDummyScript();

        scripts.add(script);
        scripts.addAll(new Scripts(scripts.getView()));
        scripts.removeAll(script.getPosition());

        assertEquals(callCounter.intValue(), 3);
    }

    @Test
    void removeFromEmptyScripts() {
        final Scripts scripts = new Scripts();
        assertThrows(
            IllegalArgumentException.class,
            () -> scripts.removeAll(new BlockPosition("worldName", 0, 0, 0)));
    }

    @Test
    void removeScript() {
        final Scripts scripts = new Scripts();
        final Script script = newDummyScript();
        scripts.add(script);
        scripts.removeAll(script.getPosition());
        assertTrue(scripts.getView().isEmpty());
    }

    @Test
    void addScript() {
        final Scripts scripts = new Scripts();
        final Script script = newDummyScript();
        scripts.add(script);
        assertTrue(scripts.getView().size() == 1);
    }

    @Test
    void addAllScripts() {
        final Scripts scripts = new Scripts();
        for (int i = 0; i < 10; i++) {
            scripts.add(newDummyScript());
        }
        final Scripts base = new Scripts();
        base.addAll(scripts);
        assertTrue(base.getView().size() == scripts.getView().size());
    }

    @Test
    void containsFound() {
        final Scripts scripts = new Scripts();
        final Script script = newDummyScript();
        scripts.add(script);
        assertTrue(scripts.contains(script.getPosition()));
    }

    @Test
    void containsNotFound() {
        final Scripts scripts = new Scripts();
        assertFalse(scripts.contains(new BlockPosition("worldName", 0, 0, 0)));
    }

    @Test
    void build() {
        final Scripts.Builder builder = Scripts.builder();
        for (int i = 0; i < 10; i++) {
            builder.add(newDummyScript());
        }
        final Scripts scripts = builder.build();
        assertTrue(scripts.getView().size() == 10);
    }
}
