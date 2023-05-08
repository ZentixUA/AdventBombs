package com.genife.adventbombs.Rockets;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Effects.AcidRain;
import com.genife.adventbombs.Effects.Radiation;
import com.genife.adventbombs.Rockets.Engine.RocketLogic;
import com.genife.adventbombs.Rockets.Engine.RocketState;
import com.genife.adventbombs.SoundUtils.PlaySound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public class NuclearLogic extends RocketLogic {
    private final AdventBombs instance = AdventBombs.getInstance();

    public NuclearLogic(Player rocketSender, Location targetLocation, int explosionPower) {
        super(rocketSender, targetLocation, explosionPower);
    }

    public void explode() {
        // Если ракета уже взорвана и мы каким-то образом снова пытаемся вызвать функцию - ничего не делаем
        if (isDead()) {
            return;
        }

        setState(RocketState.DEAD);

        Bukkit.broadcast(Component.text(MESSAGE_PREFIX + NUCLEAR_ROCKET_DETONATED_MESSAGE));

        World world = getRocketWorld();
        Location finalRocketLocation = getRocketLocation();

        // + 2 к высоте ибо взрыв с высокой вероятностью без этого почти не поразит игрока
        world.createExplosion(finalRocketLocation.clone().add(0, 2, 0), getExplosionPower(), true, true);
        world.spawnParticle(Particle.EXPLOSION_LARGE, finalRocketLocation, 10);

        int soundPlayRange = getExplosionPower() * 8;

        new PlaySound(ROCKET_DETONATE_SOUND, soundPlayRange, finalRocketLocation);

        catastrophe(finalRocketLocation);
    }

    private void catastrophe(Location explosionCenter) {
        new Radiation(explosionCenter, getExplosionPower()).runTask(instance);
        if (getExplosionPower() >= 50) {
            new AcidRain(getRocketWorld()).runTaskLater(instance, ACID_RAIN_DELAY * 20);
        }
    }
}
