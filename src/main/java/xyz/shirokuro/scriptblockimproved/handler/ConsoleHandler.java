package xyz.shirokuro.scriptblockimproved.handler;

import xyz.shirokuro.scriptblockimproved.TriggerData;
import xyz.shirokuro.scriptblockimproved.common.Utils;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerData triggerData, Player player, ImmutableList<String> args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.removeSlashIfNeeded(String.join(" ", args)));
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
