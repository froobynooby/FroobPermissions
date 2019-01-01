package com.froobworld.froobpermissions.commands;

import com.froobworld.frooblib.command.CommandExecutor;
import com.froobworld.frooblib.utils.PageUtils;
import com.froobworld.froobpermissions.data.Group;
import com.froobworld.froobpermissions.managers.GroupManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Group_InfoCommand extends CommandExecutor {
    private GroupManager groupManager;

    public Group_InfoCommand(GroupManager groupManager) {
        this.groupManager = groupManager;
    }


    @Override
    public boolean onCommandProcess(CommandSender sender, Command command, String cl, String[] args) {
        Group group = groupManager.getGroup(args[0]);

        if (group == null) {
            sender.sendMessage(ChatColor.RED + "A group by that name does not exist.");
            return false;
        }
        sender.sendMessage(ChatColor.YELLOW + "Information for group " + group.getName() + ":");
        sender.sendMessage(ChatColor.YELLOW + "Default: " + ChatColor.WHITE + group.isDefault());
        sender.sendMessage(ChatColor.YELLOW + "Inherits: " + ChatColor.WHITE +
                PageUtils.toString(group.getInheritance()));
        sender.sendMessage(ChatColor.YELLOW + "Permissions: " + ChatColor.WHITE +
                PageUtils.toString(group.getPermissionsNoInherit()));
        return true;
    }

    @Override
    public String command() {

        return "group info";
    }

    @Override
    public String perm() {

        return "froobbasics.group.info";
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
