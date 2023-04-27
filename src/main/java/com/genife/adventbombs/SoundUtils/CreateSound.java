package com.genife.adventbombs.SoundUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CreateSound {

    // Функция для проигрывания звука в определенном радиусе,
    // с уникальной для каждого громкостью, на нужной локации
    public CreateSound(String soundName, int range, Location location) {
        Collection<Player> players = location.getWorld().getNearbyPlayers(location, range);
        new PlaySound(location, range, soundName, players.toArray(new Player[0]));
    }
}
