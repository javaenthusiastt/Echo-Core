package sorryplspls.EchoCore.services;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import sorryplspls.EchoCore.combat.Combat;
import sorryplspls.EchoCore.utils.CommonUtils;
import sorryplspls.EchoCore.Main;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionBarService {

    public static final int UPDATE_INTERVAL_TICKS = 10;

    public enum PlayerState {
        DEFAULT,
        COMBAT,
        STAFF_MODE,
    }

    public static final Map<UUID, PlayerState> playerStates = new ConcurrentHashMap<>();
    private static final Map<UUID, BukkitTask> resetTasks = new ConcurrentHashMap<>();

    public static void startActionBarUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updatePlayerActionBar(player);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, UPDATE_INTERVAL_TICKS);
    }

    private static void updatePlayerActionBar(Player player) {
        if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            return;
        }

        PlayerState state = playerStates.getOrDefault(player.getUniqueId(), PlayerState.DEFAULT);

        double currentHealth = player.getHealth();
        double maxHealth = player.getMaxHealth();
        currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));

        String healthPart = String.format(
                "&c%s&8/&c%s ❤",
                formatHealth(currentHealth),
                formatHealth(maxHealth)
        );

        String message;

        if (Combat.isInCombat(player)) {
            double timeRemaining = Combat.getRemainingCombatSeconds(player);
            String timeColor = timeRemaining < 5 ? "&2" : "&c";

            String timeStr = timeRemaining < 10
                    ? String.format("%.1f", timeRemaining)
                    : String.format("%.0f", timeRemaining);

            message = String.format(
                    "%s &8| &fIn Combat: %s%s",
                    healthPart, timeColor, timeStr
            );
        } else {
            switch (state) {
                case STAFF_MODE -> {
                    message = "&fYou are currently &c" + (player.isInvisible() ? "&cVANISHED, &cSTAFF" : "&cNOT VANISHED, &cSTAFF");
                }
                default -> message = healthPart;
            }
        }

        CommonUtils.sendActionBar(player, message);
    }

    private static String formatHealth(double value) {
        return (value % 1 == 0)
                ? String.format("%.0f", value)
                : String.format("%.1f", value);
    }

    /**
     * Set player state with optional XP (only used if state == MINED) and auto-reset timeout.
     *
     * @param player player to update
     * @param state new state
     * @param timeoutTicks how long before resetting back to DEFAULT (in ticks)
     */

    public static void setPlayerStateWithTimeout(Player player, PlayerState state, long timeoutTicks) {
        UUID uuid = player.getUniqueId();

        BukkitTask previousTask = resetTasks.remove(uuid);
        if (previousTask != null) {
            previousTask.cancel();
        }

        playerStates.put(uuid, state);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                playerStates.put(uuid, PlayerState.DEFAULT);
                resetTasks.remove(uuid);
            }
        }.runTaskLater(Main.getInstance(), timeoutTicks);

        resetTasks.put(uuid, task);
    }
}
