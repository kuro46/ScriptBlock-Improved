package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.handler.OptionHandler;
import com.github.kuro46.scriptblockimproved.placeholder.SourceData;
import com.google.common.collect.ImmutableList;
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

    public void handle(@NonNull final Player player, @NonNull final BlockPosition position, @NonNull final TriggerInfo triggerInfo) {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        for (Script script : sbi.getScriptList().get(position)) {
            if (!script.getTriggerName().equals(triggerInfo.getName())) {
                continue;
            }
            for (Script.Option option : script.getOptions()) {
                final OptionHandler optionHandler = optionHandlers.get(option.getName());
                if (optionHandler != null) {
                    final ImmutableList<String> replacedArgs = option.getArgs().stream()
                        .map(source -> sbi.getPlaceholderGroup().replace(source, SourceData.builder().player(player).position(position).build()))
                        .collect(ImmutableList.toImmutableList());
                    if (triggerInfo.shouldSuppress()) {
                        optionHandler.onSuppressed(triggerInfo, player, replacedArgs);
                    } else {
                        optionHandler.handleOption(triggerInfo, player, replacedArgs);
                    }
                } else {
                    sbi.getLogger()
                        .warning(String.format("Unable to find OptionHandler for '%s'", option.getName()));
                }
            }
        }
    }
}
