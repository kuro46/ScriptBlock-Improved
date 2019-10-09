package com.github.kuro46.scriptblockimproved.script.option;

import lombok.NonNull;

@SuppressWarnings("serial")
public final class ParseException extends Exception {

    public ParseException(@NonNull final String message) {
        super(message);
    }
}
