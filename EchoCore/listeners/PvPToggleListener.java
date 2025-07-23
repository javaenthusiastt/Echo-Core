package sorryplspls.EchoCore.listeners;

import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import sorryplspls.EchoCore.commands.PvPToggleCommand;
import sorryplspls.EchoCore.regions.RegionUtil;
import sorryplspls.EchoCore.utils.CommonUtils;

public class PvPToggleListener implements Listener {

    private final PvPToggleCommand pvptoggleCommand;

    public PvPToggleListener(PvPToggleCommand pvptoggleCommand) {
        this.pvptoggleCommand = pvptoggleCommand;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if(RegionUtil.isPlayerInBeaconZone(victim)) return;

        Player attacker = sorryplspls.EchoCore.utils.DamageUtils.getPlayerFromDamager(event);
        if (attacker == null) return;

        boolean victimPvPDisabled = pvptoggleCommand.isPvPDisabled(victim);
        boolean attackerPvPDisabled = pvptoggleCommand.isPvPDisabled(attacker);

        if (victimPvPDisabled || attackerPvPDisabled) {
            event.setCancelled(true);

            if(attackerPvPDisabled){
                CommonUtils.sendMessage(attacker, "&7You got PvP off!");
            }

            if (victimPvPDisabled) {
                CommonUtils.sendMessage(attacker, "&7" + victim.getName() + " &chas PvP disabled and cannot take damage.");
            }
        }
    }
}
