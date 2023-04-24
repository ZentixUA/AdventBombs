package com.genife.adventbombs.Runnables;

import com.genife.adventbombs.AdventBombs;
import com.genife.adventbombs.Formatters.BlockedPlayerElement;
import com.genife.adventbombs.Managers.PasswordManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static com.genife.adventbombs.Managers.ConfigManager.DEBUG_AUTO_UNBLOCK_MESSAGE;
import static com.genife.adventbombs.Managers.ConfigManager.MESSAGE_PREFIX;

public class DataActualize extends BukkitRunnable {
    private final PasswordManager passwordManager = AdventBombs.getInstance().getPasswordManager();

    @Override
    public void run() {
        AdventBombs.getInstance().getAlarmManager().ActualizeAlarms();

        for (BlockedPlayerElement blocked : passwordManager.getBlockedPlayers()) {
            UUID playerUUID = blocked.playerUUID();
            // если время блокировки игрока из списка прошло, то разблокируем
            if (System.currentTimeMillis() >= blocked.endTime()) {
                passwordManager.unblockPlayer(playerUUID);
                Bukkit.getLogger().info(MESSAGE_PREFIX + DEBUG_AUTO_UNBLOCK_MESSAGE.replace("{player}", blocked.playerName()));
            }
        }
    }
}
