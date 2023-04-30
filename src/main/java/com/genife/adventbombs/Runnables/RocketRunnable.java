package com.genife.adventbombs.Runnables;

import com.genife.adventbombs.Rockets.Engine.Rocket;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RocketRunnable extends BukkitRunnable {
    private static final List<Rocket> activeRockets = new ArrayList<>();
    private final Rocket rocket;

    public RocketRunnable(Rocket rocket) {
        this.rocket = rocket;
        activeRockets.add(rocket); // добавляем созданную ракету в список активных ракет
    }

    public static boolean isListEmpty() {
        return activeRockets.isEmpty(); // возвращаем true, если список пуст
    }

    @Override
    public void run() {
        if (rocket.isDead()) {
            activeRockets.remove(rocket); // убираем ракету из списка активных ракет
            this.cancel();
            return;
        }
        rocket.move();
    }
}