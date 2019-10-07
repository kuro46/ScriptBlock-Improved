package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class HelpHandler extends CommandHandler {

    public HelpHandler() {
        super(Args.builder()
                .optional("topic")
                .build());
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        final Optional<String> topicOptional = data.getArgs().get("topic");
        if (topicOptional.isPresent()) {
            helpTopic(sender, topicOptional.get());
            return;
        }
        final String[] messages = new String[] {
            ChatColor.BOLD + "[modify] modify scripts",
            "  create, createat, add, addat, delete, deleteat",
            ChatColor.BOLD + "[view] view scripts",
            "  view, viewat",
            ChatColor.BOLD + "[other] other",
            "  availables, save",
            "",
            "'/sbi help <topic>' to show details about topic",
            "Examples:",
            "/sbi help modify",
            "/sbi help view"
        };
        sendMessage(sender, messages);
    }

    private void helpTopic(final CommandSender sender, final String topic) {
        switch (topic.toLowerCase()) {
            case "modify": {
                final String[] messages = new String[] {
                    ChatColor.BOLD + "create a script to clicked/specified block",
                    "  create <trigger> <script>",
                    "  createat <world> <x> <y> <z> <trigger> <script>",
                    ChatColor.BOLD + "add a script to clicked/specified block",
                    "  add <trigger> <script>",
                    "  addat <world> <x> <y> <z> <trigger> <script>",
                    ChatColor.BOLD + "delete all scripts in clicked/specified block",
                    "  delete <trigger> <script>",
                    "  delete <world> <x> <y> <z> <trigger> <script>",
                };
                sendMessage(sender, messages);
                break;
            }
            case "view": {
                final String[] messages = new String[] {
                    ChatColor.BOLD + "view script info in clicked/specified block",
                    "  view",
                    "  view <world> <x> <y> <z>"
                };
                sendMessage(sender, messages);
                break;
            }
            case "other": {
                final String[] messages = new String[] {
                    ChatColor.BOLD + "list available options and triggers",
                    "  availables",
                    ChatColor.BOLD + "save scripts into specified file or scripts.json",
                    "  save"
                };
                sendMessage(sender, messages);
                break;
            }
            default: {
                sendMessage(sender, MessageKind.ERROR, "Unknown topic");
                break;
            }
        }
    }
}
