package net.slipcor.pvpstats;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by Yaël on 27/02/2016.
 */
public class PAPIHook extends EZPlaceholderHook {

    public PAPIHook(Plugin plugin) {
        super(plugin, "slipcorpvpstats");
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {

        if (s.equals("kills")) {
            return String.valueOf(PVPData.getKills(player));
        }

        if (s.equals("deaths")) {
            return String.valueOf(PVPData.getDeaths(player));
        }

        if (s.equals("streak")) {
            return String.valueOf(PVPData.getStreak(player));
        }

        if (s.equals("maxstreak")) {
            return String.valueOf(PVPData.getMaxStreak(player));
        }

        if (s.equals("elo")) {
            return String.valueOf(PVPData.getEloScore(player));
        }

        return null;
    }
}
