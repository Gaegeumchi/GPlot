package org.gaegeumchi.gPlot;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {
    private final Map<UUID, Location> firstPositions = new HashMap<>();
    private final Map<UUID, Location> secondPositions = new HashMap<>();

    public void setFirstPosition(Player player, Location location) {
        firstPositions.put(player.getUniqueId(), location);
    }

    public void setSecondPosition(Player player, Location location) {
        secondPositions.put(player.getUniqueId(), location);
    }

    public Location getFirstPosition(Player player) {
        return firstPositions.get(player.getUniqueId());
    }

    public Location getSecondPosition(Player player) {
        return secondPositions.get(player.getUniqueId());
    }

    public boolean hasSelection(Player player) {
        UUID uuid = player.getUniqueId();
        return firstPositions.containsKey(uuid) && secondPositions.containsKey(uuid);
    }

    public void clearSelection(Player player) {
        UUID uuid = player.getUniqueId();
        firstPositions.remove(uuid);
        secondPositions.remove(uuid);
    }
}
