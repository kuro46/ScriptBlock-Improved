package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Builder(builderClassName = "Builder")
public final class AdditionalEventData {

    @Getter
    @NonNull
    private final BlockPosition position;
    @Getter
    @NonNull
    private final Player player;

    public AdditionalEventData(
        @NonNull final BlockPosition position,
        @NonNull final Player player
    ) {
        this.position = position;
        this.player = player;
    }
}
