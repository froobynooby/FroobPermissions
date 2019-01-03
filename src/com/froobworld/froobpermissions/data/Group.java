package com.froobworld.froobpermissions.data;

import com.froobworld.frooblib.utils.TimeUtils;
import com.froobworld.froobpermissions.managers.GroupManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Group {

    private GroupManager manager;
    private File file;

    private String name;
    private boolean def;
    private String chatFormat;
    private String prefix;
    private ArrayList<String> permissions;
    private ArrayList<Group> inherits;
    private String discordEquiv;

    private Group nextGroup;
    private Long timeUntilNextGroup;

    public Group(GroupManager manager, File file) {
        this.manager = manager;
        this.file = file;

        loadFromFile();
    }


    public void loadFromFile() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        name = config.getString("name");
        def = config.getBoolean("default");
        chatFormat = config.getString("chat-format");
        prefix = config.getString("prefix");
        permissions = new ArrayList<String>();
        for (String string : config.getStringList("permissions")) {
            permissions.add(string.toLowerCase());
        }
        discordEquiv = config.getString("discord-equivalent-role");

        String timeString = config.getString("auto-promote.time");
        if (timeString != null) {
            timeUntilNextGroup = TimeUtils.parseTime(timeString);
        }
    }

    public void addInheritance() {
        inherits = new ArrayList<Group>();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String string : config.getStringList("inherits")) {
            Group group = manager.getGroup(string);
            if (group != null) {
                inherits.add(group);
            }
        }

        nextGroup = manager.getGroup(config.getString("auto-promote.group"));
    }

    public void saveToFile() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("permissions", permissions);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {

        return name;
    }

    public boolean isDefault() {

        return def;
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> effectivePermissions = new ArrayList<String>();
        effectivePermissions.addAll(permissions);

        for (Group group : inherits) {
            effectivePermissions.addAll(group.getPermissions());
        }

        return effectivePermissions;
    }

    public ArrayList<String> getPermissionsNoInherit() {

        return permissions;
    }

    public ArrayList<Group> getInheritance() {

        return inherits;
    }

    public ArrayList<String> listInheritance() {
        ArrayList<String> list = new ArrayList<String>();
        for (Group group : inherits) {
            list.add(group.getName());
        }

        return list;
    }

    public String getChatFormat() {

        return ChatColor.translateAlternateColorCodes('&', chatFormat);
    }

    public String getPrefix() {

        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getDiscordRole() {

        return discordEquiv;
    }

    public void delete() {
        file.delete();
    }

    public void addPermission(String permission) {
        if (!permissions.contains(permission.toLowerCase())) {
            permissions.add(permission.toLowerCase());
            saveToFile();
        }
    }

    public void removePermission(String permission) {
        if (permissions.contains(permission.toLowerCase())) {
            permissions.remove(permission);
            saveToFile();
        }
    }

    public boolean doesInherit(Group group) {
        if (inherits.contains(group)) {
            return true;
        }

        boolean inheritInherits = false;

        for (Group in : inherits) {
            inheritInherits = inheritInherits || in.doesInherit(group);
        }

        return inheritInherits;
    }

    public Group getNextGroup() {

        return nextGroup;
    }

    public Long getTimeUntilNextGroup() {

        return timeUntilNextGroup;
    }

    @Override
    public String toString() {

        return name;
    }

}
