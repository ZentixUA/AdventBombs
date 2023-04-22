package com.genife.adventbombs.SoundUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlaySound extends BukkitRunnable {
    private final int range;
    private final Location music;
    private final String sound;
    private final Set<Player> players = new HashSet<>();

    public PlaySound(JavaPlugin plugin, Location playLocation, int playRange, String soundName, Player[] targets) {
        this.music = playLocation;
        this.range = playRange;
        this.sound = soundName;

        players.addAll(Arrays.asList(targets));

        runTask(plugin);
    }

    // вычисляет нужную громкость
    public static float convertForSound(float distance, int range) {
        return Math.max(0, 1 - (distance / range));
    }

    @Override
    public void run() {
        for (Player target : players) {
            target.playSound(target.getLocation(), sound, convertForSound((float) target.getLocation().distance(music), range), 1);
        }
    }
}