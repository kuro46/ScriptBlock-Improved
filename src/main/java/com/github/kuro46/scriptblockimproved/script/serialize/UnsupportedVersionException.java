package com.github.kuro46.scriptblockimproved.script.serialize;

import lombok.Getter;
import lombok.NonNull;

@SuppressWarnings("serial")
public final class UnsupportedVersionException extends Exception {

    @NonNull
    @Getter
    private final String loadedVersion;

    public UnsupportedVersionException(final String loadedVersion) {
        super(loadedVersion);
        this.loadedVersion = loadedVersion;
    }
}
