package com.genife.adventbombs.Managers;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Formatters.AlarmElement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public class AlarmManager implements Listener {
    private final AdventBombs instance;
    private final List<AlarmElement> alarms = new ArrayList<>();
    private final List<BukkitRunnable> sirenTasks = new ArrayList<>();
    private final Gson gson;

    public AlarmManager(AdventBombs instance) {
        this.instance = instance;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadAlarms();
    }

    private void loadAlarms() {
        File alarmsFile = new File(instance.getDataFolder(), "alarms.json");
        if (alarmsFile.exists()) {
            try (Reader reader = new FileReader(alarmsFile)) {
                Type type = new TypeToken<List<AlarmElement>>() {
                }.getType();
                List<AlarmElement> savedAlarms = gson.fromJson(reader, type);
                alarms.addAll(savedAlarms);

                /*
                MigrationTool migrationTool = new MigrationTool(this);
                migrationTool.UpdateAlarms();
                */

            } catch (IOException e) {
                e.printStackTrace();
            }
            //loadMigratedAlarms();
        }
    }

    public void removeAlarmIfExists(Location blockLocation, Player player, String message) {

        // Если что, return просто возвращает наше условие в removeIf(). Я в начале ничего не понял)
        boolean alarmRemoved = alarms.removeIf(alarm -> {
            // Получаем данные сирены из списка (координаты)
            World alarmWorld = Bukkit.getWorld(alarm.getWorldName());
            Location jukeboxLocation = new Location(alarmWorld, alarm.getJukeboxX(), alarm.getJukeboxY(), alarm.getJukeboxZ());
            Location signLocation = new Location(alarmWorld, alarm.getSignX(), alarm.getSignY(), alarm.getSignZ());
            // Удаляем, если сирена с такими данными есть
            return blockLocation.equals(jukeboxLocation) || blockLocation.equals(signLocation);
        });

        if (alarmRemoved) {
            if (player != null) {
                player.sendMessage(message);
            } else {
                Bukkit.getLogger().info(message);
            }
            saveAlarms();
        }
    }

    public void ActualizeAlarms() {
        List<Location> blocksToRemove = new ArrayList<>(); // Новый список для хранения блоков, которые нужно удалить

        for (AlarmElement alarm : alarms) {

            World world = Bukkit.getWorld(alarm.getWorldName());
            Location jukeboxLocation = new Location(world, alarm.getJukeboxX(), alarm.getJukeboxY(), alarm.getJukeboxZ());
            Location signLocation = new Location(world, alarm.getSignX(), alarm.getSignY(), alarm.getSignZ());

            if (jukeboxLocation.getBlock().getType() != Material.JUKEBOX || !(signLocation.getBlock().getState() instanceof Sign) || !isSirenSignText(((Sign) signLocation.getBlock().getState()).getLines())) {
                blocksToRemove.add(jukeboxLocation); // Добавляем блок в список
            }
        }
        removeAlarmsList(blocksToRemove); // Удаляем блоки после завершения цикла
    }

    public void removeAlarmsList(List<Location> blocksToRemove) {
        for (Location location : blocksToRemove) {
            removeAlarmIfExists(location, null, MESSAGE_PREFIX + DEBUG_BROKEN_SIREN_MESSAGE.replace("{location}", location.toString()));
        }
    }

    public List<Location> getAlarmsLocations() {
        return alarms.stream()
                .map(alarm -> new Location(Bukkit.getWorld(alarm.getWorldName()), alarm.getJukeboxX(), alarm.getJukeboxY(), alarm.getJukeboxZ()))
                .collect(Collectors.toList());
    }

    public List<AlarmElement> getAlarmsList() {
        return alarms;
    }

    public void saveAlarms() {
        File alarmsFile = new File(instance.getDataFolder(), "alarms.json");
        if (!alarmsFile.exists()) {
            try {
                alarmsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Writer writer = new FileWriter(alarmsFile)) {
            gson.toJson(alarms, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSirenSignText(String[] lines) {
        for (String line : lines) {
            if (line.contains(ALARM_PLACE_WORD)) {
                return true;
            }
        }
        return false;
    }

    public void addSirenTask(BukkitRunnable sirenTask) {
        sirenTasks.add(sirenTask);
    }

    public boolean isSirenTasksEmpty() {
        return sirenTasks.isEmpty();
    }

    public void stopSirenTasks() {
        for (BukkitRunnable task : sirenTasks) {
            task.cancel();
        }
        sirenTasks.clear();
    }

    // Функция для обновления списка из старого типа в новый. Проверял - работает.
    // Требует класса со старым форматированием - AlarmV1, новым - Alarm

    /*
    private void loadMigratedAlarms() {
        File alarmsFile = new File(plugin.getDataFolder(), "alarms.json");
        if (alarmsFile.exists()) {
            try (Reader reader = new FileReader(alarmsFile)) {
                Type type = new TypeToken<List<AlarmV1>>() {
                }.getType();
                List<AlarmV1> oldAlarms = gson.fromJson(reader, type);

                for (AlarmV1 oldAlarm: oldAlarms) {
                    Alarm newAlarm = new Alarm(
                            oldAlarm.getWorldName(),
                            oldAlarm.getJukeboxX(),
                            oldAlarm.getJukeboxY(),
                            oldAlarm.getJukeboxZZZ(),
                            oldAlarm.getSignX(),
                            oldAlarm.getSignY(),
                            oldAlarm.getSignZ(),
                            oldAlarm.getType(),
                            oldAlarm.getBlockDataString());
                    alarms.add(newAlarm);
                }
                saveAlarms();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
     */
}