package sorryplspls.EchoCore.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import sorryplspls.EchoCore.Main;
import sorryplspls.EchoCore.utils.LoggerUtil;

public class FromAfkToBoxListener implements Listener {

    @EventHandler
    public void onJoinRewardCompleter(PlayerJoinEvent event) {
        Player player = event.getPlayer();

//        JsonObject json = new JsonObject();
//        json.addProperty("uuid", player.getUniqueId().toString());
//        json.addProperty("beacons_received", 1);
//        json.addProperty("afk_vouchers_received", 2);

//        Main.getInstance().getRedisManager().setex("echobox:rewards:" + player.getUniqueId().toString(), 15, json.toString());


        String key = "echobox:rewards:" + player.getUniqueId();
        if(!(player.getName().equals("sorrypls"))) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                String json = null;

                try {
                    json = Main.getInstance().getRedisManager().get(key);
                } catch (Exception e) {
                    LoggerUtil.logError("Failed to fetch player rewards", e);
                }

                if (json == null) return;

                try {
                    JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();

                    if (parsed.has("uuid")) {
                        String uuid = parsed.get("uuid").getAsString();
                        if (!uuid.equalsIgnoreCase(player.getUniqueId().toString())) {
                            Main.getInstance().getLogger().warning("UUID mismatch in reward data for " + player.getName());
                            return;
                        }
                    }

                    int beacons = parsed.has("beacons_received") ? parsed.get("beacons_received").getAsInt() : 0;
                    int afkVouchers = parsed.has("afk_vouchers_received") ? parsed.get("afk_vouchers_received").getAsInt() : 0;

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline()) return;

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lREWARDS! &7We are now transfering your rewards to your account... &c(This system is still in beta so for any bugs, report it to our discord)"));
                            player.sendMessage("§aYou received rewards from AFK Box!");
                            player.sendMessage("§7• Beacons: §f" + beacons);
                            player.sendMessage("§7• AFK Vouchers: §f" + afkVouchers);
                        }
                    }.runTask(Main.getInstance());

                } catch (Exception e) {
                    LoggerUtil.logError("Failed to parse or apply rewards", e);
                }

                try {
                    Main.getInstance().getRedisManager().del(key);
                } catch (Exception e) {
                    LoggerUtil.logError("Failed to delete Redis key after rewards", e);
                }
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}
