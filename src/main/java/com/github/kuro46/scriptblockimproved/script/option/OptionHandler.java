package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.script.Script;
import org.bukkit.entity.Player;

public interface OptionHandler {

    Args getArgs();

    PreExecuteResult preExecute(
            final Player player,
            final Script script,
            final Option option);

    void execute(
            final Player player,
            final Script script,
            final Option option);
}
