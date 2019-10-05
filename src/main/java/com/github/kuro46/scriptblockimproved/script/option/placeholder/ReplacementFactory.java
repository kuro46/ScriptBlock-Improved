package com.github.kuro46.scriptblockimproved.script.option.placeholder;

@FunctionalInterface
public interface ReplacementFactory {

    /**
     * Creates replacement
     *
     * @return replacement
     */
    String create(SourceData data);
}
