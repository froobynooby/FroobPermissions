package com.froobworld.froobpermissions.data;

import com.froobworld.froobpermissions.managers.GroupManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Playerdata {
    private UUID uuid;

    private File file;
    private Group group;


    public Playerdata(File file, GroupManager groupManager, UUID uuid) {
        this.file = file;
        this.uuid = uuid;
        loadFromFile(groupManager);
    }


    public void loadFromFile(GroupManager groupManager) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        group = groupManager.getGroup(config.getString("group"));
    }

    public void saveToFile() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("group", group.getName());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID() {

        return uuid;
    }

    public Group getGroup() {

        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        saveToFile();
    }

}
