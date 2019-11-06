package com.github.kuro46.scriptblockimproved.common.command;

import com.github.kuro46.scriptblockimproved.common.Name;
import lombok.NonNull;

public final class CommandName extends Name {

    public CommandName(@NonNull final String name) {
        super(name);
    }

    public static CommandName of(@NonNull final String name) {
        return new CommandName(name);
    }
}
