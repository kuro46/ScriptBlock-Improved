package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.commandutility.syntax.CommandSyntax;
import com.github.kuro46.scriptblockimproved.script.Script;
import org.bukkit.entity.Player;

public interface OptionHandler {

    CommandSyntax getSyntax();

    CheckResult check(
            final Player player,
            final Script script,
            final Option option);

    void execute(
            final Player player,
            final Script script,
            final Option option);
}
