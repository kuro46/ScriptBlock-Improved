package com.github.kuro46.scriptblockimproved;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.entity.Player;

@ToString
@EqualsAndHashCode
public final class Author {

    @Getter
    private final Type type;
    @Getter
    private final String name;
    @Getter
    private final UUID uniqueId;

    private Author(@NonNull final Type type, @NonNull final String name, final UUID uniqueId) {
        this.type = type;
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public static Author system(@NonNull final String name) {
        return new Author(Type.SYSTEM, name, null);
    }

    public static Author player(@NonNull final String name, @NonNull final UUID uniqueId) {
        return new Author(Type.PLAYER, name, uniqueId);
    }

    public static Author player(@NonNull final Player player) {
        return player(player.getName(), player.getUniqueId());
    }

    public enum Type {
        SYSTEM,
        PLAYER
    }
}
