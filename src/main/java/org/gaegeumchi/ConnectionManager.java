package org.gaegeumchi.gPlot;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConnectionManager {
    private final Map<UUID, Location> connectionA = new HashMap<>();
    private final Map<UUID, Location> connectionB = new HashMap<>();
    private final Map<UUID, Location> connectionC = new HashMap<>();
    private final Map<UUID, Location> connectionD = new HashMap<>();

    public void setConnectionA(Player player, Location location) {
        connectionA.put(player.getUniqueId(), location);
    }

    public void setConnectionB(Player player, Location location) {
        connectionB.put(player.getUniqueId(), location);
    }

    public void setConnectionC(Player player, Location location) {
        connectionC.put(player.getUniqueId(), location);
    }

    public void setConnectionD(Player player, Location location) {
        connectionD.put(player.getUniqueId(), location);
    }

    public Location getConnectionA(Player player) {
        return connectionA.get(player.getUniqueId());
    }

    public Location getConnectionB(Player player) {
        return connectionB.get(player.getUniqueId());
    }

    public Location getConnectionC(Player player) {
        return connectionC.get(player.getUniqueId());
    }

    public Location getConnectionD(Player player) {
        return connectionD.get(player.getUniqueId());
    }

    public boolean hasConnectionA(Player player) {
        return connectionA.containsKey(player.getUniqueId());
    }

    public void clearConnections(Player player) {
        UUID uuid = player.getUniqueId();
        connectionA.remove(uuid);
        connectionB.remove(uuid);
        connectionC.remove(uuid);
        connectionD.remove(uuid);
    }
}
