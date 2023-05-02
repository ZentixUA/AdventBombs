package com.genife.adventbombs.Managers;

import com.genife.adventbombs.AdventBombs;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    public static int DISTANCE_TO_MOVE_ROCKET_WITH_Y;
    public static int FLYING_ROCKET_HEIGHT;
    public static double FLYING_ROCKET_SPEED;
    public static double MOVE_UP_SPEED;
    public static double MOVE_WITH_Y_SPEED;
    public static int MAX_FLY_DURATION;
    public static String NUCLEAR_START_PASS;
    public static String SCULK_START_PASS;
    public static int ROCKET_USAGE_COOLDOWN;

    public static int RADIATION_EFFECT_DELAY;
    public static int RADIATION_DURATION;
    public static int ACID_RAIN_DELAY;
    public static int ACID_RAIN_DURATION;

    public static int BLOCKING_DURATION;

    public static String ALARM_PLACE_WORD;
    public static long ALARM_SOUND_PLAY_INTERVAL;


    public static String MESSAGE_PREFIX;
    public static String RELOAD_MESSAGE;
    public static String NO_PERMISSION_MESSAGE;
    public static String INCORRECT_TYPING_MESSAGE;
    public static String ONLY_IN_GAME_MESSAGE;
    public static String COOLDOWN_MESSAGE;
    public static String ALREADY_TYPING_PASS_MESSAGE;
    public static String PLAYER_BLOCKED_MESSAGE;
    public static String NO_PASS_TYPED_MESSAGE;
    public static String PASS_ENTER_MESSAGE;
    public static String PASS_ENTERED_MESSAGE;
    public static String NON_SANCTIONED_ACCESS_MESSAGE;
    public static String INCORRECT_PASS_MESSAGE;

    public static String UNBLOCK_SUCCESS_MESSAGE;
    public static String UNBLOCK_NOT_FOUND_MESSAGE;

    public static String ALARM_PLACE_MESSAGE;
    public static String ALARM_BREAK_MESSAGE;
    public static String ALREADY_PLACED_ALARM_MESSAGE;
    public static String ROCKET_START_DETECTED_MESSAGE;
    public static String ALARM_START_BROADCAST_MESSAGE;
    public static String ALARM_STOP_BROADCAST_MESSAGE;
    public static String NUCLEAR_ROCKET_DETONATED_MESSAGE;
    public static String SCULK_ROCKET_DETONATED_MESSAGE;

    public static String ACID_RAIN_START_MESSAGE;
    public static String ACID_RAIN_STOP_MESSAGE;

    public static String RADIATION_START_MESSAGE;
    public static String RADIATION_STOP_MESSAGE;

    public static String DEBUG_AUTO_UNBLOCK_MESSAGE;
    public static String DEBUG_ACID_RAIN_FOUND_MESSAGE;
    public static String DEBUG_BROKEN_SIREN_MESSAGE;

    public static String ROCKET_CODE_TYPING_SOUND;
    public static String ROCKET_INITIALIZED_SOUND;
    public static String ROCKET_START_FLYING_SOUND;
    public static String ROCKET_FLYING_SOUND;
    public static String ROCKET_DETONATE_SOUND;
    public static String INVALID_PASS_BROADCAST_SOUND;
    public static String ALARM_SOUND;

    private final AdventBombs instance = AdventBombs.getInstance();
    private FileConfiguration config;

    public ConfigManager() {
        loadConfig();
        loadValues();
    }

    public void loadConfig() {
        // Берём значения из конфига или создаём его
        config = instance.getConfig();

        config.addDefault("rockets.distance_to_move_with_y", 340);
        config.addDefault("rockets.flying_height", 250);
        config.addDefault("rockets.flying_speed", 5.8);
        config.addDefault("rockets.move_up_speed", 1.0);
        config.addDefault("rockets.move_with_y_speed", 1.0);
        config.addDefault("rockets.max_fly_duration", 1200);
        config.addDefault("rockets.nuclear.password", "nuclear_start_pass");
        config.addDefault("rockets.sculk.password", "sculk_start_pass");
        config.addDefault("rockets.cooldown", 5);

        config.addDefault("effects.radiation.effects_delay", 60);
        config.addDefault("effects.radiation.duration", 480);

        config.addDefault("effects.acid_rain.delay", 120);
        config.addDefault("effects.acid_rain.duration", 480);

        config.addDefault("blocking.duration", 14400);

        config.addDefault("alarm.place_word", "[сирена]");
        config.addDefault("alarm.sound_play_interval", 13L);

        config.addDefault("messages.prefix", "§3[AdventBombs] ");

        config.addDefault("messages.commands.reload", "§aКонфиг успешно перезагружен!");
        config.addDefault("messages.commands.no_permissions", "§cУ тебя нет прав на использование этой команды!");
        config.addDefault("messages.commands.incorrect_typing", "§fНеверный ввод. /rocket [nuclear/sculk/unblock/reload] [X] [Z] [мощность 0-100]");
        config.addDefault("messages.commands.only_in_game", "§cЭту команду следует использовать в игре.");
        config.addDefault("messages.commands.cooldown", "§cПодожди {duration} сек, прежде чем снова запустить ракету!");
        config.addDefault("messages.commands.already_typing_pass", "§cТы уже в запросе ввода пароля! Заверши его, прежде чем начинать новый.");
        config.addDefault("messages.commands.player_blocked", "§cДоступ запрещен.");
        config.addDefault("messages.typing.no_pass_typed", "§cТы не ввёл пароль в течении 120 секунд!");
        config.addDefault("messages.typing.pass_enter", "§fВведи кодовое слово.");
        config.addDefault("messages.typing.pass_entered", "§fКод запуска ракеты принят. Инициализация...");
        config.addDefault("messages.typing.non_sanctioned_access", "§cОбнаружена попытка входа в систему управления термоядерными зарядами игроком §a{player}!");
        config.addDefault("messages.typing.incorrect_pass", "§fНеверный код запуска.");

        config.addDefault("messages.commands.unblock.success", "§fИгрок {player} был удален из списка блокировок.");
        config.addDefault("messages.commands.unblock.not_found", "§fИгрок {player} не найден в списке блокировок.");

        config.addDefault("messages.alarm.place", "§fТы установил сирену. Теперь, во время воздушной тревоги, этот блок будет проигрывать звуковое оповещение!");
        config.addDefault("messages.alarm.break", "§fТы сломал блок сирены!");
        config.addDefault("messages.alarm.already_placed", "§cНа этом месте уже установлена сирена!");
        config.addDefault("messages.alarm.rocket_start_detected", "§cЗАФИКСИРОВАН ПУСК МЕЖКОНТИНЕНТАЛЬНОЙ РАКЕТЫ НЕИЗВЕСТНОГО ТИПА!");
        config.addDefault("messages.alarm.start_broadcast", "§cВНИМАНИЕ! ОБЪЯВЛЕНА ВОЗДУШНАЯ ТРЕВОГА! ВСЕМ ПРОЙТИ В УКРЫТИЕ!");
        config.addDefault("messages.alarm.stop_broadcast", "§aВнимание! Отбой воздушной тревоги! Все ракеты достигли целей!");
        config.addDefault("messages.alarm.nuclear.detonated", "§fМежконтинентальная ядерная ракета произвела детонацию!");
        config.addDefault("messages.alarm.sculk.detonated", "§fСкалковая ракета произвела детонацию!");

        config.addDefault("messages.effects.acid_rain.start", "§fСообщается о начале §aкислотного дождя §fв мире {world}. Все в укрытия!");
        config.addDefault("messages.effects.acid_rain.stop", "§aКислотный дождь §fзакончился в мире {world}. Можно выходить из укрытий, будьте осторожны!");

        config.addDefault("messages.effects.radiation.start", "§fСообщается о появлении §aрадиации §fв районе §a{cords} в мире {world}. §fВсе необходимо принимать меры предосторожности!");
        config.addDefault("messages.effects.radiation.stop", "§fРадиация ослабевает в мире {world} по координатам §a{cords}");

        config.addDefault("messages.debug.auto_unblock", "§fИгрок {player} был удалён из списка блокировок!");
        config.addDefault("messages.debug.acid_rain_found", "§fНайден уже существующий кислотный дождь в мире {world}, отменяю его и вызываю новый (в итоге, он продлевается)..");
        config.addDefault("messages.debug.siren_broken", "§fБлок сирены по локации {location} не найден! Удаляю!");

        config.addDefault("sounds.rocket.code_typing", "minecraft:my_sounds.rocket_code_typing");
        config.addDefault("sounds.rocket.initialized", "minecraft:my_sounds.rocket_initialized");
        config.addDefault("sounds.rocket.start_flying", "minecraft:my_sounds.rocket_start_flying");
        config.addDefault("sounds.rocket.flying", "minecraft:my_sounds.rocket_flying");
        config.addDefault("sounds.rocket.detonate", "minecraft:my_sounds.rocket_detonate");
        config.addDefault("sounds.rocket.invalid_pass_broadcast_sound", "minecraft:my_sounds.invalid_pass_sound");
        config.addDefault("sounds.alarm.sound", "minecraft:my_sounds.alarm_sound");

        config.options().copyDefaults(true);
        config.options().setHeader(List.of("На данный момент, конфиг в стадии тестирования. Репорти ошибки, если найдёшь",
                "Все параметры, которые так или иначе связаны со временем указывай в СЕКУНДАХ!",
                "Названия звуков бери из ресурс пака или игры",
                "MOVE WITH Y - это когда ракета снижается. Но бывает так, что цель находится выше самой ракеты, потому говорить, что она снижается - не корректно",
                "ВНИМАНИЕ! Устанавливать высокие значения в параметрах ракеты НЕ рекомендуется! Это может привести к проблемам. Ясно, окей? Спасибо."));
        instance.saveConfig();
    }

    private void loadValues() {
        DISTANCE_TO_MOVE_ROCKET_WITH_Y = config.getInt("rockets.distance_to_move_with_y");
        FLYING_ROCKET_HEIGHT = config.getInt("rockets.flying_height");
        FLYING_ROCKET_SPEED = config.getDouble("rockets.flying_speed");
        MOVE_UP_SPEED = config.getDouble("rockets.move_up_speed");
        MOVE_WITH_Y_SPEED = config.getDouble("rockets.move_with_y_speed");
        MAX_FLY_DURATION = config.getInt("rockets.max_fly_duration");
        NUCLEAR_START_PASS = config.getString("rockets.nuclear.password");
        SCULK_START_PASS = config.getString("rockets.sculk.password");
        ROCKET_USAGE_COOLDOWN = config.getInt("rockets.cooldown");

        RADIATION_EFFECT_DELAY = config.getInt("effects.radiation.effects_delay");
        RADIATION_DURATION = config.getInt("effects.radiation.duration");

        ACID_RAIN_DELAY = config.getInt("effects.acid_rain.delay");
        ACID_RAIN_DURATION = config.getInt("effects.acid_rain.duration");

        BLOCKING_DURATION = config.getInt("blocking.duration");

        ALARM_PLACE_WORD = config.getString("alarm.place_word");
        ALARM_SOUND_PLAY_INTERVAL = config.getLong("alarm.sound_play_interval");

        MESSAGE_PREFIX = config.getString("messages.prefix");
        RELOAD_MESSAGE = config.getString("messages.commands.reload");
        NO_PERMISSION_MESSAGE = config.getString("messages.commands.no_permissions");
        INCORRECT_TYPING_MESSAGE = config.getString("messages.commands.incorrect_typing");
        ONLY_IN_GAME_MESSAGE = config.getString("messages.commands.only_in_game");
        COOLDOWN_MESSAGE = config.getString("messages.commands.cooldown");
        ALREADY_TYPING_PASS_MESSAGE = config.getString("messages.commands.already_typing_pass");
        PLAYER_BLOCKED_MESSAGE = config.getString("messages.commands.player_blocked");
        NO_PASS_TYPED_MESSAGE = config.getString("messages.typing.no_pass_typed");
        PASS_ENTER_MESSAGE = config.getString("messages.typing.pass_enter");
        PASS_ENTERED_MESSAGE = config.getString("messages.typing.pass_entered");
        NON_SANCTIONED_ACCESS_MESSAGE = config.getString("messages.typing.non_sanctioned_access");
        INCORRECT_PASS_MESSAGE = config.getString("messages.typing.incorrect_pass");

        UNBLOCK_SUCCESS_MESSAGE = config.getString("messages.commands.unblock.success");
        UNBLOCK_NOT_FOUND_MESSAGE = config.getString("messages.commands.unblock.not_found");

        ALARM_PLACE_MESSAGE = config.getString("messages.alarm.place");
        ALARM_BREAK_MESSAGE = config.getString("messages.alarm.break");
        ALREADY_PLACED_ALARM_MESSAGE = config.getString("messages.alarm.already_placed");
        ROCKET_START_DETECTED_MESSAGE = config.getString("messages.alarm.rocket_start_detected");
        ALARM_START_BROADCAST_MESSAGE = config.getString("messages.alarm.start_broadcast");
        ALARM_STOP_BROADCAST_MESSAGE = config.getString("messages.alarm.stop_broadcast");
        NUCLEAR_ROCKET_DETONATED_MESSAGE = config.getString("messages.alarm.nuclear.detonated");
        SCULK_ROCKET_DETONATED_MESSAGE = config.getString("messages.alarm.sculk.detonated");

        ACID_RAIN_START_MESSAGE = config.getString("messages.effects.acid_rain.start");
        ACID_RAIN_STOP_MESSAGE = config.getString("messages.effects.acid_rain.stop");

        RADIATION_START_MESSAGE = config.getString("messages.effects.radiation.start");
        RADIATION_STOP_MESSAGE = config.getString("messages.effects.radiation.stop");

        DEBUG_AUTO_UNBLOCK_MESSAGE = config.getString("messages.debug.auto_unblock");
        DEBUG_ACID_RAIN_FOUND_MESSAGE = config.getString("messages.debug.acid_rain_found");
        DEBUG_BROKEN_SIREN_MESSAGE = config.getString("messages.debug.siren_broken");

        ROCKET_CODE_TYPING_SOUND = config.getString("sounds.rocket.code_typing");
        ROCKET_INITIALIZED_SOUND = config.getString("sounds.rocket.initialized");
        ROCKET_START_FLYING_SOUND = config.getString("sounds.rocket.start_flying");
        ROCKET_FLYING_SOUND = config.getString("sounds.rocket.flying");
        ROCKET_DETONATE_SOUND = config.getString("sounds.rocket.detonate");
        INVALID_PASS_BROADCAST_SOUND = config.getString("sounds.rocket.invalid_pass_broadcast_sound");
        ALARM_SOUND = config.getString("sounds.alarm.sound");
    }

    public void reloadConfig() {
        instance.reloadConfig();
        loadConfig();
        loadValues();
        // Ребилдим список кулдауна (вдруг пользователь изменил его продолжительность)
        instance.getCooldownManager().configureCache();
    }
}
