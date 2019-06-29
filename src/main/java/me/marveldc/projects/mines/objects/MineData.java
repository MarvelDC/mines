package me.marveldc.projects.mines.objects;

import me.marveldc.projects.mines.Mines;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MineData {

    private String mine;
    private int duration;
    private String world;
    private String blocks;
    private String point1;
    private String point2;

    private FileConfiguration config;
    private File file;

    public MineData(String mine, int duration, String world, String blocks, String point1, String point2) {
        this.mine = mine;
        this.duration = duration;
        this.world = world;
        this.blocks = blocks;
        this.point1 = point1;
        this.point2 = point2;

        this.file = new File(Mines.getPlugin().getDataFolder(), "mines" + File.separator + this.mine + ".yml");
        if (!this.file.getParentFile().exists())
            if (!this.file.getParentFile().mkdir())
                System.out.println("[Mines] Error in creating 'mines' folder.");
        if (!this.file.exists()) {
            try {
                if (!this.file.createNewFile())
                    System.out.println("[Mines] Error in creating 'mines/" + this.mine + ".yml' file.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[Mines] Error in creating 'mines/" + this.mine + ".yml' file.");
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        addDefaults();
        save();
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public String getMine() {
        return this.mine;
    }

    public int getDuration() {
        return this.duration;
    }

    public String getWorld() {
        return this.world;
    }

    public String getBlocks() {
        return this.blocks;
    }

    public String getPoint1() {
        return this.point1;
    }

    public String getPoint2() {
        return this.point2;
    }

    private void addDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("name", this.mine);
        defaults.put("duration", this.duration);
        defaults.put("world", this.world);
        defaults.put("blocks", this.blocks);
        defaults.put("point1", this.point1);
        defaults.put("point2", this.point2);

        setDefaultValues(this.config, defaults);
    }

    private void setDefaultValues(FileConfiguration config, Map<String, Object> parameters) {
        if (config == null) return;
        for (final Map.Entry<String, Object> e : parameters.entrySet())
            config.set(e.getKey(), e.getValue());
    }

    private void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
