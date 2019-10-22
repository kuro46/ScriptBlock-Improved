package com.github.kuro46.scriptblockimproved.script.trigger;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisteredTriggerTests {

    @Test
    void unregisterListenerTest() {
        final TriggerRegistry registry = new TriggerRegistry();
        final TriggerName name = TriggerName.of("test trigger");
        final RegisteredTrigger trigger = registry.register(name);
        final MutableInt unregisterCount = new MutableInt();
        trigger.onUnregistered(unregisterCount::increment);
        registry.unregister(name);

        assertTrue(unregisterCount.intValue() == 1);
    }
}
