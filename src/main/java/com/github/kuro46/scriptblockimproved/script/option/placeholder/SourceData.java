package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
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

    @NonNull
    @Getter
    private final BlockPosition position;
    @NonNull
    @Getter
    private final Player player;
}
