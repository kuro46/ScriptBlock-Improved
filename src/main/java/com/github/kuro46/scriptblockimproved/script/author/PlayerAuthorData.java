package com.github.kuro46.scriptblockimproved.script.author;

import java.util.Objects;
import java.util.UUID;

public final class PlayerAuthorData implements AuthorData {

    private final String name;
    private final UUID uniqueId;

    public PlayerAuthorData(final String name, final UUID uniqueId) {
        this.name = Objects.requireNonNull(name, "'name' cannot be null");
        this.uniqueId = Objects.requireNonNull(uniqueId, "'uniqueId' cannot be null");
    }

    @Override
    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
