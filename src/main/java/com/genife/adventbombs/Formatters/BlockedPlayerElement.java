package com.genife.adventbombs.Formatters;

import java.util.UUID;

public record BlockedPlayerElement(UUID playerUUID, String playerName, long endTime) {
}
