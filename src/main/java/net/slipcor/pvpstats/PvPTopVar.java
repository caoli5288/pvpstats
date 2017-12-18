package net.slipcor.pvpstats;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import lombok.val;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import net.slipcor.pvpstats.entity.PVPStat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PvPTopVar extends EZPlaceholderHook {

    private final ScriptEngine engine;
    private final Cache<String, Object> pool = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public PvPTopVar(Plugin plugin) {
        super(plugin, "pvptop");
        engine = new ScriptEngineManager().getEngineByExtension("js");
    }

    @SneakyThrows
    public String onPlaceholderRequest(Player player, String input) {
        // "pvptop_<order_by>_<order>_<desc>"
        val command = input.toLowerCase();
        return (String) pool.get(command, () -> command(command));
    }

    @SneakyThrows
    private String command(String command) {
        val itr = Arrays.asList(command.split("_")).iterator();
        String by = itr.next();
        List<PVPStat> list = (List) pool.get("list_by:" + by, () -> $.getDataSource().find(PVPStat.class).orderBy(by + " desc").setMaxRows($.getPlugin().getConfig().getInt("top_limit", 20)).findList());
        PVPStat stat = list.get(Integer.parseInt(itr.next()) - 1);
        engine.put("input", stat);
        return String.valueOf(engine.eval("input." + itr.next()));
    }
}
