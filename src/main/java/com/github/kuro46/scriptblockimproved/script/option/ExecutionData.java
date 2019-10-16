package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.Script;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
public final class ExecutionData {

    @Getter
    @NonNull
    private final Player player;

    @Getter
    @NonNull
    private final Script script;

    @Getter
    @NonNull
    private final Option option;

    /**
     * Shorthand for:<br>
     * <pre>{@code
     * getOption().getArgs()
     * }</pre>
     */
    public ParsedArgs getArgs() {
        return option.getArgs();
    }
}
