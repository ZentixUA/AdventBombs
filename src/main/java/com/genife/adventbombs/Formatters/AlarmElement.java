package com.genife.adventbombs.Formatters;

import com.google.gson.Gson;

public class AlarmElement {
    // от порядка этих переменных зависит будущий порядок в JSON.
    private final String worldName;
    private final int jukeboxX;
    private final int jukeboxY;
    private final int jukeboxZ;
    private final int signX;
    private final int signY;
    private final int signZ;

    public AlarmElement(String worldName, int jukeboxX, int jukeboxY, int jukeboxZ, int signX, int signY, int signZ) {
        // функция для "формирования" новой сирены в JSON
        // (именно этот класс не совсем добавляет, а как вспомогательный, форматирует данные для добавления в наш список сирен и в JSON)
        this.worldName = worldName;
        this.jukeboxX = jukeboxX;
        this.jukeboxY = jukeboxY;
        this.jukeboxZ = jukeboxZ;
        this.signX = signX;
        this.signY = signY;
        this.signZ = signZ;
    }

    // ----------- функции для получения данных сирен в будущем -----------

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getJukeboxX() {
        return jukeboxX;
    }

    public int getJukeboxY() {
        return jukeboxY;
    }

    public int getJukeboxZ() {
        return jukeboxZ;
    }

    public int getSignX() {
        return signX;
    }

    public int getSignY() {
        return signY;
    }

    public int getSignZ() {
        return signZ;
    }

    // Функция для установки нового значения. Требует "saveAlarms()" после выполнения.
    /*
    public void setWorldName(String newWorldName) {
        this.worldName = newWorldName;
    }
    */

}