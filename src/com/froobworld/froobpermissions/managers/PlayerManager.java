package com.froobworld.froobpermissions.managers;

import com.froobworld.frooblib.data.Manager;
import com.froobworld.frooblib.data.Storage;
import com.froobworld.frooblib.utils.PageUtils;
import com.froobworld.frooblib.uuid.UUIDManager;
import com.froobworld.frooblib.uuid.UUIDManager.UUIDData;
import com.froobworld.froobpermissions.FroobPermissions;
import com.froobworld.froobpermissions.data.Group;
import com.froobworld.froobpermissions.data.Playerdata;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager extends Manager {
    private Storage storage;

    private GroupManager groupManager;

    private HashMap<Player, PermissionAttachment> attachments;
    private HashMap<UUID, Playerdata> playerdata;

    public PlayerManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }


    @Override
    public void ini() {
        attachments = new HashMap<Player, PermissionAttachment>();
        playerdata = new HashMap<UUID, Playerdata>();

        storage = new Storage(FroobPermissions.getPlugin().getDataFolder().getPath() + "/playerdata");
    }

    public boolean loadPlayerdata(UUID uuid) {
        File file = storage.getFile(uuid + ".yml");
        if (!file.exists()) {
            return false;
        }

        playerdata.put(uuid, new Playerdata(file, groupManager, uuid));
        return true;
    }

    public void loadPlayerdata(Player player) {
        File file = storage.getFile(player.getUniqueId() + ".yml");
        if (!file.exists()) {
            createNewPlayerdata(player, file);
        }

        playerdata.put(player.getUniqueId(), new Playerdata(file, groupManager, player.getUniqueId()));
    }

    public void addAttachment(Player player) {
        PermissionAttachment attachment = player.addAttachment(FroobPermissions.getPlugin());
        attachments.put(player, attachment);
        Group group = getPlayerdata(player).getGroup();
        if (group != null) {
            for (String permission : group.getPermissions()) {
                attachment.setPermission(permission, true);
            }
        }
    }

    public void removeAttachment(Player player) {
        if (attachments.containsKey(player)) {
            PermissionAttachment attachment = attachments.get(player);
            player.removeAttachment(attachment);
            attachments.remove(player);
        }
    }

    public void removeAllAttachments() {
        Set<Player> copyKeySet = attachments.keySet().stream().collect(Collectors.toSet());
        copyKeySet.forEach(p -> removeAttachment(p));
    }

    public void updatePlayer(Player player) {
        removeAttachment(player);
        addAttachment(player);
    }

    public void updateAllPlayers() {
        removeAllAttachments();
        Bukkit.getOnlinePlayers().forEach(p -> addAttachment(p));
    }

    public void createNewPlayerdata(Player player, File file) {
        YamlConfiguration config = new YamlConfiguration();
        if (groupManager.getDefaultGroup() != null) {
            config.set("group", groupManager.getDefaultGroup().getName());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Playerdata getPlayerdata(Player player) {
        if (!playerdata.containsKey(player.getUniqueId())) {
            loadPlayerdata(player);
        }

        return playerdata.get(player.getUniqueId());
    }

    public Playerdata getPlayerdata(UUID uuid) {
        if (!playerdata.containsKey(uuid)) {
            if (!loadPlayerdata(uuid)) {
                return null;
            }
        }

        return playerdata.get(uuid);
    }

    public Playerdata commandSearchForPlayer(String name, CommandSender sender, UUIDManager uuidManager) {
        Playerdata data = null;

        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            data = getPlayerdata(player);
        } else {
            ArrayList<UUIDData> uuids = uuidManager.getUUIDData(name);
            if (uuids.size() > 1) {
                sender.sendMessage(ChatColor.RED + "There are multiple people who last played with that name:");
                sender.sendMessage(PageUtils.toString(uuids));
                sender.sendMessage(ChatColor.RED + "Please use their UUID in place of their name.");
                return null;
            }
            if (uuids.size() == 1) {
                data = getPlayerdata(uuids.get(0).getUUID());
            }
        }
        if (data == null) {
            UUID uuid = null;
            try {
                uuid = UUID.fromString(name);
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(ChatColor.RED + "A player by that name could not be found.");
                return null;
            }
            if (uuid != null) {
                data = getPlayerdata(uuid);
            } else {
                sender.sendMessage(ChatColor.RED + "A player with that UUID could not be found.");
                return null;
            }
        }
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "A player by that name could not be found.");
            return null;
        }

        return data;
    }

}
