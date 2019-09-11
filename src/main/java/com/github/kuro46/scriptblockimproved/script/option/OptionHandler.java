package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.commandutility.syntax.CommandSyntax;
import com.github.kuro46.scriptblockimproved.script.Script;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface OptionHandler {

    CommandSyntax getSyntax();

    ValidateResult validate(final Arguments args);

    Arguments normalize(final Arguments rawArgs);

    CheckResult check(
            final Event event,
            final Player player,
            final Script script,
            final OptionName name,
            final Arguments args);

    void execute(
            final Event event,
            final Player player,
            final Script script,
            final OptionName name,
            final Arguments args);
}
