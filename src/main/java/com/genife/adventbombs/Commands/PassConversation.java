package com.genife.adventbombs.Commands;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Managers.PasswordManager;
import com.genife.adventbombs.Rockets.NuclearRocket;
import com.genife.adventbombs.Runnables.RocketRunnable;
import com.genife.adventbombs.SoundUtils.CreateSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PassConversation extends StringPrompt {
    private final Player rocketSender;
    private final String rocketType;
    private final int cordsX;
    private final int cordsZ;
    private final int explosionPower;
    private final AdventBombs instance = AdventBombs.getInstance();
    private final PasswordManager passwordManager;

    public PassConversation(CommandSender sender, String rocketType, int cordsX, int cordsZ, int explosionPower, PasswordManager passwordManager) {
        this.rocketSender = (Player) sender;
        this.rocketType = rocketType;
        this.cordsX = cordsX;
        this.cordsZ = cordsZ;
        this.explosionPower = explosionPower;
        this.passwordManager = passwordManager;
    }

    // функция для перехвата кодового слова
    @Override
    public String getPromptText(ConversationContext context) {
        rocketSender.playSound(rocketSender.getLocation(), "minecraft:my_sounds.nuclear_code", SoundCategory.MASTER, 1.0f, 1.0f);
        return "§3[AdventBombs] §fВведи кодовое слово.";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (passwordManager.isPasswordValid(rocketSender, input, rocketType)) {
            // запускаем ракету, ибо пароль верный
            context.getForWhom().sendRawMessage("§3[AdventBombs] §fИнициализация..");
            launchNuclearRocket();
            broadcastAlarm();
            // проигрываем личное оповещение игроку "вы инициорвали запуск ракет, в случае.."
            Bukkit.getScheduler().runTaskLater(instance,
                    () -> rocketSender.playSound(rocketSender.getLocation(), "minecraft:my_sounds.nuclear_alert", SoundCategory.MASTER, 1.0f, 1.0f), 40);
        } else {
            // Неправильный пароль
            context.getForWhom().sendRawMessage("§3[AdventBombs] §fНеверный код запуска.");
            playInvalidCode();
        }

        return null;
    }

    // функция для запуска ракеты
    private void launchNuclearRocket() {
        // запускаем ракету
        World senderWorld = rocketSender.getWorld();
        Location targetLocation = senderWorld.getHighestBlockAt(cordsX, cordsZ).getLocation();

        RocketRunnable task = new RocketRunnable(new NuclearRocket(rocketSender, rocketType, targetLocation, explosionPower, 24000));
        task.runTaskTimer(instance, 0L, 0L);
    }

    // отправляем сообщения после пуска ракеты, начинаем проигрывать звук воздушной тревоги на их локациях
    private void broadcastAlarm() {

        Bukkit.broadcastMessage("§3[Центр оповещения населения] §cЗАФИКСИРОВАН ПУСК МЕЖКОНТИНЕНТАЛЬНОЙ РАКЕТЫ НЕИЗВЕСТНОГО ТИПА!");

        BukkitRunnable sirenTask = new BukkitRunnable() {
            @Override
            public void run() {
                List<Location> allAlarms = instance.getAlarmManager().getAlarmsLocations();
                for (Location location : allAlarms) {
                    new CreateSound("minecraft:my_sounds.nuclear_alarm", 200, location);
                }
            }
        };

        if (instance.getAlarmManager().isSirenTasksEmpty()) {
            Bukkit.broadcastMessage("§3[Центр оповещения населения] §cВНИМАНИЕ! ОБЪЯВЛЕНА ВОЗДУШНАЯ ТРЕВОГА! ВСЕМ ПРОЙТИ В УКРЫТИЕ!");
            instance.getAlarmManager().addSirenTask(sirenTask);
            sirenTask.runTaskTimer(instance, 0L, 260L); // запускаем задачу с интервалом 13 секунд
        }
    }

    // если пароль не верный, то выводим публичное оповещение о несанкционированной попытке доступа
    private void playInvalidCode() {
        String playerName = rocketSender.getName();

        Bukkit.broadcastMessage("§3[Миртанский центр управления] §cОбнаружен несанкционированный вход в систему управления термоядерными зарядами игроком " + playerName + ". Система управления термоядерными зарядами переведена на ручное управление. В случае повторного фиксирования несанкционированного входа в систему будет активирован протокол \"Периметр\" - массированный запуск ядерных боеголовок по указанным целям: Верс... //КРИТИЧЕСКАЯ ОШИБКА//: Доступ запрещен.");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), "minecraft:my_sounds.nuclear_alert_fake_pass", 0.6f, 1.0f);
        }
    }
}