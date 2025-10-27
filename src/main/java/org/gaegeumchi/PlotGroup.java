package org.gaegeumchi.gPlot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class PlotGroup implements ConfigurationSerializable {
    private final String name;
    private final String hallwayTemplateName;
    private final String plotTemplateName;
    private final String attachmentPoint; // "C" or "D"
    private final Map<String, GroupInstance> instances;

    public PlotGroup(String name, String hallwayTemplateName, String plotTemplateName, String attachmentPoint) {
        this.name = name;
        this.hallwayTemplateName = hallwayTemplateName;
        this.plotTemplateName = plotTemplateName;
        this.attachmentPoint = attachmentPoint;
        this.instances = new HashMap<>();
    }

    public PlotGroup(String name, String hallwayTemplateName, String plotTemplateName, String attachmentPoint, Map<String, GroupInstance> instances) {
        this.name = name;
        this.hallwayTemplateName = hallwayTemplateName;
        this.plotTemplateName = plotTemplateName;
        this.attachmentPoint = attachmentPoint;
        this.instances = instances;
    }

    public String getName() {
        return name;
    }

    public String getHallwayTemplateName() {
        return hallwayTemplateName;
    }

    public String getPlotTemplateName() {
        return plotTemplateName;
    }

    public String getAttachmentPoint() {
        return attachmentPoint;
    }

    public void addInstance(String instanceName, Location hallwayA, Location lastHallwayB, Location lastPlotB) {
        instances.put(instanceName.toLowerCase(), new GroupInstance(instanceName, hallwayA, lastHallwayB, lastPlotB));
    }

    public GroupInstance getInstance(String instanceName) {
        return instances.get(instanceName.toLowerCase());
    }

    public boolean hasInstance(String instanceName) {
        return instances.containsKey(instanceName.toLowerCase());
    }

    public Collection<GroupInstance> getAllInstances() {
        return instances.values();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("hallwayTemplateName", hallwayTemplateName);
        map.put("plotTemplateName", plotTemplateName);
        map.put("attachmentPoint", attachmentPoint);

        // Serialize instances
        List<Map<String, Object>> instancesList = new ArrayList<>();
        for (GroupInstance instance : instances.values()) {
            instancesList.add(instance.serialize());
        }
        map.put("instances", instancesList);

        return map;
    }

    @SuppressWarnings("unchecked")
    public static PlotGroup deserialize(Map<String, Object> map) {
        String name = (String) map.get("name");
        String hallwayTemplateName = (String) map.get("hallwayTemplateName");
        String plotTemplateName = (String) map.get("plotTemplateName");
        String attachmentPoint = (String) map.get("attachmentPoint");

        Map<String, GroupInstance> instances = new HashMap<>();
        if (map.containsKey("instances")) {
            List<Map<String, Object>> instancesList = (List<Map<String, Object>>) map.get("instances");
            for (Map<String, Object> instanceMap : instancesList) {
                GroupInstance instance = GroupInstance.deserialize(instanceMap);
                instances.put(instance.getName().toLowerCase(), instance);
            }
        }

        return new PlotGroup(name, hallwayTemplateName, plotTemplateName, attachmentPoint, instances);
    }

    public static class GroupInstance {
        private final String name;
        private final Location hallwayA;
        private Location lastHallwayB;
        private Location lastPlotB;

        public GroupInstance(String name, Location hallwayA, Location lastHallwayB, Location lastPlotB) {
            this.name = name;
            this.hallwayA = hallwayA;
            this.lastHallwayB = lastHallwayB;
            this.lastPlotB = lastPlotB;
        }

        public String getName() {
            return name;
        }

        public Location getHallwayA() {
            return hallwayA;
        }

        public Location getLastHallwayB() {
            return lastHallwayB;
        }

        public Location getLastPlotB() {
            return lastPlotB;
        }

        public void setLastHallwayB(Location lastHallwayB) {
            this.lastHallwayB = lastHallwayB;
        }

        public void setLastPlotB(Location lastPlotB) {
            this.lastPlotB = lastPlotB;
        }

        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);

            if (hallwayA != null) {
                map.put("hallwayA_world", hallwayA.getWorld().getName());
                map.put("hallwayA_x", hallwayA.getX());
                map.put("hallwayA_y", hallwayA.getY());
                map.put("hallwayA_z", hallwayA.getZ());
            }

            if (lastHallwayB != null) {
                map.put("lastHallwayB_world", lastHallwayB.getWorld().getName());
                map.put("lastHallwayB_x", lastHallwayB.getX());
                map.put("lastHallwayB_y", lastHallwayB.getY());
                map.put("lastHallwayB_z", lastHallwayB.getZ());
            }

            if (lastPlotB != null) {
                map.put("lastPlotB_world", lastPlotB.getWorld().getName());
                map.put("lastPlotB_x", lastPlotB.getX());
                map.put("lastPlotB_y", lastPlotB.getY());
                map.put("lastPlotB_z", lastPlotB.getZ());
            }

            return map;
        }

        public static GroupInstance deserialize(Map<String, Object> map) {
            String name = (String) map.get("name");

            Location hallwayA = null;
            if (map.containsKey("hallwayA_world")) {
                hallwayA = new Location(
                    Bukkit.getWorld((String) map.get("hallwayA_world")),
                    (double) map.get("hallwayA_x"),
                    (double) map.get("hallwayA_y"),
                    (double) map.get("hallwayA_z")
                );
            }

            Location lastHallwayB = null;
            if (map.containsKey("lastHallwayB_world")) {
                lastHallwayB = new Location(
                    Bukkit.getWorld((String) map.get("lastHallwayB_world")),
                    (double) map.get("lastHallwayB_x"),
                    (double) map.get("lastHallwayB_y"),
                    (double) map.get("lastHallwayB_z")
                );
            }

            Location lastPlotB = null;
            if (map.containsKey("lastPlotB_world")) {
                lastPlotB = new Location(
                    Bukkit.getWorld((String) map.get("lastPlotB_world")),
                    (double) map.get("lastPlotB_x"),
                    (double) map.get("lastPlotB_y"),
                    (double) map.get("lastPlotB_z")
                );
            }

            return new GroupInstance(name, hallwayA, lastHallwayB, lastPlotB);
        }
    }
}
