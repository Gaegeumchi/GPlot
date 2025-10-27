package org.gaegeumchi.gPlot;

import org.bukkit.Location;

import java.util.*;

public class GroupInstanceManager {
    private final Map<String, GroupInstance> instances = new HashMap<>();

    public void addInstance(String instanceName, String groupName, Location lastHallwayB, Location lastPlotB) {
        instances.put(instanceName.toLowerCase(), new GroupInstance(instanceName, groupName, lastHallwayB, lastPlotB));
    }

    public GroupInstance getInstance(String instanceName) {
        return instances.get(instanceName.toLowerCase());
    }

    public boolean instanceExists(String instanceName) {
        return instances.containsKey(instanceName.toLowerCase());
    }

    public Collection<GroupInstance> getAllInstances() {
        return instances.values();
    }

    public Collection<GroupInstance> getInstancesByGroup(String groupName) {
        List<GroupInstance> result = new ArrayList<>();
        for (GroupInstance instance : instances.values()) {
            if (instance.getGroupName().equalsIgnoreCase(groupName)) {
                result.add(instance);
            }
        }
        return result;
    }

    public static class GroupInstance {
        private final String instanceName;
        private final String groupName;
        private Location lastHallwayB; // B connection of the last hallway
        private Location lastPlotB; // B connection of the last plot attached to hallway

        public GroupInstance(String instanceName, String groupName, Location lastHallwayB, Location lastPlotB) {
            this.instanceName = instanceName;
            this.groupName = groupName;
            this.lastHallwayB = lastHallwayB;
            this.lastPlotB = lastPlotB;
        }

        public String getInstanceName() {
            return instanceName;
        }

        public String getGroupName() {
            return groupName;
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
    }
}
