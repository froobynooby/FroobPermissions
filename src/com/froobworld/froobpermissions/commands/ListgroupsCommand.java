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

public class ListgroupsCommand extends CommandExecutor {
    private GroupManager groupManager;

    public ListgroupsCommand(GroupManager groupManager) {
        this.groupManager = groupManager;
    }


    @Override
    public boolean onCommandProcess(CommandSender sender, Command command, String cl, String[] args) {
        ArrayList<Group> list = groupManager.getGroups();

        if (list.size() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "There are no groups.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "There " + (list.size() == 1 ? "is one" : "are " + list.size()) + " groups:");
        sender.sendMessage(PageUtils.toString(list));
        return true;
    }

    @Override
    public String command() {

        return "listgroups";
    }

    @Override
    public String perm() {

        return "froobpermissions.listgroups";
    }

    @Override
    public List<String> tabCompletions(CommandSender sender, Command command, String cl, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();

        return completions;
    }
}
