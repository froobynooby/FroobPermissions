package com.froobworld.froobpermissions.commands;

import com.froobworld.frooblib.command.CommandExecutor;
import com.froobworld.froobpermissions.data.Group;
import com.froobworld.froobpermissions.data.Playerdata;
import com.froobworld.froobpermissions.managers.GroupManager;
import com.froobworld.froobpermissions.managers.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Group_AddpermCommand extends CommandExecutor {
    private GroupManager groupManager;
    private PlayerManager playerManager;

    public Group_AddpermCommand(GroupManager groupManager, PlayerManager playerManager) {
        this.groupManager = groupManager;
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommandProcess(CommandSender sender, Command command, String cl, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("/" + cl + " <group> addperm <permission>");
        }

        Group group = groupManager.getGroup(args[0]);
        String perm = args[2].toLowerCase();

        if (group == null) {
            sender.sendMessage(ChatColor.RED + "A group by that name does not exist.");
            return false;
        }
        if (sender instanceof Player) {
            Playerdata data = playerManager.getPlayerdata((Player) sender);
            if (!data.getGroup().doesInherit(group)) {
                sender.sendMessage(ChatColor.RED + "You cannot modify a group you do not inherit.");
                return false;
            }
        }
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(ChatColor.RED + "You cannot add a permission which you do not have.");
            return false;
        }
        if (group.getPermissions().contains(perm)) {
            sender.sendMessage(ChatColor.RED + "That group already has this permission.");
            return false;
        }

        group.addPermission(perm);
        playerManager.updateAllPlayers();
        sender.sendMessage(ChatColor.YELLOW + "Permission successfully added.");

        return true;
    }

    @Override
    public String command() {

        return "group addperm";
    }

    @Override
    public String perm() {

        return "froobpermissions.group.addperm";
    }

    @Override
    public List<String> tabCompletions(CommandSender sender, Command command, String cl, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 2) {
            completions.add("info");
        }

        return completions;
    }
}
