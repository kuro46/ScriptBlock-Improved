package com.github.kuro46.scriptblockimproved.placeholder;

import com.github.kuro46.scriptblockimproved.BlockPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.entity.Player;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder(builderClassName = "Builder")
public final class SourceData {

    @Getter
    private final BlockPosition position;
    @Getter
    private final Player player;
}
