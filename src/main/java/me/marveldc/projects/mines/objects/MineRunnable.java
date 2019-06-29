package me.marveldc.projects.mines.objects;

import me.marveldc.projects.mines.Mines;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.rmi.server.UID;
import java.util.LinkedHashMap;
import java.util.UUID;

import static me.marveldc.projects.mines.Mines.mineQueue;
import static me.marveldc.projects.mines.Util.setCube;
import static org.bukkit.Bukkit.getOnlinePlayers;
import static org.bukkit.Bukkit.getWorld;

public class MineRunnable {

    public MineRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!getOnlinePlayers().isEmpty())
                    for (final MineData mine : mineQueue) {
                        World world = getWorld(UUID.fromString(mine.getWorld()));
                        if (world != null) {
                            String[] pointSplit = mine.getPoint1().split(":");
                            final Location point1 = new Location(world,
                                    Integer.parseInt(pointSplit[0]),
                                    Integer.parseInt(pointSplit[1]),
                                    Integer.parseInt(pointSplit[2])
                            );
                            pointSplit = mine.getPoint2().split(":");
                            final Location point2 = new Location(world,
                                    Integer.parseInt(pointSplit[0]),
                                    Integer.parseInt(pointSplit[1]),
                                    Integer.parseInt(pointSplit[2])
                            );
                            setCube(point1, point2, mine.getBlocks());
                        }

                    }
            }
        }.runTaskTimer(Mines.getPlugin(), 200L, 200L); // wait 10 seconds on server start, then wait 10 seconds per cycle
    }
}
