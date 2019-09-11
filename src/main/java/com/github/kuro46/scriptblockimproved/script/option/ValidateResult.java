package com.github.kuro46.scriptblockimproved.script.option;

public enum ValidateResult {
    VALID,
    INVALID;

    public static ValidateResult validIfTrue(final boolean value) {
        return value ? VALID : INVALID;
    }
}
