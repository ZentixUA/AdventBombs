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

public class NuclearCommand implements CommandExecutor {
    private static final String NO_OP = "§3[AdventBombs] §cУ тебя нет прав на использование этой команды!";
    private static final String ONLY_IN_GAME = "§3[AdventBombs] §cЭту команду следует использовать в игре.";
    private static final String REMOVE_FROM_LIST_INCORRECT_TYPING = "§3[AdventBombs] §fНеверный ввод. /rocket unblock [никнейм]";
    private static final String INCORRECT_TYPING = "§3[AdventBombs] §fНеверный ввод. /rocket [nuclear/sculk] [X] [Z] [мощность 0-100]";
    private static final String INCORRECT_POWER_INT = "§3[AdventBombs] §fНеверное значение мощности, допустимы числа от 0 до 100.";
    private static final String PLAYER_BLOCKED = "§3[AdventBombs] §cДоступ запрещен.";
    private static final String ALREADY_IN_CONVERSATION = "§3[AdventBombs] §cТы уже в запросе ввода пароля! Заверши его, прежде чем начинать новый.";
    private static final String NO_PASS_TYPED = "§3[AdventBombs] §cТы не ввёл пароль в течении 120 секунд!";
    private final HashMap<Player, Boolean> playersConversations = new HashMap<>();
    private final PasswordManager passwordManager = AdventBombs.getInstance().getPasswordManager();
    private final CooldownManager cooldownManager = new CooldownManager();

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("unblock")) {

                // проверяем, есть ли у игрока права оператора (OP)
                if (!sender.isOp()) {
                    sender.sendMessage(NO_OP);
                    return false;
                }

                if (args.length > 1) {
                    String playerName = args[1];

                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(playerName);

                    if (player == null) {
                        sender.sendMessage("§3[AdventBombs] §fИгрок " + playerName + " не играл на сервере.");
                        return true;
                    }

                    if (passwordManager.unblockPlayer(player.getUniqueId())) {
                        sender.sendMessage("§3[AdventBombs] §fИгрок " + playerName + " был удален из списка блокировок.");
                    } else {
                        sender.sendMessage("§3[AdventBombs] §fИгрок " + playerName + " не найден в списке блокировок.");
                    }
                    return true;
                }

                // если что-то введено не верно - информируем пользователя
                sender.sendMessage(REMOVE_FROM_LIST_INCORRECT_TYPING);
                return false;
            }

            if (args[0].equalsIgnoreCase("nuclear") || args[0].equalsIgnoreCase("sculk")) {
                // проверка, кто отправил команду (игрок/другие источники)
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ONLY_IN_GAME);
                    return false;
                }

                // если отправлены не все аргументы, то информируем игрока об этом
                if (args.length < 4) {
                    sender.sendMessage(INCORRECT_TYPING);
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
                        sender.sendMessage(INCORRECT_POWER_INT);
                        return false;
                    }

                    // если пользователь заблокирован из-за ввода неправильного пароля ранее, то не пропускаем.
                    if (passwordManager.isPLayerBlocked(senderUUID)) {
                        sender.sendMessage(PLAYER_BLOCKED);
                        return false;
                    }

                    // проверяем, нет ли у игрока уже запущенного Conversation
                    if (playersConversations.containsKey((Player) sender)) {
                        sender.sendMessage(ALREADY_IN_CONVERSATION);
                        return false;
                    }

                    Duration timeLeft = cooldownManager.getRemainingCooldown(senderUUID);

                    if (!timeLeft.isZero()) {
                        double secondsLeft = (double) timeLeft.getSeconds() + (double) timeLeft.getNano() / 1_000_000_000;
                        DecimalFormat df = new DecimalFormat("0.0");
                        String durationString = df.format(secondsLeft) + " сек.";
                        sender.sendMessage("§3[AdventBombs] §cПодожди " + durationString + ", прежде чем снова запустить ракету!");
                        return false;
                    }

                    // создаём "перехватчик" ввода (в нашем случае, проверяем интерактивно пароль)
                    ConversationFactory cf = new ConversationFactory(AdventBombs.getInstance());
                    Conversation conv = cf
                            .withFirstPrompt(new ConvPrompt(sender, rocketType, cordsX, cordsZ, rocketPower, passwordManager))
                            .withLocalEcho(false)
                            .withTimeout(120)
                            .buildConversation((Player) sender);

                    // помечаем, что у игрока сейчас запущен Conversation
                    playersConversations.put((Player) sender, true);

                    // Действия после завершения чата
                    conv.addConversationAbandonedListener(event -> {
                        if (!event.gracefulExit()) {
                            // Отправляем сообщение, если Conversation был прерван по таймауту
                            ((Player) event.getContext().getForWhom()).sendMessage(NO_PASS_TYPED);
                        }
                        playersConversations.remove((Player) sender);
                    });

                    conv.begin();

                    cooldownManager.setCooldown(senderUUID, Duration.ofSeconds(CooldownManager.DEFAULT_COOLDOWN));

                    return true;

                } catch (NumberFormatException e) {
                    // если игрок ввёл не число, то выводим сообщение о неверном вводе.
                    sender.sendMessage(INCORRECT_TYPING);
                    return false;
                }
            }
        }

        // если вообще нет аргументов, то тоже информируем человека
        sender.sendMessage(INCORRECT_TYPING);
        return false;

    }
}