package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ActionExecutor implements Listener {

    private final Actions actions;

    public ActionExecutor(final Actions actions) {
        this.actions = Objects.requireNonNull(actions, "'actions' cannot be null");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        final Action action = actions.poll(player).orElse(null);
        if (action == null) return;
        event.setCancelled(true);

        action.action(player, BlockPosition.fromLocation(event.getClickedBlock().getLocation()));
    }
}
