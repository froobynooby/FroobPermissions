package com.froobworld.froobpermissions.commands;

import com.froobworld.frooblib.command.CommandExecutor;
import com.froobworld.frooblib.utils.CommandUtils;
import com.froobworld.frooblib.utils.PageUtils;
import com.froobworld.frooblib.uuid.UUIDManager;
import com.froobworld.froobpermissions.data.Group;
import com.froobworld.froobpermissions.data.Playerdata;
import com.froobworld.froobpermissions.managers.GroupManager;
import com.froobworld.froobpermissions.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SetgroupCommand extends CommandExecutor {
    private GroupManager groupManager;
    private PlayerManager playerManager;
    private UUIDManager uuidManager;

    public SetgroupCommand(GroupManager groupManager, PlayerManager playerManager, UUIDManager uuidManager) {
        this.groupManager = groupManager;
        this.playerManager = playerManager;
        this.uuidManager = uuidManager;
    }


    @Override
    public boolean onCommandProcess(CommandSender sender, Command command, String cl, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("/" + cl + " <player> <group>");
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

        Group group = groupManager.getGroup(args[1]);
        if (group == null) {
            sender.sendMessage(ChatColor.RED + "A group by that name does not exist.");
            return false;
        }

        if (sender instanceof Player) {
            Playerdata senderData = playerManager.getPlayerdata((Player) sender);
            if (!senderData.getGroup().doesInherit(group)) {
                sender.sendMessage(ChatColor.RED + "You can only add players to groups that you inherit.");
                return false;
            }
            if (!senderData.getGroup().doesInherit(data.getGroup())) {
                sender.sendMessage(ChatColor.RED + "You can only alter groups of players whose group you inherit.");
                return false;
            }
        }

        if (data.getGroup() == group) {
            sender.sendMessage(ChatColor.RED + "That player is already in that group.");
            return false;
        }

        String name = uuidManager.getUUIDData(data.getUUID()).getLastName();
        data.setGroup(group);
        if (player != null) {
            playerManager.updatePlayer(player);
        }
        Bukkit.broadcastMessage((ChatColor.YELLOW + name + " is now in group " + group.getName() + "."));

        return true;
    }

    @Override
    public String command() {

        return "setgroup";
    }

    @Override
    public String perm() {

        return "froobpermissions.setgroup";
    }

    @Override
    public List<String> tabCompletions(CommandSender sender, Command command, String cl, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            return CommandUtils.tabCompletePlayerList(args[0], true, true, uuidManager);
        }
        if (args.length == 2) {
            completions = PageUtils.toStringList(groupManager.getGroups());
        }

        completions = StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<String>(completions.size()));
        return completions;
    }

}
