package com.genife.adventbombs.SoundUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PlaySound {

    // Функция для проигрывания звука в определенном радиусе,
    // с уникальной для каждого громкостью, на нужной локации
    public PlaySound(String soundName, int playRange, Location playLocation) {
        Collection<Player> players = playLocation.getWorld().getNearbyPlayers(playLocation, playRange);
        for (Player target : players) {
            target.playSound(target.getLocation(), soundName, convertForSound((float) target.getLocation().distance(playLocation), playRange), 1);
        }
    }

    private static float convertForSound(float distance, int range) {
        return Math.max(0, 1 - (distance / range));
    }
}
