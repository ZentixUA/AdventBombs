package com.genife.adventbombs.Effects;

import com.genife.adventbombs.AdventBombs;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

public class Sculk extends BukkitRunnable {
    private final Location centerLocation;
    private final World centerWorld;
    private final int rocketPower;
    private int repeats = 0;

    public Sculk(Location centerLocation, int rocketPower) {
        this.centerLocation = centerLocation;
        this.centerWorld = this.centerLocation.getWorld();
        this.rocketPower = rocketPower;
    }

    // главная функция
    @Override
    public void run() {
        if (repeats == 0) {
            // подгружаем чанк, добавляем его тикет
            CompletableFuture<Chunk> sculk_chunk = centerWorld.getChunkAtAsync(centerLocation);
            sculk_chunk.thenAccept(chunk -> centerWorld.getChunkAtAsync(centerLocation, chunk.addPluginChunkTicket(AdventBombs.getInstance())));
        }

        // если номер повторного запуска это мощность, умноженная на 3, то отменяем задачу
        if (repeats == rocketPower * 3) {
            // удаляем тикет чанка
            CompletableFuture<Chunk> sculk_chunk = centerWorld.getChunkAtAsync(centerLocation);
            sculk_chunk.thenAccept(chunk -> centerWorld.getChunkAtAsync(centerLocation, chunk.removePluginChunkTicket(AdventBombs.getInstance())));

            this.cancel();
        }

        spawnMobs();
        repeats++;
    }

    // спавним мобов на локации
    private void spawnMobs() {
        Fox fox = (Fox) centerLocation.getWorld().spawnEntity(centerLocation, EntityType.FOX);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hideEntity(AdventBombs.getInstance(), fox);
        }
        fox.setSilent(true);
        fox.setHealth(0.0);
        fox.remove();
    }
}