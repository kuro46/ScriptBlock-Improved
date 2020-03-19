package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.handler.OptionHandler;
import com.github.kuro46.scriptblockimproved.placeholder.SourceData;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class ScriptHandler {

    private final Map<String, OptionHandler> optionHandlers = new HashMap<>();

    public Optional<OptionHandler> getHandler(@NonNull final String optionName) {
        return Optional.ofNullable(optionHandlers.get(optionName));
    }

    public void registerHandler(@NonNull final String optionName, @NonNull final OptionHandler handler) {
        optionHandlers.put(optionName, handler);
    }

    public Map<String, OptionHandler> getHandlers() {
        return Collections.unmodifiableMap(optionHandlers);
    }

    public void handle(@NonNull final Player player, @NonNull final BlockPosition position, @NonNull final TriggerData triggerData) {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final TriggerRegistry triggerRegistry = sbi.getTriggerRegistry();
        if (!triggerRegistry.isRegistered(triggerData.getTrigger())) {
            final Trigger trigger = triggerData.getTrigger();
            throw new IllegalArgumentException(String.format("Trigger '%s' (id of '%s') is not registered.", trigger.getName(), trigger.getId()));
        }
        for (Script script : sbi.getScriptList().get(position)) {
            final Trigger trigger = triggerRegistry.getTrigger(script.getTriggerName()).orElse(null);
            if (trigger == null) {
                throw new IllegalArgumentException("Unable to find trigger named '" + script.getTriggerName() + "'");
            }
            if (trigger.getId() != triggerData.getTrigger().getId()) {
                continue;
            }
            for (Script.Option option : script.getOptions()) {
                final OptionHandler optionHandler = optionHandlers.get(option.getName());
                if (optionHandler != null) {
                    final ImmutableList<String> replacedArgs = option.getArgs().stream()
                        .map(source -> sbi.getPlaceholderGroup().replace(source, SourceData.builder().player(player).position(position).build()))
                        .collect(ImmutableList.toImmutableList());
                    if (triggerData.shouldSuppress()) {
                        optionHandler.onSuppressed(triggerData, player, replacedArgs);
                    } else {
                        optionHandler.handleOption(triggerData, player, replacedArgs);
                    }
                } else {
                    sbi.getLogger()
                        .warning(String.format("Unable to find OptionHandler for '%s'", option.getName()));
                }
            }
        }
    }
}
