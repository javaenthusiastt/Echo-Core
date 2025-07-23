package sorryplspls.EchoCore.velocity;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import sorryplspls.EchoCore.Main;

public class AfkRegionVelocity {
    private final String worldName;
    private final double minX, maxX, minY, maxY, minZ, maxZ;
    private final boolean needWater;

    public AfkRegionVelocity(Main plugin) {
        var section = plugin.getConfig().getConfigurationSection("send_server_to_afk_when_they_are_here");
        if (section == null) {
            plugin.getLogger().warning("AFK region config missing!");
            worldName = "";
            minX = maxX = minY = maxY = minZ = maxZ = 0;
            needWater = false;
            return;
        }

        worldName = section.getString("corner1.world", "");
        double x1 = section.getDouble("corner1.x");
        double y1 = section.getDouble("corner1.y");
        double z1 = section.getDouble("corner1.z");
        double x2 = section.getDouble("corner2.x");
        double y2 = section.getDouble("corner2.y");
        double z2 = section.getDouble("corner2.z");

        minX = Math.min(x1, x2);
        maxX = Math.max(x1, x2);
        minY = Math.min(y1, y2);
        maxY = Math.max(y1, y2);
        minZ = Math.min(z1, z2);
        maxZ = Math.max(z1, z2);

        needWater = section.getBoolean("shouldNeedToTouchWater", false);
    }

    public boolean isInAfkRegion(Player player) {
        if (!player.getWorld().getName().equals(worldName)) return false;

        var loc = player.getLocation();
        double px = loc.getX();
        double py = loc.getY();
        double pz = loc.getZ();

        boolean inside = (px >= minX && px <= maxX) &&
                (py >= minY && py <= maxY) &&
                (pz >= minZ && pz <= maxZ);

        if (!inside) return false;

        if (needWater) {
            var feetBlock = player.getLocation().subtract(0, 0.1, 0).getBlock();
            return feetBlock.getType() == Material.WATER;
        }

        return true;
    }
}