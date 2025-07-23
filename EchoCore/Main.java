package sorryplspls.EchoCore;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import sorryplspls.EchoCore.api.LuckPermsAPI;
import sorryplspls.EchoCore.commands.*;
import sorryplspls.EchoCore.combat.Combat;
import sorryplspls.EchoCore.catamines.CataMinesMineSystem;
import sorryplspls.EchoCore.listeners.NewbieProtectionListener;
import sorryplspls.EchoCore.listeners.PvPToggleListener;
import sorryplspls.EchoCore.placeholders.CorePlaceholder;
import sorryplspls.EchoCore.services.ActionBarService;
import sorryplspls.EchoCore.services.NewbieProtectionService;
import sorryplspls.EchoCore.staff.commands.StaffCommand;
import sorryplspls.EchoCore.staff.listeners.StaffToolListener;
import sorryplspls.EchoCore.staff.commands.ClearChatCommand;
import sorryplspls.EchoCore.staff.commands.StaffChatCommand;
import sorryplspls.EchoCore.staff.listeners.StaffChatListener;
import sorryplspls.EchoCore.staff.listeners.StaffLeaveJoinListener;
import sorryplspls.EchoCore.utils.InventorySaverUtils;
import sorryplspls.EchoCore.listeners.AfkRegionEnterListener;
import sorryplspls.EchoCore.velocity.AfkRegionVelocity;
import sorryplspls.EchoCore.redis.RedisHelper;
import sorryplspls.EchoCore.redis.RedisManager;
import sorryplspls.EchoCore.listeners.FromAfkToBoxListener;

import java.io.File;

public final class Main extends JavaPlugin {

    private static Main INSTANCE;
    private static NewbieProtectionService newbieProtectionService;
    private PvPToggleCommand pvptoggleCommand;
    private StaffCommand staffCommand;

    private AfkRegionVelocity afkRegionVelocity;

    private RedisManager redisManager;
    private RedisHelper redisHelper;

    public static Main getInstance() {
        return INSTANCE;
    }

    public PvPToggleCommand getPvPToggleCommand() {
        return pvptoggleCommand;
    }

    public NewbieProtectionService getNewbieProtectionService() {
        return newbieProtectionService;
    }

    public AfkRegionVelocity getAfkRegionVelocity(){
        return afkRegionVelocity;
    }

    public RedisManager getRedisManager(){
        return redisManager;
    }

    public RedisHelper getRedisHelper() {
        return redisHelper;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();

        newbieProtectionService = new NewbieProtectionService();
        pvptoggleCommand = new PvPToggleCommand();
        staffCommand = new StaffCommand();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        afkRegionVelocity = new AfkRegionVelocity(this);
        AfkRegionEnterListener afkRegionEnterListener = new AfkRegionEnterListener();
        getServer().getPluginManager().registerEvents(afkRegionEnterListener, this);
        getServer().getPluginManager().registerEvents(new FromAfkToBoxListener(), this);

        redisManager = new RedisManager("redis-12311.c11.us-east-1-3.ec2.redns.redis-cloud.com", 12311, "5krX9gMNuSSM22wpPeKyaurzob8wlFNP");
        redisHelper = new RedisHelper(redisManager);

        registerCommand("glow", new GlowCommand());
        registerCommand("fly", new FlyCommand());
        registerCommand("newbieprotection", new NewbieProtectionCommand(newbieProtectionService));
        registerCommand("pvptoggle", pvptoggleCommand);
        registerCommand("clearchat", new ClearChatCommand());
        registerCommand("staff", staffCommand);

        Bukkit.getScheduler().runTaskTimer(this, Combat::cleanExpired, 20L, 20L);

        getServer().getPluginManager().registerEvents(new Combat(), this);
        getServer().getPluginManager().registerEvents(new CataMinesMineSystem(), this);
        getServer().getPluginManager().registerEvents(new PvPToggleListener(pvptoggleCommand), this);
        getServer().getPluginManager().registerEvents(new NewbieProtectionListener(newbieProtectionService), this);
        getServer().getPluginManager().registerEvents(new StaffToolListener(staffCommand), this);

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                LuckPermsAPI.initialize(LuckPermsProvider.get());
                registerCommand("staffchat", new StaffChatCommand());
                getServer().getPluginManager().registerEvents(new StaffChatListener(), this);
                getServer().getPluginManager().registerEvents(new StaffLeaveJoinListener(), this);
            } catch (Exception ignored) {}
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CorePlaceholder().register();
        }

        ActionBarService.startActionBarUpdater();

        new BukkitRunnable(){
            @Override
            public void run() {
                double multi = sorryplspls.EchoCore.EchoBoxCore.MultiplierManager.getAfkMultiplier(Bukkit.getPlayer("sorrypls"));
                int finalAmount = (int) Math.floor(1 * multi);
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7Transferring your AFK Rewards to your account..."));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e&lAFK VOUCHERS! &7You recieved in total: &f" + finalAmount + " &e("+multi+"x multi)"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&lBEACONS! &7You recieved in total: &f" + finalAmount + " &b("+multi+"x multi)"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&lBEACONS! &7You recieved in total: &f" + finalAmount + " &b("+multi+"x multi)"));

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&lBEACONS! &7You recieved in total: &f" + finalAmount + " &b("+multi+"x multi)"));

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&lBEACONS! &7You recieved in total: &f" + finalAmount + " &b("+multi+"x multi)"));

            }
        }.runTask(this);
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        var command = getCommand(name);

        if (command == null) {
            return;
        }

        command.setExecutor(executor);
    }

    @Override
    public void onDisable() {
        for (Player player : staffCommand.staffModePlayers) {
            if (player != null && player.isOnline()) {
                InventorySaverUtils.restorePlayerInventory(player);
            }
        }

        if (redisManager != null) {
            redisManager.close();
        }
    }
}
