package sorryplspls.EchoCore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import sorryplspls.EchoCore.Main;
import sorryplspls.EchoCore.velocity.VelocityConnection;

import java.util.*;

public class AfkRegionEnterListener implements Listener {

    private final Map<UUID, Long> lastTeleportAttempt = new HashMap<>();
    private static final long TELEPORT_COOLDOWN_MS = 5000;
    Set<String> allowed = Set.of("sorrypls", "maxi23_");

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!allowed.contains(player.getName().toLowerCase())) {
            return;
        }

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {return;}

        if (Main.getInstance().getAfkRegionVelocity().isInAfkRegion(player)) {
            long now = System.currentTimeMillis();
            Long lastAttempt = lastTeleportAttempt.get(player.getUniqueId());

            if (lastAttempt == null || (now - lastAttempt) > TELEPORT_COOLDOWN_MS) {
                lastTeleportAttempt.put(player.getUniqueId(), now);

                Main.getInstance().getRedisHelper().sendLastKnownLocationData(player, player.getLocation().clone());
                VelocityConnection.connectPlayerToServerBungee(player, "boxafk");
            }
        }
    }
}
