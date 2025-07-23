package sorryplspls.EchoCore.api;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import java.util.UUID;

public class LuckPermsAPI {

    private static LuckPerms luckPerms;

    public static void initialize(LuckPerms api) {
        luckPerms = api;
    }

    public static Component getLpPrefixComponent(UUID uuid) {
        if (luckPerms == null) return Component.empty();

        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) return Component.empty();

        String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null) return Component.empty();

        try {
            return MiniMessage.miniMessage().deserialize(prefix);
        } catch (Exception ignored) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);
        }
    }

    private static String getPrimaryGroup(UUID uuid) {
        if (luckPerms == null) return null;

        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) return null;

        return user.getPrimaryGroup();
    }

    public static String getGroupColorHex(Player player) {
        String group = getPrimaryGroup(player.getUniqueId()).toLowerCase();

        return switch (group) {
            case "owner" -> "&#FF7070";
            case "head_admin", "admin" -> "&#D43535";
            case "manager" -> "&#7500FF";
            case "builder" -> "&#FFAC1C";
            case "srmod" -> "&#E56D33";
            case "mod" -> "&#DDD618";
            case "helper" -> "&#33B1FF";
            case "trainee" -> "&#74C3A6";
            default -> "&#FFFFFF";
        };
    }
}
