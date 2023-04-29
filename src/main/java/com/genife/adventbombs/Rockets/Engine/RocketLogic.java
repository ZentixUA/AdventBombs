package com.genife.adventbombs.Rockets.Engine;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Runnables.RocketRunnable;
import com.genife.adventbombs.SoundUtils.PlaySound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public abstract class RocketLogic extends Rocket implements Selfguided, Soared, Explodable {

    private final AdventBombs instance = AdventBombs.getInstance();

    public RocketLogic(Player rocketSender, Location targetLocation) {
        super(rocketSender, targetLocation);
    }

    public void move() {
        getRocketWorld().spawnParticle(Particle.FIREWORKS_SPARK, getRocketLocation(), 0);
        getRocketWorld().spawnParticle(Particle.LAVA, getRocketLocation(), 0);
        // получаем дистанцию от ракеты к целевой локации
        double distanceToTargetLoc = getRocketLocation().distance(getTargetLocation());

        // Проверка на совпадение одно из условий для детонации. Такое сравнивание
        // дистанции необходимо, что бы ракета случайно не перемещалась туда-сюда
        // если её цель в бездне, т.е, что бы не пропустила финальную точку, так сказать
        if (reachMaxDuration() || inBlock() || (distanceToTargetLoc <= MOVE_WITH_Y_SPEED && isMovingWithY())) {
            explode();
            return;
        }

        // Действия в зависимости от высоты, дистанции и других факторов
        if (getDuration() == 0) {
            // .setPitch() обязателен, ибо он определяет под каким углом будет ракета при взлёте
            // в этом случае ровно вверх по Y (в сторону увеличения)
            getRocketLocation().setPitch(-90);
            new PlaySound(ROCKET_START_FLYING_SOUND, 200, getRocketLocation());
        }

        if (getRocketLocation().getBlockY() < FLYING_ROCKET_HEIGHT && !isMovingWithY() && !isFlying()) {
            if (getState() != RocketState.MOVING_UP) {
                setState(RocketState.MOVING_UP);
            }
            moveUp();
        } else {
            new PlaySound(ROCKET_FLYING_SOUND, 316, getRocketLocation());

            // Вычисляем отклонение координат X, Z между локациями ракеты и цели
            int differenceX = Math.abs(getRocketLocation().getBlockX() - getTargetLocation().getBlockX());
            int differenceZ = Math.abs(getRocketLocation().getBlockZ() - getTargetLocation().getBlockZ());

            // Проверяем, меньше ли наша дистанция дистанции для движения с учётом Y
            // Ещё проверяем, совпадают ли примерно X и Z, если да - начинаем движение с учётом Y до того момента,
            // пока итоговая дистанция между локациями ракеты и цели не станет меньше MAX_DISTANCE_TO_TARGET
            if (distanceToTargetLoc <= DISTANCE_TO_MOVE_ROCKET_WITH_Y || (differenceX <= FLYING_ROCKET_SPEED && differenceZ <= FLYING_ROCKET_SPEED)) {
                moveWithY();
            } else {
                // в ином (нормальном) случае двигаемся по одной высоте - MIN_HEIGHT:
                if (getState() != RocketState.FLYING) {
                    setState(RocketState.FLYING);
                }
                getRocketLocation().add(findPath(false).multiply(FLYING_ROCKET_SPEED));
            }
        }
        addDuration();
    }

    public abstract void explode();

    // эта функция отправляет оповещение, если прилетела последняя оставшаяся ракета
    // (RocketRunnable ещё не успел удалить ракету из списка)
    public void activeRocketChecker() {
        if (RocketRunnable.getActiveRocketCount() == 1) {
            Bukkit.broadcast(Component.text(MESSAGE_PREFIX + ALARM_STOP_BROADCAST_MESSAGE));
            instance.getAlarmManager().stopSirenTasks();
        }
    }


    //------------------------------Вспомогательные функции движения ракеты-------------------------------


    public void moveUp() {
        Vector dir = getRocketLocation().getDirection();
        dir.multiply(MOVE_UP_SPEED);
        // поднимаем ракету
        getRocketLocation().add(dir);
    }

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
        Location targetLocation = getTargetLocation().clone();
        if (!includeY) targetLocation.setY(getRocketLocation().getY());
        return targetLocation.toVector().subtract(getRocketLocation().toVector()).normalize();
    }
}