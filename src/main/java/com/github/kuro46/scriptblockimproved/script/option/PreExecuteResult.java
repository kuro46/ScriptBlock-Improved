package com.github.kuro46.scriptblockimproved.script.option;

public enum PreExecuteResult {
    CONTINUE,
    CANCEL;

    public static PreExecuteResult continueIfTrue(final boolean value) {
        return value ? CONTINUE : CANCEL;
    }
}
