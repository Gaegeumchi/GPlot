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

public class SelectionListener implements Listener {
    private final SelectionManager selectionManager;

    public SelectionListener(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if player is holding the selection tool
        if (item.getType() != Material.BLAZE_ROD) {
            return;
        }

        if (item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        if (!item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "GPlot Selection Tool")) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            selectionManager.setFirstPosition(player, block.getLocation());
            player.sendMessage(ChatColor.GREEN + "Position 1 set at: " +
                ChatColor.WHITE + block.getX() + ", " + block.getY() + ", " + block.getZ());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            selectionManager.setSecondPosition(player, block.getLocation());
            player.sendMessage(ChatColor.GREEN + "Position 2 set at: " +
                ChatColor.WHITE + block.getX() + ", " + block.getY() + ", " + block.getZ());
        }
    }
}
