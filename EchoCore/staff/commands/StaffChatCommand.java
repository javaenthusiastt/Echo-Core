package sorryplspls.EchoCore.staff.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sorryplspls.EchoCore.utils.StaffUtils;

import java.util.HashSet;

public class StaffChatCommand implements CommandExecutor {

    public static HashSet<Player> staffChatPlayers = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!(player.hasPermission("staff"))) {
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if(staffChatPlayers.contains(player)){
            StaffUtils.sendMessageInStaffFormat(player, "&fYour staffchat is now &cdisabled&f.");
            staffChatPlayers.remove(player);
        }else{
            StaffUtils.sendMessageInStaffFormat(player, "&fYour staffchat is now &aenabled&f.");
            staffChatPlayers.add(player);
        }

        return true;
    }
}
