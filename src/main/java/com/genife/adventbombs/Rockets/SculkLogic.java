package com.genife.adventbombs.Rockets;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Effects.Sculk;
import com.genife.adventbombs.Rockets.Engine.GlobalLogic;
import com.genife.adventbombs.Rockets.Engine.RocketState;
import com.genife.adventbombs.SoundUtils.PlaySound;
import com.genife.adventbombs.Tools.getNearlyBlocks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static com.genife.adventbombs.Managers.ConfigManager.MESSAGE_PREFIX;
import static com.genife.adventbombs.Managers.ConfigManager.SCULK_ROCKET_DETONATED_MESSAGE;

public class SculkLogic extends GlobalLogic {
    private final int explosionPower;
    private final AdventBombs instance = AdventBombs.getInstance();

    public SculkLogic(Player rocketSender, Location targetLocation, int explosionPower) {
        super(rocketSender, targetLocation);
        this.explosionPower = explosionPower;
    }

    public void explode() {
        setState(RocketState.DEAD);

        Bukkit.broadcast(Component.text(MESSAGE_PREFIX + SCULK_ROCKET_DETONATED_MESSAGE));

        activeRocketChecker();

        World world = getRocketWorld();
        Location finalRocketLocation = getRocketLocation();

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
        new PlaySound("minecraft:block.end_portal.spawn", explosionPower, wardenLocation);

        // запускаем раннаблу спавна лисиц для распространения
        BukkitRunnable task = new Sculk(catalystLocation, explosionPower);
        task.runTaskTimer(instance, 0, 1);
    }
}
