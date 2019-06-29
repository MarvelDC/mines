package me.marveldc.projects.mines.listeners;

import me.marveldc.projects.mines.Points;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static me.marveldc.projects.mines.Mines.selections;
import static me.marveldc.projects.mines.Mines.wand;
import static me.marveldc.projects.mines.Util.tl;

public class Wand implements Listener {

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
            if (!event.getHand().equals(EquipmentSlot.HAND)) return;
            if (player.getInventory().getItemInMainHand().hashCode() != wand.hashCode()) return;

            Location location = event.getClickedBlock().getLocation();
            event.setCancelled(true);
            if (selections.containsKey(player.getUniqueId())) {
                if (selections.get(player.getUniqueId()) == null) {
                    addPoints(action, player, location);
                } else {
                    Points pair = selections.get(player.getUniqueId());
                    if (action == Action.RIGHT_CLICK_BLOCK) {
                        pair.setRight(location);
                        player.sendMessage(tl(true, "&7First &bposition set (&7{0}&b, &7{1}&b, &7{2}&b)", new double[]{location.getX(), location.getY(), location.getZ()}));
                    } else {
                        pair.setLeft(location);
                        player.sendMessage(tl(true, "&7Second &bposition set (&7{0}&b, &7{1}&b, &7{2}&b)", new double[]{location.getX(), location.getY(), location.getZ()}));
                    }
                    selections.put(player.getUniqueId(), pair);
                }
            } else {
                addPoints(action, player, location);
            }
        }
    }

    private void addPoints(Action action, Player player, Location location) {
        if (action == Action.RIGHT_CLICK_BLOCK) {
            selections.put(player.getUniqueId(), new Points<>(null, location));
            player.sendMessage(tl(true, "&7First &bposition set (&7{0}&b, &7{1}&b, &7{2}&b)", new double[]{location.getX(), location.getY(), location.getZ()}));
        } else {
            selections.put(player.getUniqueId(), new Points<>(location, null));
            player.sendMessage(tl(true, "&7Second &bposition set (&7{0}&b, &7{1}&b, &7{2}&b)", new double[]{location.getX(), location.getY(), location.getZ()}));
        }
    }
}
