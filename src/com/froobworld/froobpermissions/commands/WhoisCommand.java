package com.froobworld.froobpermissions.commands;

import com.froobworld.frooblib.command.CommandExecutor;
import com.froobworld.frooblib.utils.CommandUtils;
import com.froobworld.frooblib.uuid.UUIDManager;
import com.froobworld.froobpermissions.data.Playerdata;
import com.froobworld.froobpermissions.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhoisCommand extends CommandExecutor {
    private PlayerManager playerManager;
    private UUIDManager uuidManager;

    public WhoisCommand(PlayerManager playerManager, UUIDManager uuidManager) {
        this.playerManager = playerManager;
        this.uuidManager = uuidManager;
    }


    @Override
    public boolean onCommandProcess(CommandSender sender, Command command, String cl, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + cl + " <player>");
            return false;
        }
        Player player = null;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().equalsIgnoreCase(args[0])) {
                player = online;
            }
        }
        Playerdata data = null;
        if (player != null) {
            data = playerManager.getPlayerdata(player);
        } else {
            data = playerManager.commandSearchForPlayer(args[0], sender, uuidManager);
        }
        if (data == null) {
            return false;
        }

        String name = uuidManager.getUUIDData(data.getUUID()).getLastName();
        sender.sendMessage(ChatColor.YELLOW + name + " is in the group " + data.getGroup().getName() + ".");
        return true;
    }

    @Override
    public String command() {

        return "whois";
    }

    @Override
    public String perm() {

        return "froobpermissions.whois";
    }

    @Override
    public List<String> tabCompletions(CommandSender sender, Command command, String cl, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            return CommandUtils.tabCompletePlayerList(args[0], true, true, uuidManager);
        }

        return completions;
    }

}
