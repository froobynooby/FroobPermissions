package com.froobworld.froobpermissions.managers;

import com.froobworld.frooblib.data.Manager;
import com.froobworld.frooblib.data.Storage;
import com.froobworld.froobpermissions.FroobPermissions;
import com.froobworld.froobpermissions.data.Group;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GroupManager extends Manager {
    private Storage storage;

    private ArrayList<Group> groups;

    @Override
    public void ini() {
        storage = new Storage(FroobPermissions.getPlugin().getDataFolder().getPath() + "/groups");
        loadGroups();
    }

    public void loadGroups() {
        groups = new ArrayList<Group>();

        for (File file : storage.listFiles()) {
            groups.add(new Group(this, file));
        }
        for (Group group : groups) {
            group.addInheritance();
        }
    }

    public Group getGroup(String name) {
        for (Group group : groups) {
            if (group.getName().equalsIgnoreCase(name)) {

                return group;
            }
        }

        return null;
    }

    public Group getDefaultGroup() {
        for (Group group : groups) {
            if (group.isDefault()) {

                return group;
            }
        }

        return null;
    }

    public void deleteGroup(Group group) {
        groups.remove(group);
        group.delete();

        loadGroups();
    }

    public boolean createGroup(String name, boolean def) {
        if (getGroup(name) != null) {
            return false;
        }

        YamlConfiguration config = new YamlConfiguration();
        File file = storage.getFile(name + ".yml");

        config.set("name", name);
        config.set("default", def);
        config.set("chat-format", "<%s> %s");
        config.set("permissions", new ArrayList<String>());
        config.set("inherits", new ArrayList<String>());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
            return false;
        }

        loadGroups();
        return true;
    }

    public ArrayList<Group> getGroups() {

        return groups;
    }

}
