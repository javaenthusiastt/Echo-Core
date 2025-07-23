package sorryplspls.EchoCore.utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import sorryplspls.EchoCore.api.LuckPermsAPI;

public class StaffUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final String DEFAULT_HEX_COLOR = "FFFFFF";

    public static void sendMessageToOnlineStaff(String s){
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("staff")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[STAFF] &7" + s));
            }
        }
    }

    public static void sendMessageInStaffFormat(Player player, String s){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[STAFF] &7" + s));
    }

    public static void sendStaffChat(Player target, Player sender, String s) {
        String rawColor = LuckPermsAPI.getGroupColorHex(sender);
        String hexColor = (rawColor != null && rawColor.startsWith("&#")) ? rawColor.replace("&#", "") : DEFAULT_HEX_COLOR;
        String formatted = "<aqua>[STAFF] <gray>(Box) <blue><color:#" + hexColor + ">" + sender.getName() + "</color><gray>: </gray><white>" + s + "</white>";
        target.sendMessage(MINI_MESSAGE.deserialize(formatted));
    }

    public static void sendStaffStatusMessage(Player target, Player sender, boolean joined) {
        String rawColor = LuckPermsAPI.getGroupColorHex(sender);
        String hexColor = (rawColor != null && rawColor.startsWith("&#"))
                ? rawColor.replace("&#", "")
                : DEFAULT_HEX_COLOR;

        String message = String.format(
                "<aqua>[STAFF] <color:#%s>%s</color> <gray>has </gray>%s<gray> the server (Box)</gray>",
                hexColor,
                sender.getName(),
                joined ? "<green>joined</green>" : "<red>left</red>"
        );

        target.sendMessage(MINI_MESSAGE.deserialize(message));
    }
}
