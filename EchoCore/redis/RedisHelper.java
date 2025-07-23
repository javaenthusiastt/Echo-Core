package sorryplspls.EchoCore.redis;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RedisHelper {

    private static final Gson gson = new Gson();
    private static final String CHANNEL = "echobox:db";

    private static class LocationData {
        String playerUUID;
        String world;
        double x, y, z;
        float yaw, pitch;

        LocationData(Player player, Location loc) {
            this.playerUUID = player.getUniqueId().toString();
            this.world = loc.getWorld().getName();
            this.x = loc.getX();
            this.y = loc.getY();
            this.z = loc.getZ();
            this.yaw = loc.getYaw();
            this.pitch = loc.getPitch();
        }
    }

    private final RedisManager redisManager;

    public RedisHelper(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public void sendLastKnownLocationData(Player player, Location location) {
        LocationData locationData = new LocationData(player, location);
        String json = gson.toJson(locationData);
//        String message = "LOCATION|" + json;

        redisManager.setex("echobox:lastLocation:" + player.getUniqueId().toString(), 10, json);
//        redisManager.publish(CHANNEL, message);
    }
}
