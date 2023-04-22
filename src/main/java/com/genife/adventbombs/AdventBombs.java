package com.genife.adventbombs;

import com.genife.adventbombs.Commands.NuclearCommand;
import com.genife.adventbombs.Commands.NuclearCompleter;
import com.genife.adventbombs.Events.AlarmEvents;
import com.genife.adventbombs.Managers.AlarmManager;
import com.genife.adventbombs.Managers.PasswordManager;
import com.genife.adventbombs.Runnables.DataActualize;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AdventBombs extends JavaPlugin {
    private static AdventBombs instance;
    private static String nuclearCode;
    private static String sculkCode;
    private AlarmManager alarmManager;
    private PasswordManager passwordManager;


    public static AdventBombs getInstance() {
        return instance;
    }

    public static String getNuclearCode() {
        return nuclearCode;
    }

    public static String getSculkCode() {
        return sculkCode;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Инициализируем менеджер паролей
        passwordManager = new PasswordManager();
        // Инициализируем менеджер сирен
        alarmManager = new AlarmManager(this);

        this.getCommand("rocket").setExecutor(new NuclearCommand());
        this.getCommand("rocket").setTabCompleter(new NuclearCompleter());

        // берём значения из конфига / создаём его
        FileConfiguration config = getConfig();

        config.addDefault("nuclear_code", "nuclear_start_code");
        config.addDefault("sculk_code", "sculk_start_code");
        config.options().copyDefaults(true);
        saveConfig();

        nuclearCode = config.getString("nuclear_code");
        sculkCode = config.getString("sculk_code");

        // регистрируем ивенты из alarmManager
        getServer().getPluginManager().registerEvents(new AlarmEvents(), this);

        // запускаем задачу, которая каждую секунду актуализирует данные (сирены, списки блокировок и т.д)
        new DataActualize().runTaskTimer(this, 40L, 20L);
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }
}