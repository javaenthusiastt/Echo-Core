package sorryplspls.EchoCore.staff.listeners;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import sorryplspls.EchoCore.staff.commands.StaffChatCommand;
import sorryplspls.EchoCore.utils.StaffUtils;

public class StaffChatListener implements Listener {


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String original = event.getMessage();

        String staffMessage;

        if (StaffChatCommand.staffChatPlayers.contains(player)) {
            staffMessage = message.trim();
        } else if (message.startsWith("#") && player.hasPermission("staff")) {
            staffMessage = message.substring(1).trim();
        } else {
            return;
        }

        event.setCancelled(true);

        if (staffMessage.isEmpty()) {
            StaffUtils.sendMessageInStaffFormat(player, "Your staff chat message cannot be empty.");
            return;
        }

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("staff")) {
                StaffUtils.sendStaffChat(staff, player, staffMessage);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        StaffChatCommand.staffChatPlayers.remove(event.getPlayer());
    }
}
