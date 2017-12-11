package net.slipcor.pvpstats;

import net.slipcor.pvparena.events.PADeathEvent;
import net.slipcor.pvparena.events.PAKillEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * PVP Arena listener class
 * <p/>
 * Checks the PVP Arena events to possibly ignore kills happening there
 */

public class PSPAListener implements Listener {

    private final $ plugin;

    public PSPAListener(final $ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArenaKill(final PAKillEvent event) {
        if (plugin.ignoresWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        PSMySQL.AkilledB(event.getPlayer(), null);
    }

    @EventHandler
    public void onArenaDeath(final PADeathEvent event) {
        if (plugin.ignoresWorld(event.getPlayer().getWorld().getName()) ||
                !event.isPVP()) {
            return;
        }
        PSMySQL.AkilledB(null, event.getPlayer());
    }
}