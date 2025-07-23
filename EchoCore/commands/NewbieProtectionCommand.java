package sorryplspls.EchoCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sorryplspls.EchoCore.services.NewbieProtectionService;
import sorryplspls.EchoCore.utils.CommonUtils;

public class NewbieProtectionCommand implements CommandExecutor {

    private final NewbieProtectionService protectionService;

    public NewbieProtectionCommand(NewbieProtectionService protectionService) {
        this.protectionService = protectionService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!protectionService.isEligible(player)) {
            CommonUtils.sendMessage(player, "&cYour 35 minutes of Newbie Protection have expired.");
            return true;
        }

        if (protectionService.hasProtection(player)) {
            protectionService.disableProtection(player);
            CommonUtils.sendMessage(player, "&cYou have disabled your Newbie Protection!");
        } else {
            CommonUtils.sendMessage(player, "&cYou cannot re-enable Newbie Protection once disabled.");
        }

        return true;
    }
}
