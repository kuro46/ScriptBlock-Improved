package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandler;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.option.PreExecuteResult;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.PlaceholderGroup;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.SourceData;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerRegistry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class ScriptExecutor {

    private static final Lock INIT_LOCK = new ReentrantLock();

    private volatile static ScriptExecutor instance;

    private final TriggerRegistry triggerRegistry;
    private final PlaceholderGroup placeholderGroup;
    private final OptionHandlers handlers;
    private final Scripts scripts;

    private ScriptExecutor() {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.triggerRegistry = sbi.getTriggerRegistry();
        this.placeholderGroup = sbi.getPlaceholderGroup();
        this.scripts = sbi.getScripts();
        this.handlers = sbi.getOptionHandlers();
    }

    public static void init() {
        INIT_LOCK.lock();
        try {
            if (instance != null) {
                throw new IllegalStateException("ScriptExecutor already initialized");
            }
            instance = new ScriptExecutor();
        } finally {
            INIT_LOCK.unlock();
        }
    }

    public static ScriptExecutor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScriptExecutor hasn't initialized yet");
        }
        return instance;
    }

    public void execute(
            @NonNull final TriggerName triggerName,
            @NonNull final Player player,
            @NonNull final BlockPosition position) {
        if (!triggerRegistry.isRegistered(triggerName)) {
            final String message = String.format("Trigger: '%s' is not registered", triggerName);
            throw new IllegalStateException(message);
        }
        if (!scripts.contains(position)) return;
        scripts.get(position).stream()
            .filter(script -> script.getTrigger().equals(triggerName))
            .forEach(script -> execute(script, player));
    }

    public void execute(
            @NonNull final Script script,
            @NonNull final Player player) {
        final SourceData sourceData = SourceData.builder()
            .position(script.getPosition())
            .player(player)
            .build();
        final Options replaced = script.getOptions().replaced(placeholderGroup, sourceData);
        final boolean needCancel = replaced.stream()
            .anyMatch(option -> {
                final OptionHandler handler = handlers.getOrFail(option.getName());
                final PreExecuteResult result = handler.preExecute(
                        player,
                        script,
                        option);
                return result == PreExecuteResult.CANCEL;
            });
        if (needCancel) return;
        replaced.forEach(option -> {
            final OptionName name = option.getName();
            final OptionHandler handler = handlers.getOrFail(name);
            handler.execute(player, script, option);
        });
    }
}
