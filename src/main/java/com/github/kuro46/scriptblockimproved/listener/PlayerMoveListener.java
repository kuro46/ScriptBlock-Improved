package com.github.kuro46.scriptblockimproved.listener;

import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.ScriptHandler;
import com.github.kuro46.scriptblockimproved.TriggerInfo;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class PlayerMoveListener implements Listener {

    private final Map<Player, BlockPosition> moveLastTriggeredMap = new WeakHashMap<>();
    private final Map<Player, BlockPosition> sbWalkLastTriggeredMap = new WeakHashMap<>();

    @EventHandler
    public void onMove(@NonNull final PlayerMoveEvent event) {
        moveTrigger(event);
        sbWalkTrigger(event);
    }

    private void moveTrigger(@NonNull final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location location = player.getLocation().add(0D, -1D, 0D);
        final BlockPosition position = BlockPosition.ofLocation(location);
        final BlockPosition lastTriggered = moveLastTriggeredMap.get(player);
        final boolean shouldSuppress = lastTriggered != null && lastTriggered.equals(position);
        final ScriptHandler scriptHandler = ScriptBlockImproved.getInstance().getScriptHandler();
        scriptHandler.handle(player, position, TriggerInfo.builder().name("move").shouldSuppress(shouldSuppress).event(event).build());
        if (!shouldSuppress) {
            moveLastTriggeredMap.put(player, position);
        }
    }

    private void sbWalkTrigger(@NonNull final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location location = player.getLocation().add(0D, -1D, 0D);
        if (location.getBlock().getType() == Material.AIR) {
            return;
        }
        final BlockPosition position = BlockPosition.ofLocation(location);
        final BlockPosition lastTriggered = sbWalkLastTriggeredMap.get(player);
        final boolean shouldSuppress = lastTriggered != null && lastTriggered.getX() == position.getX() && lastTriggered.getZ() == position.getZ();
        final ScriptHandler scriptHandler = ScriptBlockImproved.getInstance().getScriptHandler();
        scriptHandler.handle(player, position, TriggerInfo.builder().name("sbwalk").shouldSuppress(shouldSuppress).event(event).build());
        if (!shouldSuppress) {
            sbWalkLastTriggeredMap.put(player, position);
        }
    }
}
