package net.slipcor.pvpstats;

import net.slipcor.pvpstats.entity.PVPStat;
import org.bukkit.entity.Player;

/**
 * class for full access to player statistics
 */
public final class PVPData {

    public static Integer getDeaths(Player p) {
        return EntityMgr.pull(p).getDeaths();
    }

    public static Integer getKills(Player p) {
        return EntityMgr.pull(p).getKills();
    }

    public static Integer getMaxStreak(Player p) {
        return EntityMgr.pull(p).getStreak();
    }

    public static Integer getStreak(Player p) {
        return EntityMgr.pull(p).getCurrentstreak();
    }

    public static Integer getEloScore(Player p) {
        PVPStat stat = EntityMgr.pull(p);
        int elo = stat.getElo();
        if (elo < 1) {
            elo = $.getPlugin().getConfig().getInt("eloscore.default");
            stat.setElo(elo);
        }
        return elo;
    }

    public static void setDeaths(Player p, int value) {
        EntityMgr.pull(p).setDeaths(value);
    }

    public static void setKills(Player p, int value) {
        EntityMgr.pull(p).setKills(value);
    }

    public static void setMaxStreak(Player p, int value) {
        EntityMgr.pull(p).setStreak(value);
    }

    public static void setStreak(Player p, int value) {
        EntityMgr.pull(p).setCurrentstreak(value);
    }

    public static void setEloScore(Player p, int value) {
        EntityMgr.pull(p).setElo(value);
    }

}
