package com.github.kuro46.scriptblockimproved.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageUtilsTests {

    @Test
    void translateColorCodes() {
        final String source = "&1Hello, &z World!&2!";
        final String dest = MessageUtils.translateColorCodes('&', source);
        assertEquals(dest, "§1Hello, &z World!§2!");
    }
}
