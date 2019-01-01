package com.froobworld.froobpermissions.commands;

import com.froobworld.frooblib.command.ContainerCommandExecutor;
import com.froobworld.frooblib.utils.PageUtils;
import com.froobworld.froobpermissions.managers.GroupManager;
import com.froobworld.froobpermissions.managers.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupCommand extends ContainerCommandExecutor {

    private GroupManager groupManager;

    public GroupCommand(GroupManager groupManager, PlayerManager playerManager) {
        this.groupManager = groupManager;
        addSubCommand(new Group_AddpermCommand(groupManager, playerManager), 1, "addperm", "/group <group> addperm <permission>");
        addSubCommand(new Group_RempermCommand(groupManager, playerManager), 1, "remperm", "/group <group> remperm <permission>");
        addSubCommand(new Group_InfoCommand(groupManager), 1, "info", "/group <group> info");
    }

    @Override
    public String command() {

        return "group";
    }

    @Override
    public String perm() {

        return "froobbpermissions.group";
    }

    @Override
    public List<String> tabCompletions(CommandSender sender, Command command, String cl, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions = PageUtils.toStringList(groupManager.getGroups());
        }
        if (args.length == 2) {
            completions.add("addperm");
            completions.add("remperm");
            completions.add("info");
        }
        if (args.length > 2) {
            for (SubCommand subCommand : getSubCommands()) {
                if (args.length > subCommand.getArgIndex() + 1) {
                    if (subCommand.getArg().equalsIgnoreCase(args[subCommand.getArgIndex()])) {
                        return subCommand.getExecutor().tabCompletions(sender, command, cl, args);
                    }
                }
            }
        }

        completions = StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<String>(completions.size()));
        return completions;
    }


}