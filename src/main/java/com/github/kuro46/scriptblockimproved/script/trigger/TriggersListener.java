package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface TriggersListener {

    void onValidEvent(
            Trigger trigger,
            Event event,
            Player player,
            BlockPosition preferPosition);
}
