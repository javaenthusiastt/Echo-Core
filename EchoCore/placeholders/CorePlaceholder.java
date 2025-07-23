package sorryplspls.EchoCore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sorryplspls.EchoCore.Main;

public class CorePlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "echo";
    }

    @Override
    public @NotNull String getAuthor() {
        return "sorryplspls";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("pvp")) {
            Player onlinePlayer = player.getPlayer();

            if (onlinePlayer == null) {
                return ChatColor.RED + "Player offline";
            }

            if(Main.getInstance().getNewbieProtectionService().hasProtection(onlinePlayer)){
                return ChatColor.translateAlternateColorCodes('&', "&c&lᴘᴠᴘ ᴅɪꜱᴀʙʟᴇᴅ");
            }

            boolean pvpDisabled = Main.getInstance().getPvPToggleCommand().isPvPDisabled(onlinePlayer);
            return pvpDisabled
                    ? ChatColor.translateAlternateColorCodes('&', "&c&lᴘᴠᴘ ᴅɪꜱᴀʙʟᴇᴅ")
                    : ChatColor.translateAlternateColorCodes('&', "&a&lᴘᴠᴘ ᴇɴᴀʙʟᴇᴅ");

        }

        return null;
    }
}
