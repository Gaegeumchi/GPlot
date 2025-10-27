package org.gaegeumchi.gPlot;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupStorage {
    private final File groupsFolder;
    private final Map<String, PlotGroup> groups = new HashMap<>();

    public GroupStorage(File dataFolder) {
        this.groupsFolder = new File(dataFolder, "groups");
        if (!groupsFolder.exists()) {
            groupsFolder.mkdirs();
        }
        loadAllGroups();
    }

    public void saveGroup(PlotGroup group) {
        groups.put(group.getName().toLowerCase(), group);

        File file = new File(groupsFolder, group.getName() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("group", group.serialize());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlotGroup getGroup(String name) {
        return groups.get(name.toLowerCase());
    }

    public boolean groupExists(String name) {
        return groups.containsKey(name.toLowerCase());
    }

    public Set<String> getAllGroupNames() {
        return groups.keySet();
    }

    private void loadAllGroups() {
        File[] files = groupsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Map<String, Object> data = config.getConfigurationSection("group").getValues(true);
                PlotGroup group = PlotGroup.deserialize(data);
                groups.put(group.getName().toLowerCase(), group);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
