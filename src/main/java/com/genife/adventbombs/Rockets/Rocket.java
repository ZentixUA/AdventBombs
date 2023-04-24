package com.genife.adventbombs.Rockets;

import com.genife.adventbombs.Rockets.engine.RocketState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static com.genife.adventbombs.Managers.ConfigManager.MAX_FLY_DURATION;

public abstract class Rocket {
    private Location rocketLocation;
    private Location targetLocation;
    private World rocketWorld;
    private int duration;
    private RocketState state;

    public Rocket(Player sender, Location targetLocation) {
        setRocketLocation(sender.getLocation());
        setTargetLocation(targetLocation);
        setRocketWorld(sender.getWorld());
        setDuration(0);
    }

    public boolean isDown() {
        return getState() == RocketState.MOVING_WITH_Y;
    }

    public boolean isDead() {
        return getState() == RocketState.DEAD;
    }

    public abstract void move();

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
}