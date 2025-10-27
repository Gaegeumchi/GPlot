package org.gaegeumchi.gPlot;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlotStorage {
    private final File plotsFolder;
    private final Map<String, Plot> plots = new HashMap<>();

    public PlotStorage(File dataFolder) {
        this.plotsFolder = new File(dataFolder, "plots");
        if (!plotsFolder.exists()) {
            plotsFolder.mkdirs();
        }
        loadAllPlots();
    }

    public void savePlot(Plot plot) {
        plots.put(plot.getName().toLowerCase(), plot);

        File file = new File(plotsFolder, plot.getName() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("plot", plot.serialize());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plot getPlot(String name) {
        return plots.get(name.toLowerCase());
    }

    public boolean plotExists(String name) {
        return plots.containsKey(name.toLowerCase());
    }

    public Set<String> getAllPlotNames() {
        return plots.keySet();
    }

    private void loadAllPlots() {
        File[] files = plotsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Map<String, Object> data = config.getConfigurationSection("plot").getValues(true);
                Plot plot = Plot.deserialize(data);
                plots.put(plot.getName().toLowerCase(), plot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
