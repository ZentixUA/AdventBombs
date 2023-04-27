package com.genife.adventbombs.SoundUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaySound {

    public PlaySound(Location playLocation, int playRange, String soundName, Player[] targets) {
        for (Player target : targets) {
            target.playSound(target.getLocation(), soundName, convertForSound((float) target.getLocation().distance(playLocation), playRange), 1);
            System.out.println(target.getLocation().distance(playLocation));
        }
    }

    // вычисляет нужную громкость
    private static float convertForSound(float distance, int range) {
        return Math.max(0, 1 - (distance / range));
    }
}