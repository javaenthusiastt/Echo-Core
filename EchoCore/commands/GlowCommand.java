package sorryplspls.EchoCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlowCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) return true;

        if(!(player.hasPermission("essentials.fly"))){
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if(player.isGlowing()){
            player.setGlowing(false);
            player.sendMessage(ChatColor.RED + "You are no longer glowing.");
            return true;
        }else{
            player.setGlowing(true);
            player.sendMessage(ChatColor.GREEN + "You are now glowing.");
            return true;
        }
    }
}
