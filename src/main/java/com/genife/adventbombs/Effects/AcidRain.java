package com.genife.adventbombs.Effects;

import com.genife.adventbombs.AdventBombs;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public class AcidRain extends BukkitRunnable {
    private static final Map<World, List<Integer>> acidRainTasks = new HashMap<>();
    private final World world;

    public AcidRain(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        AdventBombs instance = AdventBombs.getInstance();

        // Если мира нет в списке, то добавляем
        acidRainTasks.putIfAbsent(world, new ArrayList<>());
        // Получаем список задач по этому миру
        List<Integer> taskIds = acidRainTasks.get(world);

        // если по этому миру уже есть кислотный дождь, то мы его продлеваем перезапустив.
        if (!taskIds.isEmpty()) {
            Bukkit.getLogger().info(MESSAGE_PREFIX + DEBUG_ACID_RAIN_FOUND_MESSAGE.replace("{world}", world.getName()));
            for (int taskId : taskIds) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            taskIds.clear();
        } else {
            Bukkit.broadcast(Component.text(MESSAGE_PREFIX + ACID_RAIN_START_MESSAGE.replace("{world}", world.getName())));
        }

        // ставим нужную погоду
        world.setStorm(true);
        world.setWeatherDuration(8 * 60 * 20);

        // запускаем задачи
        int acidRainTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new AcidRainEffect(world), 0L, 20L);
        int stopAcidRainTaskId = Bukkit.getScheduler().runTaskLater(instance, new StopAcidRain(world, acidRainTasks), ACID_RAIN_DURATION * 20L).getTaskId();

        // добавляем их id в список id`шек
        taskIds.add(acidRainTaskId);
        taskIds.add(stopAcidRainTaskId);
    }

    private record AcidRainEffect(World world) implements Runnable {

        // выдаём эффекты кислотного дождя
        @Override
        public void run() {
            for (LivingEntity entity : world.getLivingEntities()) {
                World entityWorld = entity.getWorld();
                if (entity.getLocation().getY() >= entityWorld.getHighestBlockYAt(entity.getLocation()) - 1) {
                    if (entity.getType() == EntityType.WARDEN) {
                        continue;
                    }
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
                    if (entity instanceof Creature) {
                        onCreature((Creature) entity);
                    }
                }
            }
        }

        // функция для установки агрессивности к ближайшим игрокам всем Creature мобам (коровы, овцы и т.д)
        private void onCreature(Creature creature) {
            Player nearestPlayer = null;
            double nearestPlayerDistance = Double.MAX_VALUE;
            for (Player player : world.getPlayers()) {
                double distance = creature.getLocation().distance(player.getLocation());
                if (distance < nearestPlayerDistance) {
                    nearestPlayer = player;
                    nearestPlayerDistance = distance;
                }
            }
            if (nearestPlayer != null) {
                creature.setTarget(nearestPlayer);
            }
        }
    }

    private record StopAcidRain(World world, Map<World, List<Integer>> acidRainTasks) implements Runnable {
        // останавливаем кислотный дождь, ибо он "вылил свою душу"
        @Override
        public void run() {
            Bukkit.broadcast(Component.text(MESSAGE_PREFIX + ACID_RAIN_STOP_MESSAGE.replace("{world}", world.getName())));

            // Получаем список айдишников задач по миру (он тут как ключ)
            List<Integer> taskIds = acidRainTasks.get(world);

            // отменяем каждую созданную задачу (даже ОТМЕНЯЕМ задачу ОТМЕНЫ, хуже не делает)
            for (int taskId : taskIds) {
                Bukkit.getScheduler().cancelTask(taskId);
            }

            // удаляем мир из списка
            acidRainTasks.remove(world);
        }
    }
}