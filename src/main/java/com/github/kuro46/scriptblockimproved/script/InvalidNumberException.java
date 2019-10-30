package com.github.kuro46.scriptblockimproved.script;

import lombok.NonNull;

@SuppressWarnings("serial")
public final class InvalidNumberException extends Exception {

    public InvalidNumberException(
            @NonNull final String argumentName,
            @NonNull final String value) {
        super("'" + value + "' for argument '" + argumentName + "' is an invalid number");
    }
}
