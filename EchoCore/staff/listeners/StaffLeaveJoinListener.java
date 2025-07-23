package sorryplspls.EchoCore.staff.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import sorryplspls.EchoCore.utils.StaffUtils;

public class StaffLeaveJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        if (!joiningPlayer.hasPermission("staff")) {
            return;
        }

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("staff")) {
                StaffUtils.sendStaffStatusMessage(staff, joiningPlayer, true);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player leavingPlayer = event.getPlayer();

        if (!leavingPlayer.hasPermission("staff")) {
            return;
        }

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("staff")) {
                StaffUtils.sendStaffStatusMessage(staff, leavingPlayer, false);
            }
        }
    }
}
