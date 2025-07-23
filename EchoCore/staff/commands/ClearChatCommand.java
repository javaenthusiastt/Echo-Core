package sorryplspls.EchoCore.staff.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sorryplspls.EchoCore.utils.StaffUtils;

public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            return true;
        }

        if(!(player.hasPermission("staff"))){
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        for (int i = 0; i < 100; i++) {
            Bukkit.broadcastMessage(" ");
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cPublic chat got cleared."));
        StaffUtils.sendMessageToOnlineStaff(player.getName() + " cleared the public chat.");

        return true;
    }
}
