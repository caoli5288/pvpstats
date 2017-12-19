package net.slipcor.pvpstats;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by Yaël on 27/02/2016.
 */
public class PAPIHook extends PlaceholderHook {

    public PAPIHook(Plugin plugin) {
    }

    public boolean hook() {
        PlaceholderAPI.registerPlaceholderHook("pvp", this);
        return PlaceholderAPI.registerPlaceholderHook("slipcorpvpstats", this);
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

        if (s.equals("score")) {
            return String.valueOf(PVPData.getScore(player));
        }

        return null;
    }
}
