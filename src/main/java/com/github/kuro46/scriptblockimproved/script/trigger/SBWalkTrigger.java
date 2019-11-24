package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public final class SBWalkTrigger extends Trigger<PlayerMoveEvent> {

    private final Map<Player, BlockPosition> lastWalkedPositions = new WeakHashMap<>();

    private SBWalkTrigger() {
        super(
            ScriptBlockImproved.getInstance().getPlugin(),
            PlayerMoveEvent.class,
            "sbwalk"
        );
    }

    public static void register() {
        new SBWalkTrigger();
    }

    @Override
    public ValidationResult validateCondition(@NonNull final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Block walkingBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (walkingBlock.getType() == Material.AIR) return ValidationResult.invalid();
        return ValidationResult.valid(AdditionalEventData.builder()
            .position(BlockPosition.fromBlock(walkingBlock))
            .player(player)
            .build());
    }

    @Override
    public boolean shouldSuppress(
        @NonNull final PlayerMoveEvent event,
        @NonNull final AdditionalEventData additionalData
    ) {
        final BlockPosition position = additionalData.getPosition();
        final Player player = additionalData.getPlayer();
        final BlockPosition lastWalkedPos = lastWalkedPositions.get(player);
        if (lastWalkedPos != null && lastWalkedPos.equals(position)) return true;
        lastWalkedPositions.put(player, position);
        return false;
    }
}
