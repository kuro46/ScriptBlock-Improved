package xyz.shirokuro.scriptblockimproved.placeholder;

import xyz.shirokuro.scriptblockimproved.BlockPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
