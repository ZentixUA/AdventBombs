package com.genife.adventbombs.Commands;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Formatters.BlockedPlayerElement;
import com.genife.adventbombs.Managers.PasswordManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RocketCompleter implements TabCompleter {
    private final PasswordManager passwordManager = AdventBombs.getInstance().getPasswordManager();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        // Если вводится первый аргумент команды
        if (args.length == 1) {
            completions.addAll(Arrays.asList("nuclear", "sculk", "unblock"));
            return completions;
        }
        // Если вводится второй аргумент команды
        else if (args.length == 2 && (args[0].equalsIgnoreCase("nuclear") || args[0].equalsIgnoreCase("sculk"))) {
            completions.add("X");
            return completions;
        }
        // Если вводится третий аргумент команды
        else if (args.length == 3 && (args[0].equalsIgnoreCase("nuclear") || args[0].equalsIgnoreCase("sculk"))) {
            completions.add("Z");
            return completions;
        }
        // Если вводится четвертый аргумент команды
        else if (args.length == 4 && (args[0].equalsIgnoreCase("nuclear") || args[0].equalsIgnoreCase("sculk"))) {
            completions.add("мощность: 1-100");
            return completions;
        }
        // Если вводится аргумент команды removefromlist
        else if (args.length == 2 && args[0].equalsIgnoreCase("unblock")) {
            List<String> nicknames = new ArrayList<>();
            for (BlockedPlayerElement blockedPlayer : passwordManager.getBlockedPlayers()) {
                nicknames.add(blockedPlayer.playerName());
            }
            return nicknames;
        }
        return null;
    }
}