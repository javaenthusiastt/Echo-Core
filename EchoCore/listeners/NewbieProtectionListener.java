package sorryplspls.EchoCore.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import sorryplspls.EchoCore.regions.RegionUtil;
import sorryplspls.EchoCore.services.NewbieProtectionService;
import sorryplspls.EchoCore.utils.CommonUtils;

public class NewbieProtectionListener implements Listener {

    private final NewbieProtectionService protectionService;

    public NewbieProtectionListener(NewbieProtectionService protectionService) {
        this.protectionService = protectionService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (protectionService.hasProtection(event.getPlayer())) {
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Player attacker = null;

        if (damager instanceof Player player) {
            attacker = player;
        } else if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                attacker = shooter;
            }
        }

        if (attacker != null && protectionService.hasProtection(attacker)) {
            if (!protectionService.isEligible(attacker)) {
                CommonUtils.sendMessage(attacker, "&cYour Newbie Protection has expired.");
                protectionService.disableProtection(attacker);
            } else {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getEntity() instanceof Player victim) {
            boolean victimInBeacon = RegionUtil.isPlayerInBeaconZone(victim);
            boolean attackerInBeacon = attacker != null && RegionUtil.isPlayerInBeaconZone(attacker);

            if (victimInBeacon && attacker != null && !attackerInBeacon) {
                if (!protectionService.isEligible(attacker)) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (!victimInBeacon) {
                if (protectionService.hasProtection(victim)) {
                    if (!protectionService.isEligible(victim)) {
                        CommonUtils.sendMessage(victim, "&cYour Newbie Protection has expired.");
                        protectionService.disableProtection(victim);
                        return;
                    }

                    event.setCancelled(true);

                    if (attacker != null) {
                        CommonUtils.sendMessage(attacker, "&cThis player is currently protected by Newbie Protection!");
                    }
                }
            }
        }
    }
}
