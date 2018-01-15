package net.slipcor.pvpstats;

import me.clip.placeholderapi.PlaceholderAPI;
import net.slipcor.pvpstats.entity.PVPStat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.util.NumberConversions.toInt;

public enum ExchangeMgr {

    INSTANCE;

    private final Map<String, Exchange> all = new HashMap<>();

    public static void init(List<Map<?, ?>> input) {
        input.forEach(mapping -> {
            Exchange exchange = null;
            try {
                exchange = new Exchange(mapping);
            } catch (Exception e) {
//                ;
            }
            if (!$.nil(exchange)) INSTANCE.all.put(exchange.id, exchange);
        });
    }

    public static void exchange(Player p, String id) {
        Exchange exchange = INSTANCE.all.get(id);
        $.thr(exchange == null, "兑换不存在");

        if (!(exchange.permission == null) && !exchange.permission.isEmpty() && !p.hasPermission(exchange.permission)) {
            return;
        }

        PVPStat pvp = EntityMgr.pull(p);
        if (pvp.getScore() < exchange.price) {// 无事发生
            return;
        }

        pvp.setScore(pvp.getScore() - exchange.price);
        EntityMgr.save(p);

        String command = PlaceholderAPI.setPlaceholders(p, exchange.command);
        String[] split = command.split("\n");
        if (split.length == 1) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            Arrays.stream(split).filter(line -> !line.isEmpty()).forEach(line -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line));
        }
    }

    private static class Exchange {

        private String id;
        private String command;
        private String permission;
        private int price;

        public Exchange(Map<?, ?> input) {
            id = input.get("id").toString();
            command = input.get("command").toString();
            permission = input.containsKey("permission") ? input.get("permission").toString() : null;
            price = toInt(input.get("price"));
        }
    }
}
