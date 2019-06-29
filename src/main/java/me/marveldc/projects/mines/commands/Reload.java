package me.marveldc.projects.mines.commands;

import me.marveldc.projects.mines.Mines;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.marveldc.projects.mines.Util.tl;

public class Reload implements CommandExecutor {

    public Reload(Mines plugin) {
        plugin.getCommand("mines-reload").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Mines.getPlugin().reloadMsg();
        sender.sendMessage(tl(true, "&cReloaded messages."));
        return true;
    }
}
