package sorryplspls.EchoCore.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommonUtils {

    public static void sendMessage(Player player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&7"+message));
    }

    public static void sendActionBar(Player player, String message) {
        String colored = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        TextComponent component = new TextComponent(colored);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }


}
