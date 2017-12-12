package net.slipcor.pvpstats.lh_support;

import com.google.common.collect.Maps;
import lombok.val;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.slipcor.pvpstats.PVPData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LHEloHook extends DataCollector {

    public LHEloHook(Plugin plugin) {
        super("i5mc-pvp-elo", plugin.getName(), BoardType.DEFAULT, "", "i5mc-pvp-elo", Arrays.asList(null, null, "{amount}", null), true, UUID.class);
    }

    @Override
    public List<Map.Entry<?, Double>> requestAll() {
        val out = Maps.<Object, Double>newHashMap();
        val itr = Bukkit.getOnlinePlayers().iterator();
        Player p;
        while (itr.hasNext()) {
            p = itr.next();
            out.put(p.getUniqueId(), PVPData.getEloScore(p).doubleValue());
        }
        return LeaderHeadsAPI.sortMap(out);
    }
}
