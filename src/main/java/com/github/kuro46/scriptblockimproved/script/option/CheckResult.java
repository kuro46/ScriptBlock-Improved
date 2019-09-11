package com.github.kuro46.scriptblockimproved.script.option;

public enum CheckResult {
    CONTINUE,
    CANCEL;

    public static CheckResult continueIfTrue(final boolean value) {
        return value ? CONTINUE : CANCEL;
    }
}
