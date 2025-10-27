package org.gaegeumchi.gPlot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ConnectionTool2Listener implements Listener {
    private final ConnectionManager connectionManager;

    public ConnectionTool2Listener(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if player is holding the connection tool 2
        if (item.getType() != Material.STICK) {
            return;
        }

        if (item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        if (!item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "GPlot Connection Tool 2")) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            connectionManager.setConnectionC(player, block.getLocation());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Connection Point C set at: " +
                ChatColor.WHITE + block.getX() + ", " + block.getY() + ", " + block.getZ());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            connectionManager.setConnectionD(player, block.getLocation());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Connection Point D set at: " +
                ChatColor.WHITE + block.getX() + ", " + block.getY() + ", " + block.getZ());
        }
    }
}
