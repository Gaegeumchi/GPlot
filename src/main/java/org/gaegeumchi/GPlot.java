package org.gaegeumchi.gPlot;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class GPlot extends JavaPlugin {

    private SelectionManager selectionManager;
    private ConnectionManager connectionManager;
    private PlotStorage plotStorage;
    private PlotInstanceManager instanceManager;
    private GroupStorage groupStorage;

    @Override
    public void onEnable() {
        // Initialize managers
        selectionManager = new SelectionManager();
        connectionManager = new ConnectionManager();
        plotStorage = new PlotStorage(getDataFolder());
        instanceManager = new PlotInstanceManager();
        groupStorage = new GroupStorage(getDataFolder());

        // Register command and tab completer
        getCommand("gplot").setExecutor(new GPlotCommand(this, selectionManager, connectionManager, plotStorage, instanceManager, groupStorage));
        getCommand("gplot").setTabCompleter(new GPlotTabCompleter(plotStorage, groupStorage));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new SelectionListener(selectionManager), this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(connectionManager), this);
        getServer().getPluginManager().registerEvents(new ConnectionTool2Listener(connectionManager), this);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GPlot] Plugin has been enabled!");
        getLogger().info("GPlot has been enabled!");
        getLogger().info("[GPlot] Beta 1.6 By Gaegeumchi");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GPlot] Plugin has been disabled!");
        getLogger().info("GPlot has been disabled!");
    }
}
