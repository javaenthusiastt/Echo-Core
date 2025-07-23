package sorryplspls.EchoCore.services;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

public class PlayTimeService {

    public static boolean isEligibleForProtection(@NotNull OfflinePlayer player) {
        int ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int minutes = ticks / 20 / 60;
        return minutes < 35;
    }


}
