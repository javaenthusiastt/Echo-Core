package sorryplspls.EchoCore.EchoBoxCore;

import org.bukkit.entity.Player;

public class MultiplierManager {

    /**
     * Determines the drop multiplier based on the player's permissions.
     * Evaluated live each time to reflect any runtime permission changes.
     *
     * @param player the player whose multiplier to fetch
     * @return the multiplier value
     */

    public static double getMiningMultiplier(Player player) {
        if (player.hasPermission("custom") || player.hasPermission("echo+")) {
            return 3.0;
        }
        if (player.hasPermission("echo")) {
            return 2.5;
        }
        if (player.hasPermission("mvp+")) {
            return 1.85;
        }
        if (player.hasPermission("mvp")) {
            return 1.5;
        }
        if (player.hasPermission("vip")) {
            return 1.25;
        }
        return 1.0;
    }

    public static double getAfkMultiplier(Player player) {
        if (player.hasPermission("custom") || player.hasPermission("echo+")) {
            return 3.0;
        }
        if (player.hasPermission("echo")) {
            return 2.0;
        }
        if (player.hasPermission("mvp+")) {
            return 1.5;
        }
        if (player.hasPermission("mvp")) {
            return 1.5;
        }
        if (player.hasPermission("vip")) {
            return 1.5;
        }
        return 1.0;
    }
}
