package com.github.kuro46.scriptblockimproved.common;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListUtilsTests {

    private List<String> newOneElementList() {
        return Collections.singletonList("1st element");
    }

    @Test
    void isPresentTest() {
        final List<String> oneElementList = newOneElementList();
        assertTrue(ListUtils.isPresent(oneElementList, 0));
        assertFalse(ListUtils.isPresent(oneElementList, 1));
        assertFalse(ListUtils.isPresent(oneElementList, -1));

        assertFalse(ListUtils.isPresent(emptyList(), 0));
        assertFalse(ListUtils.isPresent(emptyList(), 1));
        assertFalse(ListUtils.isPresent(emptyList(), -1));
    }

    @Test
    void getTest() {
        final List<String> oneElementList = newOneElementList();
        assertTrue(ListUtils.get(oneElementList, 0).isPresent());
        assertFalse(ListUtils.get(oneElementList, -1).isPresent());
        assertFalse(ListUtils.get(oneElementList, 1).isPresent());

        assertFalse(ListUtils.get(emptyList(), 0).isPresent());
        assertFalse(ListUtils.get(emptyList(), -1).isPresent());
        assertFalse(ListUtils.get(emptyList(), 1).isPresent());
    }

    @Test
    void removeLastElementTest() {
        final List<String> oneElementList = newOneElementList();
        assertTrue(ListUtils.removeLastElement(oneElementList).isEmpty());
    }
}
