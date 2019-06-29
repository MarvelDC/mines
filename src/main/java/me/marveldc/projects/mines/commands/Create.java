package me.marveldc.projects.mines.commands;

import me.marveldc.projects.mines.Mines;
import me.marveldc.projects.mines.Util;
import me.marveldc.projects.mines.objects.MineData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.LinkedHashMap;

import static me.marveldc.projects.mines.Mines.*;
import static me.marveldc.projects.mines.Util.*;

public class Create implements CommandExecutor {

    public Create(Mines plugin) {
        plugin.getCommand("mines-create").setExecutor(this);
    }

    // mines-create <name> <blocks> <reset duration>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        PlayerInventory inv = player.getInventory();

        if (selections.containsKey(player.getUniqueId())) {
            if (!selections.get(player.getUniqueId()).isMissing()) {
                if (!inv.contains(wand))
                    inv.setItem(inv.firstEmpty(), wand);
                // create mine logic
                if (args.length <= 2) {
                    sender.sendMessage(tl(true, "&cSyntax: &7/mines-create <name> <blocks> <reset duration>"));
                    return true;
                }
                if (args[0].length() >= 152) {
                    sender.sendMessage(tl(true, "&bName is larger than &f152 characters&7.\n&cSyntax: &7/mines-create <name> <blocks> <reset duration>"));
                    return true;
                }
                String blocks = blockParse(args[1]);
                if (blocks == null) {
                    sender.sendMessage(tl(true, "&bBlocks possible errors: &7is empty&f, &7invalid solid blocks&f, &7contains negative values&f.\n&cSyntax: &7/mines-create <name> <blocks> <reset duration>"));
                    return true;
                }
                int duration = timeParse(args[2]);
                if (duration <= -1) {
                    sender.sendMessage(tl(true, "&bReset duration possible errors: &7duration is empty&f, &7contains negative values&f, &7does not contain correct time units (s, m, h)&f, &7is too long&f.\n&cSyntax: &7/mines-create <name> <blocks> <reset duration>"));
                    return true;
                }
                if (checkMineDuplicate(args[0])) {
                    sender.sendMessage(tl(true, "&4Error: &fMine '{0}' is already defined. Please choose another name.", new String[]{args[0]}));
                    return true;
                }
                sender.sendMessage(tl(true, "&cCreating mine..."));
                Location point1 = selections.get(player.getUniqueId()).getRight();
                Location point2 = selections.get(player.getUniqueId()).getLeft();

                String response = setCube(point1, point2, blocks);
                if (response.contains("&4Error")) {
                    sender.sendMessage(tl(true, response));
                    return true;
                }
                sender.sendMessage(tl(true, response));
                String point1String = point1.getBlockX() + ":" + point1.getBlockY() + ":" + point1.getBlockZ();
                String point2String = point2.getBlockX() + ":" + point2.getBlockY() + ":" + point2.getBlockZ();

                mineQueue.add(new MineData(args[0], duration, point1.getWorld().getUID().toString(), blocks, point1String, point2String));
                return true;
            }
        }
        if (!inv.contains(wand))
            inv.setItem(inv.firstEmpty(), wand);
        sender.sendMessage(tl(true, "&7Use the &f&lIron Axe &7to select &f&ltwo points &7for the mine region. Use command &f&l/mine-create &7to confirm selection."));
        return true;
    }
}
