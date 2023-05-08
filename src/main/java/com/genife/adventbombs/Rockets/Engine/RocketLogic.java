package com.genife.adventbombs.Rockets.Engine;

import com.genife.adventbombs.SoundUtils.PlaySound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public abstract class RocketLogic extends Rocket implements Selfguided, Soared {

    public RocketLogic(Player rocketSender, Location targetLocation, int explosionPower) {
        super(rocketSender, targetLocation, explosionPower);
    }

    public void move() {
        // Обновляем целевую локацию с актуальным Y
        int highestBlockY = getRocketWorld().getHighestBlockYAt(getTargetLocation());
        if (getTargetLocation().getY() != highestBlockY) {
            getTargetLocation().setY(highestBlockY);

            if (isFlying()) {
                setVector(findPath(false));
            } else if (isMovingWithY()) {
                setVector(findPath(true));
            }
        }

        // Спавним эффекты
        getRocketWorld().spawnParticle(Particle.FIREWORKS_SPARK, getRocketLocation(), 0);
        getRocketWorld().spawnParticle(Particle.LAVA, getRocketLocation(), 0);

        // Получаем дистанцию от ракеты к целевой локации
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
            double differenceX = Math.abs(getRocketLocation().getX() - getTargetLocation().getX());
            double differenceZ = Math.abs(getRocketLocation().getZ() - getTargetLocation().getZ());

            // Проверяем, меньше ли наша дистанция дистанции для движения с учётом Y
            // Ещё проверяем, совпадают ли примерно X и Z, если да - начинаем движение с учётом Y до того момента,
            // пока итоговая дистанция между локациями ракеты и цели не станет меньше MAX_DISTANCE_TO_TARGET
            if (distanceToTargetLoc <= DISTANCE_TO_MOVE_ROCKET_WITH_Y || (differenceX <= FLYING_ROCKET_SPEED && differenceZ <= FLYING_ROCKET_SPEED)) {
                moveWithY();
            } else {
                moveWithoutY();
            }
        }
        addDuration();
    }


    //------------------------------Вспомогательные функции движения ракеты-------------------------------


    public void moveUp() {
        Vector dir = getRocketLocation().getDirection();
        dir.multiply(MOVE_UP_SPEED);
        // поднимаем ракету
        getRocketLocation().add(dir);
    }

    public void moveWithoutY() {
        // в ином (нормальном) случае двигаемся по одной высоте - MIN_HEIGHT
        if (getState() != RocketState.FLYING) {
            setState(RocketState.FLYING);
            setVector(findPath(false));
        }
        getRocketLocation().add(getVector().clone().multiply(FLYING_ROCKET_SPEED));
    }

    public void moveWithY() {
        // Двигаем ракету к цели с учётом Y
        if (getState() != RocketState.MOVING_WITH_Y) {
            setState(RocketState.MOVING_WITH_Y);
            setVector(findPath(true));
        }
        getRocketLocation().add(getVector().clone().multiply(MOVE_WITH_Y_SPEED));
    }

    public Vector findPath(boolean includeY) {
        Location targetLocation = getTargetLocation().clone();
        if (!includeY) targetLocation.setY(getRocketLocation().getY());
        return targetLocation.toVector().subtract(getRocketLocation().toVector()).normalize();
    }
}
