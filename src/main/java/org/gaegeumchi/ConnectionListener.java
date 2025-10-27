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

public class ConnectionListener implements Listener {
    private final ConnectionManager connectionManager;

    public ConnectionListener(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if player is holding the connection tool
        if (item.getType() != Material.STICK) {
            return;
        }

        if (item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        if (!item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "GPlot Connection Tool")) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            connectionManager.setConnectionA(player, block.getLocation());
            player.sendMessage(ChatColor.AQUA + "Connection Point A set at: " +
                ChatColor.WHITE + block.getX() + ", " + block.getY() + ", " + block.getZ());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            connectionManager.setConnectionB(player, block.getLocation());
            player.sendMessage(ChatColor.AQUA + "Connection Point B set at: " +
                ChatColor.WHITE + block.getX() + ", " + block.getY() + ", " + block.getZ());
        }
    }
}
