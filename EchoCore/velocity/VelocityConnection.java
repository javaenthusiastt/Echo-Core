package sorryplspls.EchoCore.velocity;

import org.bukkit.entity.Player;
import sorryplspls.EchoCore.Main;
import sorryplspls.EchoCore.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VelocityConnection {

    /**
     * Connects a player to another server via Velocity messaging.
     *
     * @param player The player to connect.
     * @param serverName The server name.
     */

    public static void connectPlayerToServerBungee(Player player, String serverName) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (IOException e) {
            CommonUtils.sendMessage(player, "&fWe failed to send you to &c" + serverName + "&f!");
            return;
        }

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }
}
