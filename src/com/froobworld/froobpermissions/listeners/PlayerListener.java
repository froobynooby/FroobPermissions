package com.froobworld.froobpermissions.listeners;

import com.froobworld.froobpermissions.data.Group;
import com.froobworld.froobpermissions.managers.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private PlayerManager playerManager;

    public PlayerListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
        Group group = playerManager.getPlayerdata(e.getPlayer()).getGroup();
        if (group != null) {
            e.setFormat(group.getChatFormat());
            e.getPlayer().setDisplayName(group.getPrefix() + e.getPlayer().getName() + ChatColor.RESET);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerLoginEvent e) {
        playerManager.updatePlayer(e.getPlayer());

        Group group = playerManager.getPlayerdata(e.getPlayer()).getGroup();
        if (group != null) {
            e.getPlayer().setDisplayName(group.getPrefix() + e.getPlayer().getName() + ChatColor.RESET);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerManager.removeAttachment(e.getPlayer());
    }

}
