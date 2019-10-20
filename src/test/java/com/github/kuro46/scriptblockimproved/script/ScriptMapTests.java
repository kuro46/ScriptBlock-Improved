package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.OptionList;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import java.util.Collections;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptMapTests {

    private Script newDummyScript() {
        return new Script(
                -1,
                TriggerName.of("trigger"),
                Author.console(),
                new BlockPosition("world", 0, 0, 0),
                new OptionList(Collections.emptyList()));
    }

    @Test
    void modify3Times() {
        final ScriptMap scriptMap = new ScriptMap();
        final MutableInt callCounter = new MutableInt();
        scriptMap.addListener(ignored -> callCounter.increment());

        final Script script = newDummyScript();

        scriptMap.add(script);
        scriptMap.addAll(new ScriptMap(scriptMap.asListMultimap()));
        scriptMap.removeAll(script.getPosition());

        assertEquals(callCounter.intValue(), 3);
    }

    @Test
    void removeFromEmptyScripts() {
        final ScriptMap scriptMap = new ScriptMap();
        assertThrows(
            IllegalArgumentException.class,
            () -> scriptMap.removeAll(new BlockPosition("worldName", 0, 0, 0)));
    }

    @Test
    void removeScript() {
        final ScriptMap scriptMap = new ScriptMap();
        final Script script = newDummyScript();
        scriptMap.add(script);
        scriptMap.removeAll(script.getPosition());
        assertTrue(scriptMap.asListMultimap().isEmpty());
    }

    @Test
    void addScript() {
        final ScriptMap scriptMap = new ScriptMap();
        final Script script = newDummyScript();
        scriptMap.add(script);
        assertTrue(scriptMap.asListMultimap().size() == 1);
    }

    @Test
    void addAllScripts() {
        final ScriptMap scriptMap = new ScriptMap();
        for (int i = 0; i < 10; i++) {
            scriptMap.add(newDummyScript());
        }
        final ScriptMap base = new ScriptMap();
        base.addAll(scriptMap);
        assertTrue(base.asListMultimap().size() == scriptMap.asListMultimap().size());
    }

    @Test
    void containsFound() {
        final ScriptMap scriptMap = new ScriptMap();
        final Script script = newDummyScript();
        scriptMap.add(script);
        assertTrue(scriptMap.contains(script.getPosition()));
    }

    @Test
    void containsNotFound() {
        final ScriptMap scriptMap = new ScriptMap();
        assertFalse(scriptMap.contains(new BlockPosition("worldName", 0, 0, 0)));
    }

    @Test
    void build() {
        final ScriptMap.Builder builder = ScriptMap.builder();
        for (int i = 0; i < 10; i++) {
            builder.add(newDummyScript());
        }
        final ScriptMap scriptMap = builder.build();
        assertTrue(scriptMap.asListMultimap().size() == 10);
    }
}
