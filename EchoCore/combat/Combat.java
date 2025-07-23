package sorryplspls.EchoCore.combat;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import sorryplspls.EchoCore.services.ActionBarService;
import sorryplspls.EchoCore.utils.DamageUtils;
import sorryplspls.EchoCore.Main;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Combat implements Listener {

    private static final long COMBAT_DURATION_MS = 10_000;
    private static final long COMBAT_DURATION_TICKS = COMBAT_DURATION_MS / 50;
    private static final Map<UUID, CombatData> combatMap = new ConcurrentHashMap<>();

    private static class CombatData {
        long lastTagged;
        boolean wasFlying;
        boolean allowFlight;
        float walkSpeed;

        CombatData(Player player) {
            this.lastTagged = System.currentTimeMillis();
            this.wasFlying = player.isFlying();
            this.allowFlight = player.getAllowFlight();
            this.walkSpeed = player.getWalkSpeed();
        }

        void updateTimestamp() {
            this.lastTagged = System.currentTimeMillis();
        }
    }

    public static void tagCombat(Player player) {
        UUID uuid = player.getUniqueId();
        CombatData data = combatMap.get(uuid);

        if (data == null) {
            data = new CombatData(player);
            combatMap.put(uuid, data);

            player.setFlying(false);
            player.setAllowFlight(false);
            player.setWalkSpeed(Math.min(0.2f, player.getWalkSpeed()));
        } else {
            data.updateTimestamp();
        }

        ActionBarService.setPlayerStateWithTimeout(player, ActionBarService.PlayerState.COMBAT, COMBAT_DURATION_TICKS);
    }

    public static boolean isInCombat(Player player) {
        CombatData data = combatMap.get(player.getUniqueId());
        if (data == null) return false;

        long timeSince = System.currentTimeMillis() - data.lastTagged;
        if (timeSince > COMBAT_DURATION_MS) {
            endCombat(player);
            return false;
        }
        return true;
    }

    public static void endCombat(Player player) {
        CombatData data = combatMap.remove(player.getUniqueId());
        if (data == null) return;

        if (!player.isOnline() || player.isDead()) return;

        if (player.getAllowFlight() != data.allowFlight) {
            player.setAllowFlight(data.allowFlight);
        }
        if (player.isFlying() != data.wasFlying) {
            player.setFlying(data.wasFlying);
        }
        if (Float.compare(player.getWalkSpeed(), data.walkSpeed) != 0) {
            player.setWalkSpeed(data.walkSpeed);
        }

        ActionBarService.setPlayerStateWithTimeout(player, ActionBarService.PlayerState.DEFAULT, 1);
        player.sendMessage(ChatColor.GREEN + "You are no longer in combat!");
    }


    public static double getRemainingCombatSeconds(Player player) {
        CombatData data = combatMap.get(player.getUniqueId());
        if (data == null) return 0.0;

        long remaining = COMBAT_DURATION_MS - (System.currentTimeMillis() - data.lastTagged);
        return Math.max(0.0, remaining / 1000.0);
    }

    public static void cleanExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, CombatData>> iter = combatMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<UUID, CombatData> entry = iter.next();
            if (now - entry.getValue().lastTagged > COMBAT_DURATION_MS) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    endCombat(player);
                }
                iter.remove();
            }
        }
    }


    private boolean isInRegion(Player player, String regionId) {
        return isInRegionAt(player.getLocation(), regionId);
    }

    private boolean isInRegionAt(Location location, String regionId) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));
        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionId)) return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isInCombat(player) || event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        Location to = event.getTo();
        if (to == null) return;

        boolean leavingToSpawn = !isInRegion(player, "spawn") && isInRegionAt(to, "spawn");
        boolean insideSpawn = isInRegion(player, "spawn");

        if (leavingToSpawn || insideSpawn) {
            player.teleport(new Location(player.getWorld(), 263, 61, 56));
        }
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!isInCombat(player)) return;

        double remaining = getRemainingCombatSeconds(player);
        player.sendMessage(ChatColor.RED + "You can't use this while in combat! (" + String.format("%.1f", remaining) + "s)");
        event.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        endCombat(event.getPlayer());
    }

    @EventHandler
    public void onRocketUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isInCombat(player)) return;

        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.FIREWORK_ROCKET) return;

        event.setCancelled(true);
        double remaining = getRemainingCombatSeconds(player);
        player.sendMessage(ChatColor.RED + "You can't use rockets while in combat! (" + String.format("%.1f", remaining) + "s)");
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isInCombat(player)) {
            player.damage(1000.0);
            endCombat(player);
        }
    }

    @EventHandler
    public void onCombatDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (event.isCancelled() || event.getDamage() <= 0 || victim.getGameMode() == GameMode.CREATIVE) return;

        Player attacker = DamageUtils.getPlayerFromDamager(event);
        if (attacker == null || attacker.getGameMode() == GameMode.CREATIVE) return;

        new BukkitRunnable() {
            final double before = victim.getHealth();

            @Override
            public void run() {
                if (victim.getHealth() < before) {
                    tagCombat(victim);
                    tagCombat(attacker);
                }
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}
