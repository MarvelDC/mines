package me.marveldc.projects.mines;

import me.marveldc.projects.mines.commands.Create;
import me.marveldc.projects.mines.commands.Reload;
import me.marveldc.projects.mines.listeners.Wand;
import me.marveldc.projects.mines.objects.MineData;
import me.marveldc.projects.mines.objects.MineRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.marveldc.projects.mines.Util.*;
import static org.bukkit.Bukkit.getPluginManager;

public class Mines extends JavaPlugin {

    static String prefix;

    public static ItemStack wand;
    public static HashMap<UUID, Points<Location, Location>> selections = new HashMap<>();
    public static ArrayList<MineData> mineQueue;

    private static Mines instance;
    private FileConfiguration messages;
    private File messagesFile;

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getScheduler().cancelAllTasks();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        mineQueue = getMines();

        createMessages();
        addDefaults();
        prefix = tl(msg().getString("prefix"));

        // register commands
        new Create(this);
        new Reload(this);

        // register events
        PluginManager pm = getPluginManager();
        pm.registerEvents(new Wand(), this);

        // wand ItemStack creation
        wand = new ItemStack(Material.IRON_AXE, 1);
        ItemMeta wandMeta = wand.getItemMeta();
        wandMeta.setDisplayName(tl("Mine region selector"));
        wandMeta.setUnbreakable(true);
        List<String> lore = new ArrayList<>();
        lore.add(tl("&7Use this axe to select &f&ltwo points &7for the mine region."));
        lore.add(tl("&7Once you have selected a region, do &f&l/mine-create&7."));
        wandMeta.setLore(lore);
        wand.setItemMeta(wandMeta);

        new MineRunnable();
    }

    public static Mines getPlugin() {
        return instance;
    }

    private FileConfiguration msg() {
        return this.messages;
    }

    public void reloadMsg() {
        addDefaults();
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefix = tl(msg().getString("prefix"));
    }

    private void addDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("prefix", "&7&lMines &f&l>>");

        setDefaultValues(messages, defaults);
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        if (!messagesFile.exists()) {
            if (!messagesFile.getParentFile().mkdir())
                System.out.println("[Mines] Error in creating 'messages.yml' directories.");
            //saveResource("messages.yml", false);
            try {
                if (!messagesFile.createNewFile())
                    System.out.println("[Mines] Failed in creating 'messages.yml' file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        try {
            messages.load(messagesFile);
            addDefaults();
            messages.save(messagesFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            System.out.println("[Mines] Error loading 'messages.yml' file.");
        }
    }
}
