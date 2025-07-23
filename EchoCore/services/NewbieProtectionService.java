package sorryplspls.EchoCore.services;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NewbieProtectionService {

    private final Set<UUID> manuallyDisabled = new HashSet<>();

    public void disableProtection(Player player) {
        manuallyDisabled.add(player.getUniqueId());
    }

    public boolean hasProtection(Player player) {
        return isEligible(player) && !manuallyDisabled.contains(player.getUniqueId());
    }

    public boolean isEligible(OfflinePlayer player) {
        int ticks = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
        int minutes = ticks / 20 / 60;
        return minutes < 35;
    }
}
