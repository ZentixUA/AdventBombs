package com.genife.adventbombs.Tools;

import com.genife.adventbombs.Formatters.AlarmElement;
import com.genife.adventbombs.Managers.AlarmManager;

public class MigrationTool {
    private final AlarmManager alarmManager;

    public MigrationTool(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }

    public void UpdateAlarms() {
        for (AlarmElement alarm : alarmManager.getAlarmsList()) {
            if (alarm.getWorldName() != null) {
                //alarm.setWorldName("test");
            }
        }

        alarmManager.saveAlarms();
    }

}
