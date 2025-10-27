package org.gaegeumchi.gPlot;

import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlotInstanceManager {
    private final Map<String, PlotInstance> instances = new HashMap<>();

    public void addInstance(String name, String templateName, Location connectionA, Location connectionB) {
        instances.put(name.toLowerCase(), new PlotInstance(name, templateName, connectionA, connectionB));
    }

    public PlotInstance getInstance(String name) {
        return instances.get(name.toLowerCase());
    }

    public boolean instanceExists(String name) {
        return instances.containsKey(name.toLowerCase());
    }

    public Collection<PlotInstance> getAllInstances() {
        return instances.values();
    }

    public static class PlotInstance {
        private final String name;
        private final String templateName;
        private final Location connectionA;
        private final Location connectionB;

        public PlotInstance(String name, String templateName, Location connectionA, Location connectionB) {
            this.name = name;
            this.templateName = templateName;
            this.connectionA = connectionA;
            this.connectionB = connectionB;
        }

        public String getName() {
            return name;
        }

        public String getTemplateName() {
            return templateName;
        }

        public Location getConnectionA() {
            return connectionA;
        }

        public Location getConnectionB() {
            return connectionB;
        }
    }
}
