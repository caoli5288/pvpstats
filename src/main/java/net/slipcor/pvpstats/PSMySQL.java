package net.slipcor.pvpstats;

import net.slipcor.pvpstats.entity.PVPStat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * MySQL access class
 *
 * @author slipcor
 */

public final class PSMySQL {

    private PSMySQL() {
    }

    private static boolean incKill(final Player p) {
        if (p.hasPermission("pvpstats.count")) {
            PVPStat stat = EntityMgr.pull(p);
            stat.setKills(stat.getKills() + 1);
            int nstreak = stat.getCurrentstreak() + 1;
            stat.setCurrentstreak(nstreak);
            if (nstreak > stat.getStreak()) {
                stat.setStreak(nstreak);
            }
            return true;
        }
        return false;
    }

    private static boolean incDeath(final Player p) {
        if (p.hasPermission("pvpstats.count")) {
            PVPData.setStreak(p, 0);
            PVPData.setDeaths(p, PVPData.getDeaths(p));
            return true;
        }
        return false;
    }

    public static void AkilledB(Player attacker, Player player) {
        if (attacker == null && player == null) {
            return;
        }

        if (player == null) {
            incKill(attacker);
            EntityMgr.save(attacker, true);
            return;
        }

        if (attacker == null) {
            incDeath(player);
            EntityMgr.save(player, false);
            return;
        }

        if (attacker.hasPermission("pvpstats.newbie") || player.hasPermission("pvpstats.newbie")) {
            return;
        }

        ConfigurationSection sec = $.getPlugin().getConfig().getConfigurationSection("eloscore");

        if (!sec.getBoolean("active")) {
            incKill(attacker);
            incDeath(player);
        } else {
            final int min = sec.getInt("minimum", 18);
            final int max = sec.getInt("maximum", 3000);
            final int kBelow = sec.getInt("k-factor.below", 32);
            final int kAbove = sec.getInt("k-factor.above", 16);
            final int kThreshold = sec.getInt("k-factor.threshold", 2000);

            final int oldA = PVPData.getEloScore(attacker);
            final int oldP = PVPData.getEloScore(player);

            final int kA = oldA >= kThreshold ? kAbove : kBelow;
            final int kP = oldP >= kThreshold ? kAbove : kBelow;

            final int newA = calcElo(oldA, oldP, kA, true, min, max);
            final int newP = calcElo(oldP, oldA, kP, false, min, max);

            if (incKill(attacker)) {
                $.getPlugin().sendPrefixed(attacker, Language.MSG_ELO_ADDED.toString(String.valueOf(newA - oldA), String.valueOf(newA)));
                PVPData.setEloScore(attacker, newA);
            }
            if (incDeath(player)) {
                $.getPlugin().sendPrefixed(player, Language.MSG_ELO_SUBBED.toString(String.valueOf(oldP - newP), String.valueOf(newP)));
                PVPData.setEloScore(player, newP);
            }
        }

        EntityMgr.kill(attacker, player);
    }

    private static int calcElo(int myOld, int otherOld, int k, boolean win, int min, int max) {
        double expected = 1.0f / (1.0f + Math.pow(10.0f, ((float) (otherOld - myOld)) / 400.0f));

        int newVal;
        if (win) {
            newVal = (int) Math.round(myOld + k * (1.0f - expected));
        } else {
            newVal = (int) Math.round(myOld + k * (0.0f - expected));
        }

        if (min > -1 && newVal < min) {
            return min;
        }

        if (max > -1 && newVal > max) {
            return max;
        }

        return newVal;
    }

}
