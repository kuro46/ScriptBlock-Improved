package xyz.shirokuro.scriptblockimproved.command.migration;

import lombok.NonNull;

@SuppressWarnings("serial")
public final class MigrationException extends Exception {

    public MigrationException(@NonNull final String message) {
        super(message);
    }
}
