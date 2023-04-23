package com.genife.adventbombs.Events;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Formatters.AlarmElement;
import com.genife.adventbombs.Managers.AlarmManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import static com.genife.adventbombs.Managers.ConfigManager.*;

public class AlarmEvents implements Listener {
    AlarmManager alarmManager = AdventBombs.getInstance().getAlarmManager();

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();
        BlockData blockData = block.getBlockData();

        if (alarmManager.isSirenSignText(event.getLines())) {
            if (blockData instanceof WallSign wallSign) {

                Block attachedBlock = block.getRelative(wallSign.getFacing().getOppositeFace());
                if (attachedBlock.getType() != Material.JUKEBOX) {
                    return;
                }

                Location jukeboxLocation = attachedBlock.getLocation();

                // Добавляем, если совпадений нет (если такого jukebox нет в списке, то значит это новая сирена, потому добавляем)
                if (alarmManager.getAlarmsList().stream().noneMatch(alarm -> jukeboxLocation.equals(new Location(Bukkit.getWorld(alarm.getWorldName()), alarm.getJukeboxX(), alarm.getJukeboxY(), alarm.getJukeboxZ())))) {

                    // Создаём новую сирену, форматируя нужные данные под AlarmFormatter (координаты и т.д)
                    AlarmElement alarm = new AlarmElement(jukeboxLocation.getWorld().getName(), attachedBlock.getX(),
                            attachedBlock.getY(), attachedBlock.getZ(), event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
                    alarmManager.getAlarmsList().add(alarm);
                    // сохранение обновлённого списка сирен в файл
                    alarmManager.saveAlarms();

                    // отправляем сообщение
                    player.sendMessage(MESSAGE_PREFIX + ALARM_PLACE_MESSAGE);
                } else {
                    // если сирена уже существует, отправляем предупреждение.
                    player.sendMessage(MESSAGE_PREFIX + ALREADY_PLACED_ALARM_MESSAGE);
                }
            }
        }
    }


    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.JUKEBOX || block.getState() instanceof Sign) {
            Location block_location = block.getLocation();
            alarmManager.removeAlarmIfExists(block_location, event.getPlayer(), ALARM_BREAK_MESSAGE);
        }
    }
}
