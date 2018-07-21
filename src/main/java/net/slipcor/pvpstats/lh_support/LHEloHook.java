package net.slipcor.pvpstats.lh_support;

import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.slipcor.pvpstats.PVPData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class LHEloHook extends OnlineDataCollector {

    public LHEloHook(Plugin plugin) {
        super("i5mc-pvp-elo", plugin.getName(), BoardType.DEFAULT, "i5mc-pvp-elo", "", Arrays.asList(null, null, "{amount}"));
    }

    @Override
    public Double getScore(Player player) {
        return PVPData.getEloScore(player).doubleValue();
    }
}
