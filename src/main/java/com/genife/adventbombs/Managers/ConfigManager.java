package com.genife.adventbombs.Managers;

import com.genife.adventbombs.AdventBombs;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    public static int DISTANCE_TO_MOVE_ROCKET_WITH_Y;
    public static int FLYING_ROCKET_HEIGHT;
    public static String NUCLEAR_START_PASS;
    public static String SCULK_START_PASS;

    public static Integer ACID_RAIN_DELAY;
    public static Integer ACID_RAIN_DURATION;
    public static Integer RADIATION_EFFECT_DELAY;
    public static Integer RADIATION_DURATION;

    public static String NO_PERMISSIONS;
    public static String ONLY_IN_GAME_COMMAND;
    public static String COOLDOWN_COMMAND;
    public static String INCORRECT_TYPING_MESSAGE;
    public static String PLAYER_BLOCKED_MESSAGE;
    public static String ALREADY_TYPING_PASS_MESSAGE;
    public static String NO_PASS_TYPED_MESSAGE;
    public static String PASS_ENTER_MESSAGE;
    public static String PASS_ENTERED_MESSAGE;
    public static String NON_SANCTIONED_ACCESS_MESSAGE;
    public static String INCORRECT_PASS_MESSAGE;

    public static String UNBLOCK_SUCCESS_MESSAGE;
    public static String UNBLOCK_NOT_FOUND_MESSAGE;

    public static String ALARM_PLACE_WORD;
    public static String ALARM_PLACE_MESSAGE;
    public static String ALARM_BREAK_MESSAGE;
    public static String ALREADY_PLACED_ALARM_MESSAGE;
    public static String ROCKET_START_DETECTED_MESSAGE;
    public static String ALARM_START_BROADCAST_MESSAGE;
    public static String ALARM_STOP_BROADCAST_MESSAGE;

    public static String ACID_RAIN_START_MESSAGE;
    public static String ACID_RAIN_STOP_MESSAGE;

    public static String RADIATION_START_MESSAGE;
    public static String RADIATION_STOP_MESSAGE;
    public static String NUCLEAR_ROCKET_DETONATED_MESSAGE;
    public static String SCULK_ROCKET_DETONATED_MESSAGE;

    public static String DEBUG_AUTO_UNBLOCK_MESSAGE;
    public static String DEBUG_ACID_RAIN_FOUND_MESSAGE;
    public static String DEBUG_BROKEN_SIREN_MESSAGE;

    public static String ROCKET_CODE_TYPING_SOUND;
    public static String ROCKET_INITIALIZED_SOUND;
    public static String ALARM_SOUND;
    public static String INVALID_PASS_BROADCAST_SOUND;
    public static String ROCKET_START_FLYING_SOUND;
    public static String ROCKET_FLYING_SOUND;
    public static String ROCKET_DETONATE_SOUND;

    private final AdventBombs plugin;
    private FileConfiguration config;

    public ConfigManager(AdventBombs plugin) {
        this.plugin = plugin;
        loadConfig();
        loadValues();
    }

    public void loadConfig() {
        // Берём значения из конфига или создаём его
        config = plugin.getConfig();

        config.addDefault("rockets.distance_to_move_with_y", 340);
        config.addDefault("rockets.flying_height", 250);
        config.addDefault("rockets.nuclear.password", "nuclear_start_pass");
        config.addDefault("rockets.sculk.password", "sculk_start_pass");

        config.addDefault("effects.radiation.effects_delay", 60);
        config.addDefault("effects.radiation.duration", 480);

        config.addDefault("effects.acid_rain.delay", 120);
        config.addDefault("effects.acid_rain.duration", 480);

        config.addDefault("alarm.place_word", "[сирена]");

        config.addDefault("messages.prefix", "§3[AdventBombs]");

        config.addDefault("messages.commands.incorrect_typing", "§3[AdventBombs] §fНеверный ввод. /rocket [nuclear/sculk] [X] [Z] [мощность 0-100]");
        config.addDefault("messages.commands.no_permissions", "§3[AdventBombs] §cУ тебя нет прав на использование этой команды!");
        config.addDefault("messages.commands.only_in_game", "§3[AdventBombs] §cЭту команду следует использовать в игре.");
        config.addDefault("messages.commands.cooldown", "§3[AdventBombs] §cПодожди {duration} сек, прежде чем снова запустить ракету!");
        config.addDefault("messages.commands.already_typing_pass", "§3[AdventBombs] §cТы уже в запросе ввода пароля! Заверши его, прежде чем начинать новый.");
        config.addDefault("messages.commands.player_blocked", "§3[AdventBombs] §cДоступ запрещен.");
        config.addDefault("messages.typing.no_pass_typed", "§3[AdventBombs] §cТы не ввёл пароль в течении 120 секунд!");
        config.addDefault("messages.typing.pass_enter", "§3[AdventBombs] §fВведи кодовое слово.");
        config.addDefault("messages.typing.pass_entered", "§3[AdventBombs] §fКод запуска ракеты принят. Инициализация...");
        config.addDefault("messages.typing.non_sanctioned_access", "§3[AdventBombs] §cОбнаружена попытка входа в систему управления термоядерными зарядами игроком §a{player}!");
        config.addDefault("messages.typing.incorrect_pass", "§3[AdventBombs] §fНеверный код запуска.");

        config.addDefault("messages.commands.unblock.success", "§3[AdventBombs] §fИгрок {player} был удален из списка блокировок.");
        config.addDefault("messages.commands.unblock.not_found", "§3[AdventBombs] §fИгрок {player} не найден в списке блокировок.");

        config.addDefault("messages.alarm.place", "§3[Центр оповещения населения] §fТы установил сирену. Теперь, во время воздушной тревоги, этот блок будет проигрывать звуковое оповещение!");
        config.addDefault("messages.alarm.break", "§3[Центр оповещения населения] §fТы сломал блок сирены!");
        config.addDefault("messages.alarm.already_placed", "§3[Центр оповещения населения] §cНа этом месте уже установлена сирена!");
        config.addDefault("messages.alarm.rocket_start_detected", "§3[Центр оповещения населения] §cЗАФИКСИРОВАН ПУСК МЕЖКОНТИНЕНТАЛЬНОЙ РАКЕТЫ НЕИЗВЕСТНОГО ТИПА!");
        config.addDefault("messages.alarm.start_broadcast", "§3[Центр оповещения населения] §cВНИМАНИЕ! ОБЪЯВЛЕНА ВОЗДУШНАЯ ТРЕВОГА! ВСЕМ ПРОЙТИ В УКРЫТИЕ!");
        config.addDefault("messages.alarm.stop_broadcast", "§3[Центр оповещения населения] §aВнимание! Отбой воздушной тревоги! Все ракеты достигли целей!");
        config.addDefault("messages.alarm.nuclear.detonated", "§3[Центр оповещения населения] §fМежконтинентальная ядерная ракета произвела детонацию!");
        config.addDefault("messages.alarm.sculk.detonated", "§3[Центр оповещения населения] §fСкалковая ракета достигла цели!");

        config.addDefault("messages.effects.acid_rain.start", "§3[Центр оповещения населения] §fСообщается о начале §aкислотного дождя §fв мире {world}. Все в укрытия!");
        config.addDefault("messages.effects.acid_rain.stop", "§3[Центр оповещения населения] §aКислотный дождь §fзакончился в мире {world}. Можно выходить из укрытий, будьте осторожны!");

        config.addDefault("messages.effects.radiation.start", "§3[Центр оповещения населения] §fСообщается о появлении §aрадиации §fв районе §a{cords} в мире {world}. §fВсе необходимо принимать меры предосторожности!");
        config.addDefault("messages.effects.radiation.stop", "§3[Центр оповещения населения] §fРадиация ослабевает в мире {world} по координатам §a{cords}");

        config.addDefault("messages.debug.auto_unblock", "§3[AdventBombs] §fИгрок {player} был удалён из списка блокировок!");
        config.addDefault("messages.debug.acid_rain_found", "§3[AdventBombs] §fНайден уже существующий кислотный дождь в мире {world}, отменяю его и вызываю новый (в итоге, он продлевается)..");
        config.addDefault("messages.debug.siren_broken", "§3[Центр оповещения населения] §fБлок сирены по локации {location} не найден! Удаляю!");

        config.addDefault("sounds.rocket.code_typing", "minecraft:my_sounds.rocket_code_typing");
        config.addDefault("sounds.rocket.initialized", "minecraft:my_sounds.rocket_initialized");
        config.addDefault("sounds.rocket.start_flying", "minecraft:my_sounds.rocket_start_flying");
        config.addDefault("sounds.rocket.flying", "minecraft:my_sounds.rocket_flying");
        config.addDefault("sounds.rocket.detonate", "minecraft:my_sounds.rocket_detonate");
        config.addDefault("sounds.rocket.invalid_pass_broadcast_sound", "minecraft:my_sounds.invalid_pass_sound");
        config.addDefault("sounds.alarm.sound", "minecraft:my_sounds.alarm_sound");

        config.options().copyDefaults(true);
        config.options().setHeader(List.of("На данный момент, конфиг в стадии тестирования. Репорти ошибки, если найдёшь", "Все параметры, которые так или иначе связаны со временем указывай в СЕКУНДАХ!"));
        plugin.saveConfig();
    }

    private void loadValues() {
        DISTANCE_TO_MOVE_ROCKET_WITH_Y = config.getInt("rockets.distance_to_move_with_y");
        FLYING_ROCKET_HEIGHT = config.getInt("rockets.flying_height");
        NUCLEAR_START_PASS = config.getString("rockets.nuclear.password");
        SCULK_START_PASS = config.getString("rockets.sculk.password");

        RADIATION_EFFECT_DELAY = config.getInt("effects.radiation.effects_delay");
        RADIATION_DURATION = config.getInt("effects.radiation.duration");

        ACID_RAIN_DELAY = config.getInt("effects.acid_rain.delay");
        ACID_RAIN_DURATION = config.getInt("effects.acid_rain.duration");

        ALARM_PLACE_WORD = config.getString("alarm.place_word");

        ONLY_IN_GAME_COMMAND = config.getString("messages.commands.only_in_game");
        NO_PERMISSIONS = config.getString("messages.commands.no_permissions");
        COOLDOWN_COMMAND = config.getString("messages.commands.cooldown");
        INCORRECT_TYPING_MESSAGE = config.getString("messages.commands.incorrect_typing");
        PLAYER_BLOCKED_MESSAGE = config.getString("messages.commands.player_blocked");
        ALREADY_TYPING_PASS_MESSAGE = config.getString("messages.commands.already_typing_pass");
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

        ACID_RAIN_START_MESSAGE = config.getString("messages.effects.acid_rain.start");
        ACID_RAIN_STOP_MESSAGE = config.getString("messages.effects.acid_rain.stop");

        RADIATION_START_MESSAGE = config.getString("messages.effects.radiation.start");
        RADIATION_STOP_MESSAGE = config.getString("messages.effects.radiation.stop");

        NUCLEAR_ROCKET_DETONATED_MESSAGE = config.getString("messages.alarm.nuclear.detonated");
        SCULK_ROCKET_DETONATED_MESSAGE = config.getString("messages.alarm.sculk.detonated");

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
}
