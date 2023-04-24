package com.genife.adventbombs.Effects;

import com.genife.adventbombs.AdventBombs;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public class Radiation extends BukkitRunnable {
    private final Location explosionCenter;
    private final int explosionPower;
    private final int radiationRadius;
    private final Map<Location, Map<Player, Long>> radiationZone;

    public Radiation(Location explosionCenter, int explosionPower) {
        this.explosionCenter = explosionCenter;
        this.explosionPower = explosionPower;
        this.radiationRadius = explosionPower * 3;
        this.radiationZone = new HashMap<>();
    }

    // отправляем глобальное сообщение о появлении радиации, стартуем задачи
    @Override
    public void run() {
        AdventBombs instance = AdventBombs.getInstance();

        // Оповещаем игроков о начале радиации
        String cords = explosionCenter.getBlockX() + ", " + explosionCenter.getBlockZ();
        Bukkit.broadcast(Component.text(MESSAGE_PREFIX + RADIATION_START_MESSAGE.replace("{cords}", cords).replace("{world}", explosionCenter.getWorld().getName())));

        // Создаем таск и запускаем его
        int radiationTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new RadiationEffect(explosionCenter, radiationRadius, explosionPower, radiationZone), 0L, 20L);
        // Завершаем задачу радиации через 7 минут
        Bukkit.getScheduler().runTaskLater(instance, new StopRadiation(radiationTaskId, explosionCenter, radiationZone), RADIATION_DURATION * 20L);
    }

    // эта задача добавляет/удаляет жертв радиации из списка
    // и выдаёт эффекты в зависимости от мощности бомбы, + радиус действия высчитывает по формуле
    private record RadiationEffect(Location explosionCenter, int radiationRadius, int explosionPower,
                                   Map<Location, Map<Player, Long>> radiationZone) implements Runnable {

        @Override
        public void run() {
            World world = explosionCenter.getWorld();
            // Добавляем/убираем (в блоке else) эффект радиации игрокам в радиусе X блоков от центра (зависит от мощности)
            for (Player player : world.getPlayers()) {
                if (player.getLocation().distance(explosionCenter) <= radiationRadius) {
                    Map<Player, Long> playersInZone = radiationZone.computeIfAbsent(explosionCenter, location -> new HashMap<>());

                    if (!playersInZone.containsKey(player)) {
                        // Игрок вошел в зону радиации
                        playersInZone.put(player, System.currentTimeMillis());
                    }
                } else {
                    Map<Player, Long> playersInZone = radiationZone.get(explosionCenter);
                    if (playersInZone != null) {
                        // Игрок вышел из зоны радиации, можно удалять даже если в списке не было. Устойчив .remove() к этому.
                        playersInZone.remove(player);
                    }
                }
            }

            // Проверяем время нахождения игроков в зоне радиации и добавляем эффекты
            long currentTime = System.currentTimeMillis();

            for (Map.Entry<Location, Map<Player, Long>> entry : radiationZone.entrySet()) {
                Map<Player, Long> playersInZone = entry.getValue();

                Iterator<Map.Entry<Player, Long>> iterator = playersInZone.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Player, Long> playerEntry = iterator.next();
                    Player player = playerEntry.getKey();
                    long enterTime = playerEntry.getValue();

                    if (!(player.isOnline() && player.getWorld().getName().equals(world.getName()))) {
                        iterator.remove();
                        continue;
                    }

                    if (currentTime - enterTime > RADIATION_EFFECT_DELAY * 1000) {

                        // эффекты к добавлению, "по ступенькам"
                        if (explosionPower > 0) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                        }
                        if (explosionPower > 20) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1));
                        }
                        if (explosionPower > 40) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
                        }
                        if (explosionPower > 60) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
                        }
                        if (explosionPower >= 100) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
                        }

                    }
                }
            }
        }
    }

    private record StopRadiation(int taskId, Location explosionCenter,
                                 Map<Location, Map<Player, Long>> radiationZone) implements Runnable {

        @Override
        public void run() {
            // Оповещаем игроков о конце радиации
            String cords = explosionCenter.getBlockX() + ", " + explosionCenter.getBlockZ();
            Bukkit.broadcast(Component.text(MESSAGE_PREFIX + RADIATION_STOP_MESSAGE.replace("{world}", explosionCenter.getWorld().getName()).replace("{cords}", cords)));

            radiationZone.remove(explosionCenter);
            // Отменяем задачу и очищаем зону
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}