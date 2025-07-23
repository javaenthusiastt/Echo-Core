package sorryplspls.EchoCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) return true;

        if(!(player.hasPermission("essentials.fly"))){
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if(player.getAllowFlight()){
            player.sendMessage(ChatColor.RED + "Your flight has been disabled.");
            player.setAllowFlight(false);
            player.setFlying(false);
            return true;
        }else{
            player.sendMessage(ChatColor.GREEN + "Your flight has been enabled.");
            player.setAllowFlight(true);
            player.setFlying(true);
            return true;
        }
    }
}
