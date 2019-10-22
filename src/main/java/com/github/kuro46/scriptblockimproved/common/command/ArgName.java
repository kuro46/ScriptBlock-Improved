package com.github.kuro46.scriptblockimproved.common.command;

import com.github.kuro46.scriptblockimproved.common.Name;
import lombok.NonNull;

/**
 * This class is a representation of the argument name.
 */
public final class ArgName extends Name {

    private ArgName(@NonNull final String name) {
        super(name);
    }

    public static ArgName of(@NonNull final String name) {
        return new ArgName(name);
    }
}
