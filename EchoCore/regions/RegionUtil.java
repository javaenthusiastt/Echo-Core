package sorryplspls.EchoCore.regions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RegionUtil {

    /**
     * Static method to check if a player is inside a region defined by two corners.
     */

    static Location beacon_1 = new Location(Bukkit.getWorld("world"), 272.356, 67, 54.394);
    static Location beacon_2 = new Location(Bukkit.getWorld("world"), 255.522, 51, 37.531);

    public static boolean isPlayerInRegion(Player player, Location corner1, Location corner2) {
        Location loc = player.getLocation();

        if (!loc.getWorld().equals(corner1.getWorld())) return false;

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        double xMin = Math.min(corner1.getX(), corner2.getX());
        double xMax = Math.max(corner1.getX(), corner2.getX());

        double yMin = Math.min(corner1.getY(), corner2.getY());
        double yMax = Math.max(corner1.getY(), corner2.getY());

        double zMin = Math.min(corner1.getZ(), corner2.getZ());
        double zMax = Math.max(corner1.getZ(), corner2.getZ());

        return x >= xMin && x <= xMax
                && y >= yMin && y <= yMax
                && z >= zMin && z <= zMax;
    }

    /**
     * Check if the player is inside a fixed beacon zone.
     */

    public static boolean isPlayerInBeaconZone(Player player) {
        return isPlayerInRegion(player, beacon_1, beacon_2);
    }
}


