package org.gaegeumchi.gPlot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plot implements ConfigurationSerializable {
    private final String name;
    private final List<StoredBlock> blocks;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int connectionAX;
    private final int connectionAY;
    private final int connectionAZ;
    private final int connectionBX;
    private final int connectionBY;
    private final int connectionBZ;
    private final int connectionCX;
    private final int connectionCY;
    private final int connectionCZ;
    private final int connectionDX;
    private final int connectionDY;
    private final int connectionDZ;

    public Plot(String name, Location pos1, Location pos2, Location connectionA, Location connectionB, Location connectionC, Location connectionD) {
        this.name = name;
        this.blocks = new ArrayList<>();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        this.sizeX = maxX - minX + 1;
        this.sizeY = maxY - minY + 1;
        this.sizeZ = maxZ - minZ + 1;

        // Store connection point A as offset from minimum corner
        if (connectionA != null) {
            this.connectionAX = connectionA.getBlockX() - minX;
            this.connectionAY = connectionA.getBlockY() - minY;
            this.connectionAZ = connectionA.getBlockZ() - minZ;
        } else {
            // Default to origin (0, 0, 0) if no connection point specified
            this.connectionAX = 0;
            this.connectionAY = 0;
            this.connectionAZ = 0;
        }

        // Store connection point B as offset from minimum corner
        if (connectionB != null) {
            this.connectionBX = connectionB.getBlockX() - minX;
            this.connectionBY = connectionB.getBlockY() - minY;
            this.connectionBZ = connectionB.getBlockZ() - minZ;
        } else {
            // Default to origin (0, 0, 0) if no connection point specified
            this.connectionBX = 0;
            this.connectionBY = 0;
            this.connectionBZ = 0;
        }

        // Store connection point C as offset from minimum corner
        if (connectionC != null) {
            this.connectionCX = connectionC.getBlockX() - minX;
            this.connectionCY = connectionC.getBlockY() - minY;
            this.connectionCZ = connectionC.getBlockZ() - minZ;
        } else {
            // Default to origin (0, 0, 0) if no connection point specified
            this.connectionCX = 0;
            this.connectionCY = 0;
            this.connectionCZ = 0;
        }

        // Store connection point D as offset from minimum corner
        if (connectionD != null) {
            this.connectionDX = connectionD.getBlockX() - minX;
            this.connectionDY = connectionD.getBlockY() - minY;
            this.connectionDZ = connectionD.getBlockZ() - minZ;
        } else {
            // Default to origin (0, 0, 0) if no connection point specified
            this.connectionDX = 0;
            this.connectionDY = 0;
            this.connectionDZ = 0;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = pos1.getWorld().getBlockAt(x, y, z);

                    // Skip AIR and player heads (player heads contain player-specific NBT data)
                    if (block.getType() == Material.AIR ||
                        block.getType() == Material.PLAYER_HEAD ||
                        block.getType() == Material.PLAYER_WALL_HEAD) {
                        continue;
                    }

                    String blockDataString = block.getBlockData().getAsString();
                    blocks.add(new StoredBlock(
                        x - minX,
                        y - minY,
                        z - minZ,
                        blockDataString
                    ));
                }
            }
        }
    }

    public Plot(String name, List<StoredBlock> blocks, int sizeX, int sizeY, int sizeZ,
                int connectionAX, int connectionAY, int connectionAZ,
                int connectionBX, int connectionBY, int connectionBZ,
                int connectionCX, int connectionCY, int connectionCZ,
                int connectionDX, int connectionDY, int connectionDZ) {
        this.name = name;
        this.blocks = blocks;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.connectionAX = connectionAX;
        this.connectionAY = connectionAY;
        this.connectionAZ = connectionAZ;
        this.connectionBX = connectionBX;
        this.connectionBY = connectionBY;
        this.connectionBZ = connectionBZ;
        this.connectionCX = connectionCX;
        this.connectionCY = connectionCY;
        this.connectionCZ = connectionCZ;
        this.connectionDX = connectionDX;
        this.connectionDY = connectionDY;
        this.connectionDZ = connectionDZ;
    }

    public String getName() {
        return name;
    }

    public PlotCreationResult create(Location baseLocation) {
        // Calculate offset so that connection point A appears at baseLocation
        // baseLocation should be where connection A is, so we need to subtract the connection offset
        Location cornerLocation = baseLocation.clone().subtract(connectionAX, connectionAY, connectionAZ);

        for (StoredBlock storedBlock : blocks) {
            Location loc = cornerLocation.clone().add(storedBlock.x, storedBlock.y, storedBlock.z);
            BlockData blockData = org.bukkit.Bukkit.createBlockData(storedBlock.blockDataString);
            loc.getBlock().setBlockData(blockData);
        }

        // Calculate actual world locations of connection points
        Location actualConnectionA = cornerLocation.clone().add(connectionAX, connectionAY, connectionAZ);
        Location actualConnectionB = cornerLocation.clone().add(connectionBX, connectionBY, connectionBZ);
        Location actualConnectionC = cornerLocation.clone().add(connectionCX, connectionCY, connectionCZ);
        Location actualConnectionD = cornerLocation.clone().add(connectionDX, connectionDY, connectionDZ);

        return new PlotCreationResult(actualConnectionA, actualConnectionB, actualConnectionC, actualConnectionD);
    }

    public static class PlotCreationResult {
        public final Location connectionA;
        public final Location connectionB;
        public final Location connectionC;
        public final Location connectionD;

        public PlotCreationResult(Location connectionA, Location connectionB, Location connectionC, Location connectionD) {
            this.connectionA = connectionA;
            this.connectionB = connectionB;
            this.connectionC = connectionC;
            this.connectionD = connectionD;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("sizeX", sizeX);
        map.put("sizeY", sizeY);
        map.put("sizeZ", sizeZ);
        map.put("connectionAX", connectionAX);
        map.put("connectionAY", connectionAY);
        map.put("connectionAZ", connectionAZ);
        map.put("connectionBX", connectionBX);
        map.put("connectionBY", connectionBY);
        map.put("connectionBZ", connectionBZ);
        map.put("connectionCX", connectionCX);
        map.put("connectionCY", connectionCY);
        map.put("connectionCZ", connectionCZ);
        map.put("connectionDX", connectionDX);
        map.put("connectionDY", connectionDY);
        map.put("connectionDZ", connectionDZ);

        List<Map<String, Object>> blocksList = new ArrayList<>();
        for (StoredBlock block : blocks) {
            Map<String, Object> blockMap = new HashMap<>();
            blockMap.put("x", block.x);
            blockMap.put("y", block.y);
            blockMap.put("z", block.z);
            blockMap.put("blockData", block.blockDataString);
            blocksList.add(blockMap);
        }
        map.put("blocks", blocksList);

        return map;
    }

    @SuppressWarnings("unchecked")
    public static Plot deserialize(Map<String, Object> map) {
        String name = (String) map.get("name");
        int sizeX = (int) map.get("sizeX");
        int sizeY = (int) map.get("sizeY");
        int sizeZ = (int) map.get("sizeZ");

        // Support old plots without connection points
        int connectionAX = map.containsKey("connectionAX") ? (int) map.get("connectionAX") : 0;
        int connectionAY = map.containsKey("connectionAY") ? (int) map.get("connectionAY") : 0;
        int connectionAZ = map.containsKey("connectionAZ") ? (int) map.get("connectionAZ") : 0;
        int connectionBX = map.containsKey("connectionBX") ? (int) map.get("connectionBX") : 0;
        int connectionBY = map.containsKey("connectionBY") ? (int) map.get("connectionBY") : 0;
        int connectionBZ = map.containsKey("connectionBZ") ? (int) map.get("connectionBZ") : 0;
        int connectionCX = map.containsKey("connectionCX") ? (int) map.get("connectionCX") : 0;
        int connectionCY = map.containsKey("connectionCY") ? (int) map.get("connectionCY") : 0;
        int connectionCZ = map.containsKey("connectionCZ") ? (int) map.get("connectionCZ") : 0;
        int connectionDX = map.containsKey("connectionDX") ? (int) map.get("connectionDX") : 0;
        int connectionDY = map.containsKey("connectionDY") ? (int) map.get("connectionDY") : 0;
        int connectionDZ = map.containsKey("connectionDZ") ? (int) map.get("connectionDZ") : 0;

        List<Map<String, Object>> blocksList = (List<Map<String, Object>>) map.get("blocks");
        List<StoredBlock> blocks = new ArrayList<>();

        for (Map<String, Object> blockMap : blocksList) {
            blocks.add(new StoredBlock(
                (int) blockMap.get("x"),
                (int) blockMap.get("y"),
                (int) blockMap.get("z"),
                (String) blockMap.get("blockData")
            ));
        }

        return new Plot(name, blocks, sizeX, sizeY, sizeZ, connectionAX, connectionAY, connectionAZ,
                connectionBX, connectionBY, connectionBZ, connectionCX, connectionCY, connectionCZ,
                connectionDX, connectionDY, connectionDZ);
    }

    public static class StoredBlock {
        public final int x, y, z;
        public final String blockDataString;

        public StoredBlock(int x, int y, int z, String blockDataString) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockDataString = blockDataString;
        }
    }
}
