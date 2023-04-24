package com.genife.adventbombs.Commands;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Managers.CooldownManager;
import com.genife.adventbombs.Managers.PasswordManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public class RocketCommand implements CommandExecutor {
    private final HashMap<Player, Boolean> playersConversations = new HashMap<>();
    private final AdventBombs instance = AdventBombs.getInstance();
    private final PasswordManager passwordManager = instance.getPasswordManager();
    private final CooldownManager cooldownManager = new CooldownManager();

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("unblock")) {
                // проверяем, есть ли у игрока права оператора (OP)
                if (!sender.isOp()) {
                    sender.sendMessage(MESSAGE_PREFIX + NO_PERMISSION_MESSAGE);
                    return false;
                }

                if (args.length > 1) {
                    String playerName = args[1];

                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(playerName);

                    if (player == null) {
                        sender.sendMessage(MESSAGE_PREFIX + UNBLOCK_NOT_FOUND_MESSAGE.replace("{player}", playerName));
                        return true;
                    }

                    if (passwordManager.unblockPlayer(player.getUniqueId())) {
                        sender.sendMessage(MESSAGE_PREFIX + UNBLOCK_SUCCESS_MESSAGE.replace("{player}", playerName));
                    } else {
                        sender.sendMessage(MESSAGE_PREFIX + UNBLOCK_NOT_FOUND_MESSAGE.replace("{player}", playerName));
                    }
                    return true;
                }

                // если что-то введено не верно - информируем пользователя
                sender.sendMessage(MESSAGE_PREFIX + INCORRECT_TYPING_MESSAGE);
                return false;
            }

            if (args[0].equalsIgnoreCase("nuclear") || args[0].equalsIgnoreCase("sculk")) {
                // проверка, кто отправил команду (игрок/другие источники)
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MESSAGE_PREFIX + ONLY_IN_GAME_MESSAGE);
                    return false;
                }

                // если отправлены не все аргументы, то информируем игрока об этом
                if (args.length < 4) {
                    sender.sendMessage(MESSAGE_PREFIX + INCORRECT_TYPING_MESSAGE);
                    return false;
                }

                try {
                    UUID senderUUID = ((Player) sender).getUniqueId();
                    // устанавливаем параметры нашей ракеты
                    String rocketType = args[0];
                    int cordsX = Integer.parseInt(args[1]);
                    int cordsZ = Integer.parseInt(args[2]);
                    int rocketPower = Integer.parseInt(args[3]);

                    // ограничиваем мощность ракеты
                    if (rocketPower < 0 || rocketPower > 100) {
                        sender.sendMessage(MESSAGE_PREFIX + INCORRECT_TYPING_MESSAGE);
                        return false;
                    }

                    // если пользователь заблокирован из-за ввода неправильного пароля ранее, то не пропускаем.
                    if (passwordManager.isPLayerBlocked(senderUUID)) {
                        sender.sendMessage(MESSAGE_PREFIX + PLAYER_BLOCKED_MESSAGE);
                        return false;
                    }

                    // проверяем, нет ли у игрока уже запущенного Conversation
                    if (playersConversations.containsKey((Player) sender)) {
                        sender.sendMessage(MESSAGE_PREFIX + ALREADY_TYPING_PASS_MESSAGE);
                        return false;
                    }

                    Duration timeLeft = cooldownManager.getRemainingCooldown(senderUUID);

                    if (!timeLeft.isZero()) {
                        double secondsLeft = (double) timeLeft.getSeconds() + (double) timeLeft.getNano() / 1_000_000_000;
                        DecimalFormat df = new DecimalFormat("0.0");
                        String durationString = df.format(secondsLeft);
                        sender.sendMessage(MESSAGE_PREFIX + COOLDOWN_MESSAGE.replace("{duration}", durationString));
                        return false;
                    }

                    // создаём "перехватчик" ввода (в нашем случае, проверяем интерактивно пароль)
                    ConversationFactory cf = new ConversationFactory(AdventBombs.getInstance());
                    Conversation conv = cf
                            .withFirstPrompt(new PassConversation(sender, rocketType, cordsX, cordsZ, rocketPower, passwordManager))
                            .withLocalEcho(false)
                            .withTimeout(120)
                            .buildConversation((Player) sender);

                    // помечаем, что у игрока сейчас запущен PassConversation
                    playersConversations.put((Player) sender, true);

                    // Действия после завершения чата
                    conv.addConversationAbandonedListener(event -> {
                        if (!event.gracefulExit()) {
                            // Отправляем сообщение, если Conversation был прерван по таймауту
                            ((Player) event.getContext().getForWhom()).sendMessage(MESSAGE_PREFIX + NO_PASS_TYPED_MESSAGE);
                        }
                        playersConversations.remove((Player) sender);
                    });

                    conv.begin();

                    cooldownManager.setCooldown(senderUUID);

                    return true;

                } catch (NumberFormatException e) {
                    // если игрок ввёл не число, то выводим сообщение о неверном вводе.
                    sender.sendMessage(MESSAGE_PREFIX + INCORRECT_TYPING_MESSAGE);
                    return false;
                }
            }

            if (args[0].equalsIgnoreCase("reload")) {
                // проверяем, есть ли у игрока права оператора (OP)
                if (!sender.isOp()) {
                    sender.sendMessage(MESSAGE_PREFIX + NO_PERMISSION_MESSAGE);
                    return false;
                }

                instance.getConfigManager().reloadConfig();

                sender.sendMessage(MESSAGE_PREFIX + RELOAD_MESSAGE);
                return true;

            }
        }

        // если вообще нет аргументов, то тоже информируем человека
        sender.sendMessage(MESSAGE_PREFIX + INCORRECT_TYPING_MESSAGE);
        return false;

    }
}