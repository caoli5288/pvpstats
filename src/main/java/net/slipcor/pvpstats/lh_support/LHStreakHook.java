package net.slipcor.pvpstats.lh_support;

import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.slipcor.pvpstats.PVPData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class LHStreakHook extends OnlineDataCollector {

    public LHStreakHook(Plugin plugin) {
        super("i5mc-pvp-streak", plugin.getName(), BoardType.DEFAULT, "", "i5mc-pvp-streak", Arrays.asList(null, null, "{amount}", null));
    }

    @Override
    public Double getScore(Player player) {
        return PVPData.getStreak(player).doubleValue();
    }
}
