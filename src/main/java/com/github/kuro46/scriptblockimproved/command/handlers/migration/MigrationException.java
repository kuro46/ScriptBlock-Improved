package com.github.kuro46.scriptblockimproved.command.handlers.migration;

import lombok.NonNull;

@SuppressWarnings("serial")
final class MigrationException extends Exception {

    public MigrationException(@NonNull final String message) {
        super(message);
    }
}
