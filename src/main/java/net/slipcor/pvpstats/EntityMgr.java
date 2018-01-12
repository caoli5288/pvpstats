package net.slipcor.pvpstats;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mengcraft.simpleorm.EbeanHandler;
import lombok.SneakyThrows;
import net.slipcor.pvpstats.entity.PVPKill;
import net.slipcor.pvpstats.entity.PVPStat;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public enum EntityMgr {

    INSTANCE;

    private final Cache<UUID, Future<PVPStat>> handled = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    public static void quit(Player p) {
        INSTANCE.handled.invalidate(p.getUniqueId());
    }

    @SneakyThrows
    public static Future<PVPStat> join(Player p) {
        return INSTANCE.handled.get(p.getUniqueId(), () -> CompletableFuture.supplyAsync(() -> {
            EbeanHandler pool = $.getDataSource();
            PVPStat stat = pool.find(PVPStat.class).where("uid = :id").setParameter("id", p.getUniqueId()).findUnique();
            if (stat == null) {
                stat = pool.bean(PVPStat.class);
                stat.setUid(p.getUniqueId());
                stat.setName(p.getName());
            }
            return stat;
        }, $.getExecutor()));
    }

    public static void save(Player p, boolean kill) {
        PVPKill k = new PVPKill();
        k.setUid(p.getUniqueId());
        k.setName(p.getName());
        k.setKill(kill ? 1 : 0);
        long second = Instant.now().getEpochSecond();
        k.setTime(second);
        PVPStat pull = pull(p);
        pull.setTime(second);
        $.execute(() -> $.getDataSource().save(Arrays.asList(pull, k)));
    }

    public static void save(Player p) {
        $.getDataSource().save(pull(p));
    }

    public static void save(PVPStat p) {
        $.execute(() -> $.getDataSource().save(p));
    }

    @SneakyThrows
    public static PVPStat pull(Player p) {
        return join(p).get();
    }

    public static void kill(Player killer, Player p) {
        save(killer, true);
        save(p, false);
    }
}
