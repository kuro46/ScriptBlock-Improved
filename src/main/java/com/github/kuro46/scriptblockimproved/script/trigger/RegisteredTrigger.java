package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.ScriptExecutor;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class RegisteredTrigger {

    private final List<UnregisteredListener> listenerList = new ArrayList<>();

    @Getter
    private boolean unregistered = false;

    @Getter
    @NonNull
    private final TriggerName name;

    public RegisteredTrigger(@NonNull final TriggerName name) {
        this.name = name;
    }

    public void executeIfAvailable(
            @NonNull final Player player,
            @NonNull final BlockPosition position) {
        if (unregistered) return;
        ScriptExecutor.getInstance().execute(name, player, position);
    }

    public void execute(
            @NonNull final Player player,
            @NonNull final BlockPosition position) {
        ScriptExecutor.getInstance().execute(name, player, position);
    }

    public final void unregister() {
        unregistered = true;
        fireUnregistered();
    }

    public final void onUnregistered(@NonNull final UnregisteredListener listener) {
        listenerList.add(listener);
    }

    private final void fireUnregistered() {
        listenerList.forEach(UnregisteredListener::onUnregistered);
    }
}
