package net.slipcor.pvpstats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener class
 *
 * @author slipcor
 */

public class PSListener implements Listener {

    private final $ plugin;
    private final Map<String, String> lastKill = new HashMap<>();
    private final Map<String, BukkitTask> killTask = new HashMap<>();

    public PSListener(final $ instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void handle(final PlayerJoinEvent event) {
        EntityMgr.join(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (plugin.getConfig().getBoolean("resetkillstreakonquit")) {
            PVPData.setStreak(event.getPlayer(), 0);
        }
        EntityMgr.quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity() == null || plugin.ignoresWorld(event.getEntity().getWorld().getName())) {
            return;
        }

        if (event.getEntity().getKiller() == null) {
            if (plugin.getConfig().getBoolean("countregulardeaths")) {
                PSMySQL.AkilledB(null, event.getEntity());
            }
            return;
        }

        final Player attacker = event.getEntity().getKiller();
        final Player player = event.getEntity();

        if (plugin.getConfig().getBoolean("checkabuse")) {
            if (lastKill.containsKey(attacker.getName()) && lastKill.get(attacker.getName()).equals(player.getName())) {
                return; // no logging!
            }

            lastKill.put(attacker.getName(), player.getName());
            int abusesec = plugin.getConfig().getInt("abuseseconds");
            if (abusesec > 0) {
                BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    lastKill.remove(attacker.getName());
                    killTask.remove(attacker.getName());
                }, abusesec * 20L);

                if (killTask.containsKey(attacker.getName())) {
                    killTask.get(attacker.getName()).cancel();
                }

                killTask.put(attacker.getName(), task);
            }
        }
        PSMySQL.AkilledB(attacker, player);
    }

}
