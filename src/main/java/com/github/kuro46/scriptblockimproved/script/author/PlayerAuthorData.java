package com.github.kuro46.scriptblockimproved.script.author;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

public final class PlayerAuthorData implements AuthorData {

    @NonNull
    private final String name;
    private final UUID uniqueId;

    public PlayerAuthorData(@NonNull final String name, final UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public PlayerAuthorData(@NonNull final String name) {
        this(name, null);
    }

    @Override
    public String getName() {
        return name;
    }

    public Optional<UUID> getUniqueId() {
        return Optional.ofNullable(uniqueId);
    }
}
