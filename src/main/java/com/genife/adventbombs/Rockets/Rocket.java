package com.genife.adventbombs.Rockets;

import com.genife.adventbombs.Rockets.engine.RocketState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Rocket {
    private Location rocketLocation;
    private Location targetLocation;
    private World rocketWorld;
    private int beam;
    private int maxBeam;
    private RocketState state;

    public Rocket(Player sender, Location targetLocation, int maxBeam) {
        setRocketLocation(sender.getLocation());
        setTargetLocation(targetLocation);
        setRocketWorld(sender.getWorld());
        setBeam(0);
        setMaxBeam(maxBeam);
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

    public boolean reachMaxBeam() {
        return beam >= maxBeam;
    }

    public void setMaxBeam(int maxBeam) {
        this.maxBeam = maxBeam;
    }

    public int getBeam() {
        return beam;
    }

    public void setBeam(int beam) {
        this.beam = beam;
    }

    public void addBeam() {
        this.setBeam(getBeam() + 1);
    }
}