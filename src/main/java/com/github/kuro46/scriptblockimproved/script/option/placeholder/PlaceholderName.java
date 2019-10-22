package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.common.Name;
import lombok.NonNull;

public final class PlaceholderName extends Name {

    public PlaceholderName(@NonNull final String name) {
        super(name);
    }

    public static PlaceholderName of(final String name) {
        return new PlaceholderName(name);
    }
}
