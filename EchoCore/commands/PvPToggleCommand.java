package sorryplspls.EchoCore.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import sorryplspls.EchoCore.utils.CommonUtils;
import sorryplspls.EchoCore.Main;

public class PvPToggleCommand implements CommandExecutor {

    private final NamespacedKey pvpKey;

    public PvPToggleCommand() {
        this.pvpKey = new NamespacedKey(Main.getInstance(), "pvp-disabled");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if(!(player.hasPermission("echoboxxx.pvptoggle"))){
            CommonUtils.sendMessage(player, "&cNo permission.");
            return true;
        }

        var data = player.getPersistentDataContainer();
        boolean disabled = data.has(pvpKey, PersistentDataType.BYTE) && data.get(pvpKey, PersistentDataType.BYTE) == 1;

        if (disabled) {
            data.remove(pvpKey);
            CommonUtils.sendMessage(player, "&aPvP is now enabled.");
        } else {
            data.set(pvpKey, PersistentDataType.BYTE, (byte) 1);
            CommonUtils.sendMessage(player, "&cPvP is now disabled.");
        }

        return true;
    }

    public boolean isPvPDisabled(Player player) {
        var data = player.getPersistentDataContainer();
        return data.has(pvpKey, PersistentDataType.BYTE) && data.get(pvpKey, PersistentDataType.BYTE) == 1;
    }
}
