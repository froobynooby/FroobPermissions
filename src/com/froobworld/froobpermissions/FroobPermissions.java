package com.froobworld.froobpermissions;

import com.froobworld.froobbasics.FroobBasics;
import com.froobworld.frooblib.FroobPlugin;
import com.froobworld.frooblib.uuid.UUIDManager;
import com.froobworld.froobpermissions.commands.GroupCommand;
import com.froobworld.froobpermissions.commands.ListgroupsCommand;
import com.froobworld.froobpermissions.commands.SetgroupCommand;
import com.froobworld.froobpermissions.commands.WhoisCommand;
import com.froobworld.froobpermissions.listeners.DiscordListener;
import com.froobworld.froobpermissions.listeners.PlayerListener;
import com.froobworld.froobpermissions.managers.GroupManager;
import com.froobworld.froobpermissions.managers.PlayerManager;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class FroobPermissions extends FroobPlugin {
    final Plugin discord = Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV");
    private UUIDManager uuidManager;
    private GroupManager groupManager;
    private PlayerManager playerManager;
    private DiscordListener discordListener;
    private com.froobworld.froobbasics.managers.PlayerManager fbPlayerManager;

    public static Plugin getPlugin() {

        return getPlugin(FroobPermissions.class);
    }

    public void onEnable() {
        iniManagers();
        regCommands();
        regEvents();
    }

    public void onDisable() {
        playerManager.removeAllAttachments();
        if (discord != null) {
            DiscordSRV.api.unsubscribe(discordListener);
        }
    }

    public void regCommands() {
        registerCommand(new GroupCommand(groupManager, playerManager));
        registerCommand(new SetgroupCommand(groupManager, playerManager, uuidManager));
        registerCommand(new WhoisCommand(playerManager, uuidManager));
        registerCommand(new ListgroupsCommand(groupManager));
    }

    public void regEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(playerManager), this);
        if (discord != null) {
            DiscordSRV.api.subscribe(discordListener);
        }
    }

    public void iniManagers() {
        uuidManager = uuidManager();
        groupManager = new GroupManager();
        registerManager(groupManager);

        FroobBasics froobBasics = (FroobBasics) getServer().getPluginManager().getPlugin("FroobBasics");
        if (froobBasics != null) {
            fbPlayerManager = froobBasics.getPlayerManager();
        } else {
            fbPlayerManager = null;
        }

        playerManager = new PlayerManager(groupManager, fbPlayerManager);
        registerManager(playerManager);
        playerManager.updateAllPlayers();
        discordListener = new DiscordListener(playerManager);
    }

    public PlayerManager getPlayerManager() {

        return playerManager;
    }

    public GroupManager getGroupManager() {

        return groupManager;
    }
}
