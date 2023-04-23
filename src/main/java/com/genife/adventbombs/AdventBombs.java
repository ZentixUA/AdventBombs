package com.genife.adventbombs;

import com.genife.adventbombs.Commands.RocketCommand;
import com.genife.adventbombs.Commands.RocketCompleter;
import com.genife.adventbombs.Events.AlarmEvents;
import com.genife.adventbombs.Managers.AlarmManager;
import com.genife.adventbombs.Managers.ConfigManager;
import com.genife.adventbombs.Managers.PasswordManager;
import com.genife.adventbombs.Runnables.DataActualize;
import org.bukkit.plugin.java.JavaPlugin;

public class AdventBombs extends JavaPlugin {
    private static AdventBombs instance;
    private AlarmManager alarmManager;
    private PasswordManager passwordManager;


    public static AdventBombs getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Инициализируем конфиг
        new ConfigManager(this);
        // Инициализируем менеджер паролей
        passwordManager = new PasswordManager();
        // Инициализируем менеджер сирен
        alarmManager = new AlarmManager(this);

        //noinspection DataFlowIssue
        this.getCommand("rocket").setExecutor(new RocketCommand());
        //noinspection DataFlowIssue
        this.getCommand("rocket").setTabCompleter(new RocketCompleter());

        // регистрируем ивенты из alarmManager
        getServer().getPluginManager().registerEvents(new AlarmEvents(), this);

        // запускаем задачу, которая каждую секунду актуализирует данные (сирены, списки блокировок и т.д)
        new DataActualize().runTaskTimer(this, 40L, 20L);
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

}