package com.genife.adventbombs.Rockets;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Effects.AcidRain;
import com.genife.adventbombs.Effects.Radiation;
import com.genife.adventbombs.Effects.Sculk;
import com.genife.adventbombs.Rockets.engine.Explodable;
import com.genife.adventbombs.Rockets.engine.RocketState;
import com.genife.adventbombs.Rockets.engine.Selfguided;
import com.genife.adventbombs.Rockets.engine.Soared;
import com.genife.adventbombs.Runnables.RocketRunnable;
import com.genife.adventbombs.SoundUtils.CreateSound;
import com.genife.adventbombs.Tools.getNearlyBlocks;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Objects;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public class RocketLogic extends Rocket implements Selfguided, Soared, Explodable {
    private static final int MIN_BEAM = 20;
    private final String rocketType;
    private final int explosionPower;
    private final AdventBombs instance = AdventBombs.getInstance();

    public RocketLogic(Player rocketSender, String rocketType, Location targetLocation, int explosionPower) {
        super(rocketSender, targetLocation);
        this.explosionPower = explosionPower;
        this.rocketType = rocketType;
    }

    @Override
    public void move() {
        // получаем дистанцию от ракеты к целевой локации
        double distanceToTargetLoc = getRocketLocation().distance(getTargetLocation());

        // проверка на совпадение одно из условий для детонации
        if ((reachMaxDuration() || inBlock() || (distanceToTargetLoc <= 1 && isDown())) && getDuration() > MIN_BEAM) {
            explode();
            return;
        }

        System.out.println(getRocketLocation().getBlockY());

        // движение ракеты, логика в зависимости от высоты
        if (getDuration() == 0) {
            getRocketLocation().setPitch(-90);
            new CreateSound(ROCKET_START_FLYING_SOUND, 200, getRocketLocation());

        } else if (getRocketLocation().getBlockY() < FLYING_ROCKET_HEIGHT && !isDown()) {
            if (getState() != RocketState.MOVING_UP) {
                setState(RocketState.MOVING_UP);
            }
            moveUp();

        } else if (distanceToTargetLoc >= DISTANCE_TO_MOVE_ROCKET_WITH_Y) {
            new CreateSound(ROCKET_FLYING_SOUND, 316, getRocketLocation());

            // Вычисляем отклонение координат X, Z между локациями ракеты и цели
            int differenceX = Math.abs(getRocketLocation().getBlockX() - getTargetLocation().getBlockX());
            int differenceZ = Math.abs(getRocketLocation().getBlockZ() - getTargetLocation().getBlockZ());

            // Проверяем, совпадают ли примерно X и Z, если да - начинаем движение с учётом Y до того момента,
            // пока итоговая дистанция между локациями ракеты и цели не станет меньше MAX_DISTANCE_TO_TARGET
            if (differenceX <= 3 && differenceZ <= 3) {
                moveWithY();
            } else {
                // в ином (нормальном) случае двигаемся по одной высоте - MIN_HEIGHT:
                if (getState() != RocketState.FLYING) {
                    setState(RocketState.FLYING);
                }
                getRocketLocation().add(findPath(false).multiply(FLYING_ROCKET_SPEED));
            }
        } else if (distanceToTargetLoc < DISTANCE_TO_MOVE_ROCKET_WITH_Y) {
            new CreateSound(ROCKET_FLYING_SOUND, 316, getRocketLocation());
            moveWithY();
        }

        getRocketWorld().spawnParticle(Particle.FIREWORKS_SPARK, getRocketLocation(), 0);
        getRocketWorld().spawnParticle(Particle.LAVA, getRocketLocation(), 0);
        addDuration();
    }

    @Override
    public void explode() {
        setState(RocketState.DEAD);

        if (Objects.equals(rocketType, "nuclear")) {
            Bukkit.broadcast(Component.text(MESSAGE_PREFIX + NUCLEAR_ROCKET_DETONATED_MESSAGE));
        } else if (Objects.equals(rocketType, "sculk")) {
            Bukkit.broadcast(Component.text(MESSAGE_PREFIX + SCULK_ROCKET_DETONATED_MESSAGE));
        }

        activeRocketChecker();

        World world = getRocketWorld();
        Location finalRocketLocation = getRocketLocation();

        if (Objects.equals(rocketType, "nuclear")) {
            // + 2 к высоте ибо взрыв с высокой вероятностью без этого почти не поразит игрока
            world.createExplosion(finalRocketLocation.clone().add(0, 2, 0), explosionPower, true, true);
            world.spawnParticle(Particle.EXPLOSION_LARGE, finalRocketLocation, 10);

            int soundPlayRange = explosionPower * 8;

            new CreateSound(ROCKET_DETONATE_SOUND, soundPlayRange, getRocketLocation());

            catastrophe(finalRocketLocation);

        } else if (Objects.equals(rocketType, "sculk")) {
            // ставим скалковые блоки
            Location catalystLocation = finalRocketLocation.clone();

            while (catalystLocation.getBlock().isLiquid()) {
                catalystLocation.add(0, -1, 0);
            }

            ArrayList<Block> nearlyBlocks = new getNearlyBlocks().getBlocks(catalystLocation.getBlock(), 1);

            for (Block block : nearlyBlocks) {
                if (!block.isLiquid() && !block.getType().isAir()) {
                    block.setType(Material.SCULK);
                }
            }

            catalystLocation.getBlock().setType(Material.SCULK_CATALYST);

            // спавним вардена
            Location wardenLocation = catalystLocation.clone().add(0, 2, 0);
            world.spawnEntity(wardenLocation, EntityType.WARDEN);

            // проигрываем звук активации портала в Энд всем в радиусе взрыва (мощности)
            new CreateSound("minecraft:block.end_portal.spawn", explosionPower, wardenLocation);

            // запускаем раннаблу спавна лисиц для распространения
            BukkitRunnable task = new Sculk(catalystLocation, explosionPower);
            task.runTaskTimer(instance, 0L, 0L);
        }
    }

    // эта функция отправляет оповещение, если прилетела последняя оставшаяся ракета
    // (RocketRunnable ещё не успел удалить ракету из списка)
    private void activeRocketChecker() {
        if (RocketRunnable.getActiveRocketCount() == 1) {
            Bukkit.broadcast(Component.text(MESSAGE_PREFIX + ALARM_STOP_BROADCAST_MESSAGE));
            instance.getAlarmManager().stopSirenTasks();
        }
    }

    private void catastrophe(Location explosionCenter) {
        new Radiation(explosionCenter, explosionPower).runTask(instance);
        if (explosionPower >= 50) {
            new AcidRain(getRocketWorld()).runTaskLater(instance, ACID_RAIN_DELAY * 20L);
        }
    }


    //------------------------------Вспомогательные функции движения ракеты-------------------------------


    @Override
    public void moveUp() {
        Vector dir = getRocketLocation().getDirection();
        dir.multiply(MOVE_UP_SPEED);
        // поднимаем ракету
        getRocketLocation().add(dir);
    }

    @Override
    public void moveWithY() {
        if (getState() != RocketState.MOVING_WITH_Y) {
            setState(RocketState.MOVING_WITH_Y);
        }
        // обновляем целевую локацию с актуальным Y
        getTargetLocation().setY(getRocketWorld().getHighestBlockYAt(getTargetLocation()));
        // двигаем ракету к цели
        getRocketLocation().add(findPath(true).multiply(MOVE_WITH_Y_SPEED));
    }

    public Vector findPath(boolean includeY) {
        Location from = getRocketLocation();
        Location to = getTargetLocation();
        double dX = from.getX() - to.getX();
        double dZ = from.getZ() - to.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch;
        double dY;
        double sqrt = Math.sqrt(dZ * dZ + dX * dX);
        if (includeY) {
            dY = from.getY() - to.getY();
            pitch = Math.atan2(sqrt, dY) + Math.PI;
        } else {
            // ракета придерживается своей высоты в таком случае
            pitch = Math.atan2(sqrt, 0) + Math.PI;
        }
        double X = Math.sin(pitch) * Math.cos(yaw);
        double Y = Math.sin(pitch) * Math.sin(yaw);
        double Z = Math.cos(pitch);
        return new Vector(X, Z, Y);
    }
}
