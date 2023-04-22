package com.genife.adventbombs.Runnables;

import com.genife.adventbombs.Rockets.Rocket;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RocketRunnable extends BukkitRunnable {
    private static final List<RocketRunnable> activeRockets = new ArrayList<>();
    private Rocket rocket;

    public RocketRunnable(Rocket rocket) {
        setRocket(rocket);
        activeRockets.add(this); // добавляем созданный инстанс в список активных ракет
    }

    public static int getActiveRocketCount() {
        return activeRockets.size(); // возвращаем размер списка активных ракет
    }

    @Override
    public void run() {
        if (rocket.isDead()) {
            this.cancel();
            activeRockets.remove(this); // убираем инстанс из списка активных ракет
            return;
        }
        rocket.move();
    }

    private void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }

}