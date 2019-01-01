package com.froobworld.froobpermissions.listeners;

import com.froobworld.froobpermissions.managers.PlayerManager;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.dependencies.jda.core.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.core.entities.Role;

public class DiscordListener {
    private PlayerManager playerManager;

    public DiscordListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }


    @Subscribe
    public void onAccountLink(AccountLinkedEvent e) {
        String role = playerManager.getPlayerdata(e.getPlayer().getUniqueId()).getGroup().getDiscordRole();
        if (role == null) {
            return;
        }
        for (Guild guild : e.getUser().getMutualGuilds()) {
            for (Role r : guild.getRoles()) {
                if (r.getName().equalsIgnoreCase(role)) {
                    try {
                        guild.getController().addSingleRoleToMember(guild.getMember(e.getUser()), r).complete();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


}
