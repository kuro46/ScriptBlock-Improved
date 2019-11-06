package com.github.kuro46.scriptblockimproved.common.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.command.CommandSender;

@ToString
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public final class CompletionData {

    @NonNull
    @Getter
    private final RootCommand root;
    @NonNull
    @Getter
    private final Command command;
    @NonNull
    @Getter
    private final CommandSender dispatcher;
    @NonNull
    @Getter
    private final ArgName argName;
    @NonNull
    @Getter
    private final String currentValue;
}
