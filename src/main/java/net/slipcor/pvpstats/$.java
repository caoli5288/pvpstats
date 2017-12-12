package net.slipcor.pvpstats;

import com.mengcraft.simpleorm.DatabaseException;
import com.mengcraft.simpleorm.EbeanHandler;
import lombok.Getter;
import net.slipcor.pvpstats.entity.PVPKill;
import net.slipcor.pvpstats.entity.PVPStat;
import net.slipcor.pvpstats.lh_support.LHDeathHook;
import net.slipcor.pvpstats.lh_support.LHEloHook;
import net.slipcor.pvpstats.lh_support.LHKillHook;
import net.slipcor.pvpstats.lh_support.LHMaxStreakHook;
import net.slipcor.pvpstats.lh_support.LHStreakHook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * main class
 *
 * @author slipcor
 */

public class $ extends JavaPlugin {

    public static final ExecutorService POOL = Executors.newFixedThreadPool(1);

    protected Plugin paHandler = null;

    // Settings Variables
    protected Boolean mySQL = false;
    protected String dbHost = null;
    protected String dbUser = null;
    protected String dbPass = null;
    protected String dbDatabase = null;
    protected String dbTable = null;
    protected String dbKillTable = null;
    protected int dbPort = 3306;

    private final PSListener entityListener = new PSListener(this);
    protected final PSPAListener paListener = new PSPAListener(this);
    private PSPAPluginListener paPluginListener;

    private static $ plugin;

    @Getter
    private static EbeanHandler dataSource;

    public void onEnable() {
        plugin = this;

        try {
            OfflinePlayer.class.getDeclaredMethod("getUniqueId");
        } catch (Exception e) {
            getLogger().info("Your server is still not ready for UUIDs? Use PVP Stats older than v0.8.25.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final PluginDescriptionFile pdfFile = getDescription();

        getServer().getPluginManager().registerEvents(entityListener, this);

        loadConfig();
        if (!mySQL) {
            setEnabled(false);
            return;
        }

        dataSource = new EbeanHandler(this);
        dataSource.setUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbDatabase + "");
        dataSource.setUserName(dbUser);
        dataSource.setPassword(dbPass);

        if (dataSource.isNotInitialized()) {
            dataSource.define(PVPStat.class);
            dataSource.define(PVPKill.class);
            try {
                dataSource.initialize();
            } catch (DatabaseException e) {
                e.printStackTrace();
                setEnabled(false);
                return;
            }
        }
        dataSource.install(true);

        loadHooks();

        if (getConfig().getBoolean("PVPArena")) {
            if (getServer().getPluginManager().isPluginEnabled("pvparena")) {
                getServer().getPluginManager().registerEvents(paListener, this);
            } else {
                paPluginListener = new PSPAPluginListener(this);
                getServer().getPluginManager().registerEvents(paPluginListener, this);
            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("LeaderHeads")) {
            new LHDeathHook(this);
            new LHEloHook(this);
            new LHKillHook(this);
            new LHMaxStreakHook(this);
            new LHStreakHook(this);
            getLogger().info("Hook into lh. namespace i5mc-pvp");
        }

        loadLanguage();

        if (getConfig().getBoolean("clearonstart", true)) {

            //run the task within its own runnable no need for an imbedded class
            Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pvpstats cleanup"), 5000L);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PVPStats - PlaceholderAPI found.");
            new PAPIHook(getPlugin()).hook();
        }

        getLogger().info("enabled. (version " + pdfFile.getVersion() + ")");
    }

    private void loadLanguage() {
        final File langFile = new File(this.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            try {
                langFile.createNewFile();
            } catch (IOException e) {
                this.getLogger().warning("Language file could not be created. Using defaults!");
                e.printStackTrace();
            }
        }
        final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(langFile);
        if (Language.load(cfg)) {
            try {
                cfg.save(langFile);
            } catch (IOException e) {
                this.getLogger().warning("Language file could not be written. Using defaults!");
                e.printStackTrace();
            }
        }
    }

    private void loadHooks() {
        final Plugin paPlugin = getServer().getPluginManager().getPlugin("pvparena");
        if (paPlugin != null && paPlugin.isEnabled()) {
            getLogger().info("<3 PVP Arena");
            this.paHandler = paPlugin;
        }
    }

    public void sendPrefixed(final CommandSender sender, final String message) {
        if (!"".equals(message)) {
            sender.sendMessage(Language.MSG_PREFIX + message);
        }
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {

        if (args == null || args.length < 1 || !(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("debug") || args[0].equalsIgnoreCase("cleanup") || args[0].equalsIgnoreCase("purge"))) {
            if (!parsecommand(sender, args)) {
                sender.sendMessage("/pvpstats - show your pvp stats");
                sender.sendMessage("/pvpstats [player] - show player's pvp stats");
                sender.sendMessage("/pvpstats [amount] - show the top [amount] players (K-D)");
                if (sender.hasPermission("pvpstats.top")) {
                    sender.sendMessage("/pvpstats top [amount] - show the top [amount] players (K-D)");
                    sender.sendMessage("/pvpstats top [type] - show the top 10 players of the type");
                    sender.sendMessage("/pvpstats top [type] [amount] - show the top [amount] players of the type");
                }
                if (sender.hasPermission("pvpstats.reload")) {
                    sender.sendMessage("/pvpstats reload - reload the configs");
                }
                if (sender.hasPermission("pvpstats.cleanup")) {
                    sender.sendMessage("/pvpstats cleanup - removes multi entries");
                }
                if (sender.hasPermission("pvpstats.purge")) {
                    sender.sendMessage("/pvpstats purge [specific | standard | both] [amount] - remove kill entries older than [amount] days");
                }
                if (sender.hasPermission("pvpstats.debug")) {
                    sender.sendMessage("/pvpstats debug [on | off] - enable or disable debugging");
                }
            }
            return true;
        } else if (args[0].equalsIgnoreCase("cleanup")) {

        } else if (args[0].equalsIgnoreCase("debug")) {
            if (!sender.hasPermission("pvpstats.debug")) {
                sendPrefixed(sender, Language.MSG_NOPERMDEBUG.toString());
                return true;
            }

            if (args.length > 1) {
                getConfig().set("debug", args[1]);
            }

            return true;
        } else if (args[0].equalsIgnoreCase("purge")) {

        }

        if (!sender.hasPermission("pvpstats.reload")) {
            sendPrefixed(sender, Language.MSG_NOPERMRELOAD.toString());
            return true;
        }

        this.reloadConfig();
        loadConfig();
        loadLanguage();
        sendPrefixed(sender, Language.MSG_RELOADED.toString());

        return true;
    }

    private boolean parsecommand(final CommandSender sender, final String[] args) {
        return true;
    }

    private void loadConfig() {

        getConfig().options().copyDefaults(true);
        saveConfig();

        // get variables from settings handler
        if (getConfig().getBoolean("MySQL", false)) {
            this.mySQL = getConfig().getBoolean("MySQL", false);
            this.dbHost = getConfig().getString("MySQLhost", "");
            this.dbUser = getConfig().getString("MySQLuser", "");
            this.dbPass = getConfig().getString("MySQLpass", "");
            this.dbDatabase = getConfig().getString("MySQLdb", "");
            this.dbTable = getConfig().getString("MySQLtable", "pvpstats");

            if (getConfig().getBoolean("collectprecise")) {
                this.dbKillTable = getConfig().getString("MySQLkilltable", "pvpkillstats");
            }

            this.dbPort = getConfig().getInt("MySQLport", 3306);
        }

        // Check Settings
        if (this.mySQL) {
            if (this.dbHost.equals("")) {
                this.mySQL = false;
            } else if (this.dbUser.equals("")) {
                this.mySQL = false;
            } else if (this.dbPass.equals("")) {
                this.mySQL = false;
            } else if (this.dbDatabase.equals("")) {
                this.mySQL = false;
            }
        }

        // Enabled SQL/MySQL
    }

    public void onDisable() {
        getLogger().info("disabled. (version " + getDescription().getVersion() + ")");
    }

    public boolean ignoresWorld(final String name) {
        if (!getConfig().contains("ignoreworlds")) {
            return false;
        }
        return getConfig().getStringList("ignoreworlds").contains(name);
    }

    public static $ getPlugin() {
        return plugin;
    }
}
