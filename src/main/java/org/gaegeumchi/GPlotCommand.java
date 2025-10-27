package org.gaegeumchi.gPlot;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Set;

public class GPlotCommand implements CommandExecutor {
    private final GPlot plugin;
    private final SelectionManager selectionManager;
    private final ConnectionManager connectionManager;
    private final PlotStorage plotStorage;
    private final PlotInstanceManager instanceManager;
    private final GroupStorage groupStorage;

    public GPlotCommand(GPlot plugin, SelectionManager selectionManager,
                        ConnectionManager connectionManager, PlotStorage plotStorage,
                        PlotInstanceManager instanceManager, GroupStorage groupStorage) {
        this.plugin = plugin;
        this.selectionManager = selectionManager;
        this.connectionManager = connectionManager;
        this.plotStorage = plotStorage;
        this.instanceManager = instanceManager;
        this.groupStorage = groupStorage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "tool":
                giveTools(player);
                break;

            case "save":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot save <name>");
                    return true;
                }
                savePlot(player, args[1]);
                break;

            case "create":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot create <name> <plotsavedfilename>");
                    return true;
                }
                createPlotInstance(player, args[1], args[2]);
                break;

            case "add":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot add <name> <count>");
                    return true;
                }
                try {
                    int count = Integer.parseInt(args[2]);
                    addPlots(player, args[1], count);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Count must be a number!");
                }
                break;

            case "instance":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot instance <list>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("list")) {
                    listInstances(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot instance <list>");
                }
                break;

            case "group":
                handleGroupCommand(player, args);
                break;

            case "teleport":
            case "tp":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot teleport <groupname> <instancename>");
                    return true;
                }
                teleportToInstance(player, args[1], args[2]);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void giveTools(Player player) {
        // Give selection tool (Blaze Rod)
        ItemStack selectionTool = new ItemStack(Material.BLAZE_ROD);
        ItemMeta selectionMeta = selectionTool.getItemMeta();
        selectionMeta.setDisplayName(ChatColor.GOLD + "GPlot Selection Tool");
        selectionTool.setItemMeta(selectionMeta);

        // Give connection tool (Stick)
        ItemStack connectionTool = new ItemStack(Material.STICK);
        ItemMeta connectionMeta = connectionTool.getItemMeta();
        connectionMeta.setDisplayName(ChatColor.AQUA + "GPlot Connection Tool");
        connectionTool.setItemMeta(connectionMeta);

        // Give connection tool 2 (Stick)
        ItemStack connectionTool2 = new ItemStack(Material.STICK);
        ItemMeta connectionMeta2 = connectionTool2.getItemMeta();
        connectionMeta2.setDisplayName(ChatColor.LIGHT_PURPLE + "GPlot Connection Tool 2");
        connectionTool2.setItemMeta(connectionMeta2);

        player.getInventory().addItem(selectionTool, connectionTool, connectionTool2);
        player.sendMessage(ChatColor.GREEN + "Selection tool (Blaze Rod) given! Left-click: pos 1, right-click: pos 2.");
        player.sendMessage(ChatColor.AQUA + "Connection tool (Stick) given! Left-click: Connection A, right-click: Connection B.");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Connection tool 2 (Stick) given! Left-click: Connection C, right-click: Connection D.");
    }

    private void savePlot(Player player, String name) {
        if (!selectionManager.hasSelection(player)) {
            player.sendMessage(ChatColor.RED + "You must select two positions first!");
            return;
        }

        if (plotStorage.plotExists(name)) {
            player.sendMessage(ChatColor.RED + "A plot with that name already exists!");
            return;
        }

        Plot plot = new Plot(name,
            selectionManager.getFirstPosition(player),
            selectionManager.getSecondPosition(player),
            connectionManager.getConnectionA(player),
            connectionManager.getConnectionB(player),
            connectionManager.getConnectionC(player),
            connectionManager.getConnectionD(player));

        plotStorage.savePlot(plot);
        player.sendMessage(ChatColor.GREEN + "Plot design '" + name + "' saved successfully!");

        if (connectionManager.hasConnectionA(player)) {
            player.sendMessage(ChatColor.AQUA + "Connection points A, B, C, and D saved with plot!");
        }

        selectionManager.clearSelection(player);
        connectionManager.clearConnections(player);
    }

    private void createPlotInstance(Player player, String instanceName, String templateName) {
        if (!plotStorage.plotExists(templateName)) {
            player.sendMessage(ChatColor.RED + "Plot template '" + templateName + "' does not exist!");
            return;
        }

        if (instanceManager.instanceExists(instanceName)) {
            player.sendMessage(ChatColor.RED + "Plot instance '" + instanceName + "' already exists!");
            return;
        }

        Plot plot = plotStorage.getPlot(templateName);
        Plot.PlotCreationResult result = plot.create(player.getLocation());

        instanceManager.addInstance(instanceName, templateName, result.connectionA, result.connectionB);
        player.sendMessage(ChatColor.GREEN + "Plot instance '" + instanceName + "' created using template '" + templateName + "'!");
    }

    private void addPlots(Player player, String instanceName, int count) {
        if (!instanceManager.instanceExists(instanceName)) {
            player.sendMessage(ChatColor.RED + "Plot instance '" + instanceName + "' does not exist!");
            return;
        }

        PlotInstanceManager.PlotInstance lastInstance = instanceManager.getInstance(instanceName);
        String templateName = lastInstance.getTemplateName();
        Plot plot = plotStorage.getPlot(templateName);

        if (plot == null) {
            player.sendMessage(ChatColor.RED + "Plot template '" + templateName + "' does not exist!");
            return;
        }

        for (int i = 0; i < count; i++) {
            // Connect new plot's A to previous plot's B
            Plot.PlotCreationResult result = plot.create(lastInstance.getConnectionB());

            // Update the last instance to point to the newly created one
            String newInstanceName = instanceName + "_added_" + (i + 1);
            instanceManager.addInstance(newInstanceName, templateName, result.connectionA, result.connectionB);
            lastInstance = instanceManager.getInstance(newInstanceName);
        }

        player.sendMessage(ChatColor.GREEN + "Added " + count + " plot(s) connected to '" + instanceName + "'!");
    }

    private void listInstances(Player player) {
        Collection<PlotInstanceManager.PlotInstance> instances = instanceManager.getAllInstances();

        if (instances.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No plot instances exist.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== Plot Instances ===");
        for (PlotInstanceManager.PlotInstance instance : instances) {
            player.sendMessage(ChatColor.YELLOW + instance.getName() +
                    ChatColor.WHITE + " (template: " + ChatColor.AQUA + instance.getTemplateName() + ChatColor.WHITE + ")");
        }
        player.sendMessage(ChatColor.GOLD + "Total: " + instances.size() + " instance(s)");
    }

    private void handleGroupCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /gplot group <create|instancelist|add|extend>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "create":
                if (args.length < 6) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot group create <groupname> <hallway_template> <plot_template> <C or D>");
                    return;
                }
                createGroup(player, args[2], args[3], args[4], args[5]);
                break;

            case "list":
                listGroups(player);
                break;

            case "instancelist":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot group instancelist <groupname>");
                    return;
                }
                listGroupInstances(player, args[2]);
                break;

            case "add":
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot group add <groupname> <instancename> [count]");
                    return;
                }
                int count = 1; // Default to 1 if not specified
                if (args.length >= 5) {
                    try {
                        count = Integer.parseInt(args[4]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Count must be a number!");
                        return;
                    }
                }
                addGroupPlots(player, args[2], args[3], count);
                break;

            case "extend":
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "Usage: /gplot group extend <groupname> <newinstancename>");
                    return;
                }
                // Default to 1 hallway+plot
                extendGroup(player, args[2], args[3], 1);
                break;

            default:
                player.sendMessage(ChatColor.RED + "Usage: /gplot group <create|instancelist|add|extend>");
                break;
        }
    }

    private void createGroup(Player player, String groupName, String hallwayTemplate, String plotTemplate, String attachPoint) {
        // Validate attachment point
        if (!attachPoint.equalsIgnoreCase("C") && !attachPoint.equalsIgnoreCase("D")) {
            player.sendMessage(ChatColor.RED + "Attachment point must be C or D!");
            return;
        }

        // Check if templates exist
        if (!plotStorage.plotExists(hallwayTemplate)) {
            player.sendMessage(ChatColor.RED + "Hallway template '" + hallwayTemplate + "' does not exist!");
            return;
        }

        if (!plotStorage.plotExists(plotTemplate)) {
            player.sendMessage(ChatColor.RED + "Plot template '" + plotTemplate + "' does not exist!");
            return;
        }

        // Check if group already exists
        if (groupStorage.groupExists(groupName)) {
            player.sendMessage(ChatColor.RED + "Group '" + groupName + "' already exists!");
            return;
        }

        // Create group
        PlotGroup group = new PlotGroup(groupName, hallwayTemplate, plotTemplate, attachPoint.toUpperCase());

        // Place first hallway at player location
        Plot hallway = plotStorage.getPlot(hallwayTemplate);
        Plot.PlotCreationResult hallwayResult = hallway.create(player.getLocation());

        // Place first plot attached to hallway's C or D pointoaky
        Plot plot = plotStorage.getPlot(plotTemplate);
        Location attachLocation = attachPoint.equalsIgnoreCase("C") ? hallwayResult.connectionC : hallwayResult.connectionD;
        Plot.PlotCreationResult plotResult = plot.create(attachLocation);

        // Add first instance named "test" to the group
        group.addInstance("test", hallwayResult.connectionA, hallwayResult.connectionB, plotResult.connectionB);

        // Save group with instance
        groupStorage.saveGroup(group);

        player.sendMessage(ChatColor.GREEN + "Group '" + groupName + "' created with first instance 'test'!");
        player.sendMessage(ChatColor.AQUA + "Hallway placed with plot attached to connection " + attachPoint + "!");
    }

    private void listGroupInstances(Player player, String groupName) {
        if (!groupStorage.groupExists(groupName)) {
            player.sendMessage(ChatColor.RED + "Group '" + groupName + "' does not exist!");
            return;
        }

        PlotGroup group = groupStorage.getGroup(groupName);
        Collection<PlotGroup.GroupInstance> instances = group.getAllInstances();

        if (instances.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No instances exist for group '" + groupName + "'.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== Instances for group '" + groupName + "' ===");
        for (PlotGroup.GroupInstance instance : instances) {
            player.sendMessage(ChatColor.YELLOW + instance.getName());
        }
        player.sendMessage(ChatColor.GOLD + "Total: " + instances.size() + " instance(s)");
    }

    private void listGroups(Player player) {
        Set<String> groupNames = groupStorage.getAllGroupNames();

        if (groupNames.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No groups exist.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== All Groups ===");
        for (String groupName : groupNames) {
            PlotGroup group = groupStorage.getGroup(groupName);
            int instanceCount = group.getAllInstances().size();
            player.sendMessage(ChatColor.YELLOW + groupName + ChatColor.WHITE + " (" + instanceCount + " instance" + (instanceCount != 1 ? "s" : "") + ")");
        }
        player.sendMessage(ChatColor.GOLD + "Total: " + groupNames.size() + " group(s)");
    }

    private void teleportToInstance(Player player, String groupName, String instanceName) {
        if (!groupStorage.groupExists(groupName)) {
            player.sendMessage(ChatColor.RED + "Group '" + groupName + "' does not exist!");
            return;
        }

        PlotGroup group = groupStorage.getGroup(groupName);

        if (!group.hasInstance(instanceName)) {
            player.sendMessage(ChatColor.RED + "Instance '" + instanceName + "' does not exist in group '" + groupName + "'!");
            return;
        }

        PlotGroup.GroupInstance instance = group.getInstance(instanceName);
        Location hallwayA = instance.getHallwayA();
        Location hallwayB = instance.getLastHallwayB();

        if (hallwayA == null || hallwayB == null) {
            player.sendMessage(ChatColor.RED + "Instance '" + instanceName + "' has no valid location data!");
            return;
        }

        // Calculate direction from A to B
        double dx = hallwayB.getX() - hallwayA.getX();
        double dy = hallwayB.getY() - hallwayA.getY();
        double dz = hallwayB.getZ() - hallwayA.getZ();

        // Calculate distance and normalize
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0) {
            // A and B are at the same location, just teleport to A
            Location teleportLoc = hallwayA.clone().add(0.5, 0, 0.5);
            player.teleport(teleportLoc);
            player.sendMessage(ChatColor.GREEN + "Teleported to instance '" + instanceName + "' in group '" + groupName + "'!");
            return;
        }

        double normX = dx / distance;
        double normY = dy / distance;
        double normZ = dz / distance;

        // Move 23 blocks from A in the direction of B
        double teleportX = hallwayA.getX() + (normX * 23) - 0.5;
        double teleportY = hallwayA.getY() + (normY * 23);
        double teleportZ = hallwayA.getZ() + (normZ * 23) - 0.5;

        // Calculate yaw (horizontal rotation) from direction vector
        // atan2(-dx, dz) gives angle in radians, convert to degrees
        float yaw = (float) Math.toDegrees(Math.atan2(-normX, normZ));

        // Add 90 degrees rotation
        yaw += 90;

        // Keep pitch at 0 (looking straight ahead)
        float pitch = 0;

        Location teleportLoc = new Location(hallwayA.getWorld(), teleportX, teleportY, teleportZ, yaw, pitch);
        player.teleport(teleportLoc);
        player.sendMessage(ChatColor.GREEN + "Teleported to instance '" + instanceName + "' in group '" + groupName + "'!");
    }

    private void addGroupPlots(Player player, String groupName, String instanceName, int count) {
        if (!groupStorage.groupExists(groupName)) {
            player.sendMessage(ChatColor.RED + "Group '" + groupName + "' does not exist!");
            return;
        }

        PlotGroup group = groupStorage.getGroup(groupName);

        if (!group.hasInstance(instanceName)) {
            player.sendMessage(ChatColor.RED + "Instance '" + instanceName + "' does not exist in group '" + groupName + "'!");
            return;
        }

        PlotGroup.GroupInstance instance = group.getInstance(instanceName);
        Plot plotTemplate = plotStorage.getPlot(group.getPlotTemplateName());

        if (plotTemplate == null) {
            player.sendMessage(ChatColor.RED + "Plot template '" + group.getPlotTemplateName() + "' does not exist!");
            return;
        }

        Location lastPlotB = instance.getLastPlotB();

        if (lastPlotB == null) {
            player.sendMessage(ChatColor.RED + "Instance '" + instanceName + "' has no connection point B! Instance may be corrupted.");
            return;
        }

        for (int i = 0; i < count; i++) {
            Plot.PlotCreationResult result = plotTemplate.create(lastPlotB);
            lastPlotB = result.connectionB;
        }

        instance.setLastPlotB(lastPlotB);
        groupStorage.saveGroup(group); // Save the updated group
        player.sendMessage(ChatColor.GREEN + "Added " + count + " plot(s) to instance '" + instanceName + "' in group '" + groupName + "'!");
    }

    private void extendGroup(Player player, String groupName, String newInstanceName, int count) {
        if (!groupStorage.groupExists(groupName)) {
            player.sendMessage(ChatColor.RED + "Group '" + groupName + "' does not exist!");
            return;
        }

        PlotGroup group = groupStorage.getGroup(groupName);

        if (group.hasInstance(newInstanceName)) {
            player.sendMessage(ChatColor.RED + "Instance '" + newInstanceName + "' already exists in group '" + groupName + "'!");
            return;
        }

        // Check if there are any existing instances to extend from
        if (group.getAllInstances().isEmpty()) {
            player.sendMessage(ChatColor.RED + "No instances exist in group '" + groupName + "' to extend from!");
            return;
        }

        Plot hallwayTemplate = plotStorage.getPlot(group.getHallwayTemplateName());
        Plot plotTemplate = plotStorage.getPlot(group.getPlotTemplateName());

        if (hallwayTemplate == null) {
            player.sendMessage(ChatColor.RED + "Hallway template '" + group.getHallwayTemplateName() + "' does not exist!");
            return;
        }

        if (plotTemplate == null) {
            player.sendMessage(ChatColor.RED + "Plot template '" + group.getPlotTemplateName() + "' does not exist!");
            return;
        }

        // Get the first existing instance to extend from (usually "test")
        PlotGroup.GroupInstance baseInstance = group.getAllInstances().iterator().next();
        Location currentHallwayB = baseInstance.getLastHallwayB();

        if (currentHallwayB == null) {
            player.sendMessage(ChatColor.RED + "Base instance has no hallway B connection point! Cannot extend.");
            return;
        }

        Location currentPlotB = null;
        Location firstHallwayA = null;

        for (int i = 0; i < count; i++) {
            // Place new hallway connected to previous hallway's B
            Plot.PlotCreationResult hallwayResult = hallwayTemplate.create(currentHallwayB);

            // Store the first hallway's connection A
            if (i == 0) {
                firstHallwayA = hallwayResult.connectionA;
            }

            // Place plot attached to hallway's C or D
            Location attachLocation = group.getAttachmentPoint().equalsIgnoreCase("C")
                ? hallwayResult.connectionC
                : hallwayResult.connectionD;
            Plot.PlotCreationResult plotResult = plotTemplate.create(attachLocation);

            currentHallwayB = hallwayResult.connectionB;
            currentPlotB = plotResult.connectionB;
        }

        // Add new instance to the group
        group.addInstance(newInstanceName, firstHallwayA, currentHallwayB, currentPlotB);
        groupStorage.saveGroup(group); // Save the updated group

        player.sendMessage(ChatColor.GREEN + "Created new instance '" + newInstanceName + "' in group '" + groupName + "' extending from '" + baseInstance.getName() + "' with " + count + " (hallway + plot)!");
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== GPlot Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/gplot tool" + ChatColor.WHITE + " - Get selection and connection tools");
        player.sendMessage(ChatColor.YELLOW + "/gplot save <name>" + ChatColor.WHITE + " - Save selected region as a plot template");
        player.sendMessage(ChatColor.YELLOW + "/gplot create <name> <template>" + ChatColor.WHITE + " - Create plot instance from template");
        player.sendMessage(ChatColor.YELLOW + "/gplot add <name> <count>" + ChatColor.WHITE + " - Add connected plots to existing instance");
        player.sendMessage(ChatColor.YELLOW + "/gplot instance list" + ChatColor.WHITE + " - List all plot instances");
        player.sendMessage(ChatColor.GOLD + "=== Group Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/gplot group create <name> <hallway> <plot> <C|D>" + ChatColor.WHITE + " - Create new group");
        player.sendMessage(ChatColor.YELLOW + "/gplot group list" + ChatColor.WHITE + " - List all groups");
        player.sendMessage(ChatColor.YELLOW + "/gplot group instancelist <group>" + ChatColor.WHITE + " - List group instances");
        player.sendMessage(ChatColor.YELLOW + "/gplot group add <group> <instance> [count]" + ChatColor.WHITE + " - Add plots to instance (default: 1)");
        player.sendMessage(ChatColor.YELLOW + "/gplot group extend <group> <newinstance>" + ChatColor.WHITE + " - Create new instance (1 hallway+plot)");
        player.sendMessage(ChatColor.YELLOW + "/gplot teleport <group> <instance>" + ChatColor.WHITE + " - Teleport to group instance");
    }
}
