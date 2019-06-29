package me.marveldc.projects.mines;

import me.marveldc.projects.mines.objects.MineData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Util {

    public static String tl(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String tl(Boolean prefix, String message) {
        if (prefix)
            message = Mines.prefix + " " + message;
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String tl(Boolean prefix, String message, String[] variables) {
        if (prefix)
            message = Mines.prefix + " " + message;
        if (variables.length == 1)
            message = message.replace("{0}", variables[0]);
        else
            for (int i = 0; i < variables.length-1; i++)
                message = message.replace("{" + i + "}", variables[i]);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String tl(Boolean prefix, String message, int[] variables) {
        if (prefix)
            message = Mines.prefix + " " + message;
        for (int i = 0; i < variables.length-1; i++) {
            message = message.replace("{" + i + "}", String.valueOf(variables[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String tl(Boolean prefix, String message, double[] variables) {
        if (prefix)
            message = Mines.prefix + " " + message;
        for (int i = 0; i <= variables.length-1; i++) {
            message = message.replace("{" + i + "}", String.valueOf(variables[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void setDefaultValues(FileConfiguration config, Map<String, Object> parameters) {
        if (config == null) return;
        for (final Map.Entry<String, Object> e : parameters.entrySet())
            if (!config.contains(e.getKey()))
                config.set(e.getKey(), e.getValue());
    }

    public static String setCube(Location first, Location second, String blocks) {
        final World world = first.getWorld().getUID() == second.getWorld().getUID() ? first.getWorld() : null;
        if (world == null) return "&4Error: &fSelected points are in different worlds.";

        int x1 = Math.min(first.getBlockX(), second.getBlockX());
        int y1 = Math.min(first.getBlockY(), second.getBlockY());
        int z1 = Math.min(first.getBlockZ(), second.getBlockZ());

        int x2 = Math.max(first.getBlockX(), second.getBlockX());
        int y2 = Math.max(first.getBlockY(), second.getBlockY());
        int z2 = Math.max(first.getBlockZ(), second.getBlockZ());

        double weightTotal = 0.0;
        LinkedHashMap<Material, Double> materials = new LinkedHashMap<>();
        String[] splitted = blocks.split(":"); // material:weighting:material:weighting -> ['material', 'weighting', 'material', 'material']
        String lastMaterial = "";

        for (int i = 0; i <= splitted.length-1; i++) {
            if (i % 2 == 0) { // material
                materials.put(Material.getMaterial(splitted[i]), null);
                lastMaterial = splitted[i];
            } else { // weighting
                weightTotal += Double.parseDouble(splitted[i]);
                materials.replace(Material.getMaterial(lastMaterial), null, Double.parseDouble(splitted[i]));
            }
        }

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {

                    int randomIndex = -1;
                    double randomDouble = Math.random() * weightTotal;
                    for (int i = 0; i < materials.size(); ++i) {
                        randomDouble -= (Double) materials.values().toArray()[i];
                        if (randomDouble <= 0.0) {
                            randomIndex = i;
                            break;
                        }
                    }
                    new Location(world, x, y, z).getBlock().setType(Material.matchMaterial(String.valueOf(materials.keySet().toArray()[randomIndex])));
                }
            }
        }

        return "&fCreated mine &asuccessfully&f!";
    }

    public static int timeParse(String input) {
        input = input.toLowerCase();
        if (input.isEmpty()) return -1;
        if (input.contains("-")) return -1;

        char[] time = input.trim().toCharArray();
        int duration = 0;
        StringBuilder numbers = new StringBuilder();

        for (char c : time) {
            if (Character.isDigit(c)) numbers.append(c);
            else {
                if (!numbers.toString().isEmpty()) {
                    if (c == 's') duration += Integer.valueOf(numbers.toString()) * 1000; // second
                    else if (c == 'm') duration += Integer.valueOf(numbers.toString()) * 60000; // minute
                    else if (c == 'h') duration += Integer.valueOf(numbers.toString()) * 3600000; // hour
                    else return -1;
                    numbers.setLength(0);
                }
            }
        }

        if (duration <= 0) return -1;
        if (duration >= 864000001) return -1;
        return duration;
    }

    public static String blockParse(String input) {
        input = input.trim();
        if (input.isEmpty()) return null;
        if (input.contains("-")) return null;

        String blocks = "";

        if (!input.contains("%")) {
            String[] strings = input.split(",");
            for (String str : strings) {
                Material material = Material.matchMaterial(str);
                blocks = getString(blocks, material, 100.0 / strings.length);
            }
        } else {
            String[] strings = input.split(",");
            for (String str : strings) {
                if (str.contains("%")) {
                   String[] entries = str.split("%");
                   try {
                       double parsedWeighting = Double.parseDouble(entries[0]);
                       Material material = Material.matchMaterial(entries[1]);
                       blocks = getString(blocks, material, parsedWeighting);
                   } catch (NumberFormatException ignored) {}
                }
            }
        }
        if (blocks.contains(":"))
            blocks = blocks.substring(0, blocks.length()-1);
        return blocks.isEmpty() ? null : blocks;
    }

    private static String getString(String blocks, Material material, double v) {
        if (material != null)
            if (material.isSolid())
                if (material.isItem()) {
                    try {
                        Double roundedDouble = Double.valueOf(BigDecimal.valueOf(v).setScale(1, RoundingMode.CEILING).toString());
                        blocks = blocks + material.name() + ":" + roundedDouble + ":";
                    } catch (NumberFormatException ignored) {}
                }
        return blocks;
    }

    static ArrayList<MineData> getMines() {
        // source: https://stackoverflow.com/a/19190625/11427881
        // source: https://stackoverflow.com/a/4917347/11427881

        ArrayList<MineData> list = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        String[] keys = {"name", "duration", "world", "blocks", "point1", "point2"};
        boolean success;

        File directory = new File(Mines.getPlugin().getDataFolder(), "mines");
        File[] files = directory.listFiles();
        if (files != null)
            for (File file : files)
                if (file.getName().contains(".yml")) {
                    success = false;
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    if (names.contains(file.getName()))
                        System.out.println("[Mines] '" + file.getName() + "' mine configuration file is already defined.");
                    else {
                        for (String key : keys) {
                            if (!config.contains(key)) {
                                System.out.println("[Mines] '" + file.getName() + "' mine configuration file is missing the key & value of: '" + key + "'.");
                                success = false;
                            } else if (config.get(key) == null) {
                                System.out.println("[Mines] '" + file.getName() + "' mine configuration file value of key: '" + key + "' is not defined.");
                                success = false;
                            } else success = true;
                        }
                        if (success) {
                            list.add(new MineData(
                                    config.getString(keys[0]),
                                    config.getInt(keys[1]),
                                    config.getString(keys[2]),
                                    config.getString(keys[3]),
                                    config.getString(keys[4]),
                                    config.getString(keys[5])
                            ));
                            names.add(file.getName());
                            System.out.println("[Mines] Loaded mine '" + config.getString("name") + "' successfully.");
                        } else System.out.println("[Mines] '" + file.getName() + "' was not loaded.");
                    }
                }
        return list;
    }

    public static boolean checkMineDuplicate(String input) {
        File directory = new File(Mines.getPlugin().getDataFolder(), "mines");
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(input + ".yml")) {
                    return true;
                }
            }
        }
        return false;
    }
}
