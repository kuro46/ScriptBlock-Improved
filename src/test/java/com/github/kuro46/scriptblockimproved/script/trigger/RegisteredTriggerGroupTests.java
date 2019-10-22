package com.github.kuro46.scriptblockimproved.script.trigger;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisteredTriggerGroupTests {

    @Test
    void unregisterListenerTest() {
        final TriggerRegistry registry = new TriggerRegistry();
        final RegisteredTrigger trigger1 = registry.register("test trigger1");
        final RegisteredTrigger trigger2 = registry.register("test trigger2");
        final MutableInt unregisterCount = new MutableInt();
        final RegisteredTriggerGroup group = RegisteredTriggerGroup.builder()
            .add(trigger1)
            .add(trigger2)
            .build();
        group.onUnregisteredAll(unregisterCount::increment);
        registry.unregister(trigger1.getName());
        registry.unregister(trigger2.getName());

        assertTrue(unregisterCount.intValue() == 1);
    }
}
