package org.gaegeumchi.gPlot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GPlotTabCompleter implements TabCompleter {
    private final PlotStorage plotStorage;
    private final GroupStorage groupStorage;

    public GPlotTabCompleter(PlotStorage plotStorage, GroupStorage groupStorage) {
        this.plotStorage = plotStorage;
        this.groupStorage = groupStorage;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: show all subcommands
            List<String> subcommands = Arrays.asList("tool", "save", "create", "add", "instance", "group", "teleport", "tp");
            return subcommands.stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            // For instance command, show subcommands
            if (args[0].equalsIgnoreCase("instance")) {
                return Arrays.asList("list").stream()
                        .filter(cmd -> cmd.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For group command, show subcommands
            if (args[0].equalsIgnoreCase("group")) {
                return Arrays.asList("create", "list", "instancelist", "add", "extend").stream()
                        .filter(cmd -> cmd.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For teleport command, show group names
            if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
                return new ArrayList<>(groupStorage.getAllGroupNames()).stream()
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For save command, don't show completions (player enters new name)
            if (args[0].equalsIgnoreCase("save")) {
                return new ArrayList<>();
            }
            // For create and add commands, no completion for instance name
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add")) {
                return new ArrayList<>();
            }
        } else if (args.length == 3) {
            // Third argument for create command: show plot template names
            if (args[0].equalsIgnoreCase("create")) {
                return new ArrayList<>(plotStorage.getAllPlotNames()).stream()
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // Third argument for add command: no completion for count
            if (args[0].equalsIgnoreCase("add")) {
                return Arrays.asList("1", "2", "3", "5", "10");
            }
            // For group create, no completion for group name
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("create")) {
                return new ArrayList<>();
            }
            // For group instancelist, show group names
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("instancelist")) {
                return new ArrayList<>(groupStorage.getAllGroupNames()).stream()
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For group add, show group names
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("add")) {
                return new ArrayList<>(groupStorage.getAllGroupNames()).stream()
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For group extend, show group names
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("extend")) {
                return new ArrayList<>(groupStorage.getAllGroupNames()).stream()
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For teleport, show instance names for the selected group
            if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
                String groupName = args[1];
                if (groupStorage.groupExists(groupName)) {
                    PlotGroup group = groupStorage.getGroup(groupName);
                    return group.getAllInstances().stream()
                            .map(PlotGroup.GroupInstance::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                            .collect(Collectors.toList());
                }
                return new ArrayList<>();
            }
        } else if (args.length == 4) {
            // For group create, show hallway template names
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("create")) {
                return new ArrayList<>(plotStorage.getAllPlotNames()).stream()
                        .filter(name -> name.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For group add, no completion for instance name (user enters it)
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("add")) {
                return new ArrayList<>();
            }
            // For group extend, no completion for new instance name (user enters it)
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("extend")) {
                return new ArrayList<>();
            }
        } else if (args.length == 5) {
            // For group create, show plot template names
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("create")) {
                return new ArrayList<>(plotStorage.getAllPlotNames()).stream()
                        .filter(name -> name.toLowerCase().startsWith(args[4].toLowerCase()))
                        .collect(Collectors.toList());
            }
            // For group add, show count suggestions
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("add")) {
                return Arrays.asList("1", "2", "3", "5", "10").stream()
                        .filter(num -> num.startsWith(args[4]))
                        .collect(Collectors.toList());
            }
            // For group extend, no count parameter needed (defaults to 1)
        } else if (args.length == 6) {
            // For group create, show C or D
            if (args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("create")) {
                return Arrays.asList("C", "D").stream()
                        .filter(cmd -> cmd.toUpperCase().startsWith(args[5].toUpperCase()))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}
