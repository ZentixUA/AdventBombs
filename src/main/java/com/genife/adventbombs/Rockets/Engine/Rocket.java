package com.genife.adventbombs.Rockets.Engine;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static com.genife.adventbombs.Managers.ConfigManager.MAX_FLY_DURATION;

public abstract class Rocket {
    private Location rocketLocation;
    private Location targetLocation;
    private World rocketWorld;
    private Vector vector;
    private int duration;
    private int explosionPower;
    private RocketState state;

    public Rocket(Player sender, Location targetLocation, int explosionPower) {
        setRocketLocation(sender.getLocation());
        setTargetLocation(targetLocation);
        setExplosionPower(explosionPower);
        setRocketWorld(sender.getWorld());
        setDuration(0);
    }

    public boolean isFlying() {
        return getState() == RocketState.FLYING;
    }

    public boolean isMovingWithY() {
        return getState() == RocketState.MOVING_WITH_Y;
    }

    public boolean isDead() {
        return getState() == RocketState.DEAD;
    }

    public abstract void move();

    public abstract void explode();

    public Location getRocketLocation() {
        return rocketLocation;
    }

    public void setRocketLocation(Location rocketLocation) {
        this.rocketLocation = rocketLocation;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location target_location) {
        this.targetLocation = target_location;
    }

    public boolean inBlock() {
        return !rocketLocation.getBlock().getType().isAir();
    }

    public World getRocketWorld() {
        return rocketWorld;
    }

    public void setRocketWorld(World rocketWorld) {
        this.rocketWorld = rocketWorld;
    }

    public Vector getVector() {
        return vector;
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }

    public RocketState getState() {
        return state;
    }

    public void setState(RocketState state) {
        this.state = state;
    }

    public boolean reachMaxDuration() {
        return duration >= MAX_FLY_DURATION * 20L;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void addDuration() {
        this.setDuration(getDuration() + 1);
    }

    public int getExplosionPower() {
        return explosionPower;
    }

    public void setExplosionPower(int explosionPower) {
        this.explosionPower = explosionPower;
    }
}