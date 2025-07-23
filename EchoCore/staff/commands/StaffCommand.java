package sorryplspls.EchoCore.staff.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import sorryplspls.EchoCore.services.ActionBarService;
import sorryplspls.EchoCore.utils.AdventureUtils;
import sorryplspls.EchoCore.utils.InventorySaverUtils;
import sorryplspls.EchoCore.utils.InventoryUtils;
import sorryplspls.EchoCore.utils.StaffUtils;

import java.util.HashSet;

public class StaffCommand implements CommandExecutor {

    public HashSet<Player> staffModePlayers = new HashSet<>();
    public String LONG_LINE_LORE = "&3&m                                     ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return true;

        if(!(player.hasPermission("staff"))){
            player.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) && !staffModePlayers.contains(player)) {
            player.sendMessage(ChatColor.RED + "Do not enter Staff Mode while having invisibility, etc.");
            return true;
        }

        if(!(staffModePlayers.contains(player))){
            STAFF_MODE_TYPE_ENTER(player);
            StaffUtils.sendMessageToOnlineStaff("&f" + player.getName() + "'s staff mode is now &aON&f.");
            return true;
        }else{
            STAFF_MODE_TYPE_EXIT(player);
            StaffUtils.sendMessageToOnlineStaff("&f" + player.getName() + "'s staff mode is now &cOFF&f.");
            return true;
        }
    }

    private void STAFF_MODE_TYPE_ENTER(Player player){
        staffModePlayers.add(player);
        ActionBarService.playerStates.put(player.getUniqueId(), ActionBarService.PlayerState.STAFF_MODE);
        InventorySaverUtils.saveInventory(player);

        player.setHealth(20f);
        player.setSaturation(20f);
        player.setFoodLevel(20);

        player.performCommand("v");

        player.getInventory().setItem(0, InventoryUtils.createItem(Material.BOOK, "&3Inspection Book", "", "&9[Echo Mod]", LONG_LINE_LORE, "&7Inspect a player’s inventory and status.", LONG_LINE_LORE));
        player.getInventory().setItem(1, InventoryUtils.createItem(Material.PACKED_ICE, "&3Freeze Player", "", "&9[Echo Mod]", LONG_LINE_LORE, "&7Freeze a player in place to prevent movement.", LONG_LINE_LORE));
        player.getInventory().setItem(2, InventoryUtils.createItem(Material.WOODEN_AXE, "&3Wand (WE)", "", "&9[Echo Mod]", LONG_LINE_LORE, "&7Select regions for WorldEdit actions.", LONG_LINE_LORE));
        player.getInventory().setItem(3, InventoryUtils.createItem(Material.RECOVERY_COMPASS, "&3Random Teleport", "", "&9[Echo Mod]", LONG_LINE_LORE, "&7Teleport to a random online player.", LONG_LINE_LORE));
        player.getInventory().setItem(7, InventoryUtils.createItem(Material.SKULL_BANNER_PATTERN, "&3Online Staff", "", "&9[Echo Mod]", LONG_LINE_LORE, "&7Check view staff is online.", LONG_LINE_LORE));
        player.getInventory().setItem(8, InventoryUtils.createItem(Material.GRAY_DYE, "&3Disable Vanish", "", "&9[Echo Mod]", LONG_LINE_LORE, "&7Exit vanish mode and become visible.", LONG_LINE_LORE));

        player.setGameMode(GameMode.SURVIVAL);

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(new Vector(0, 1, 0));

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', AdventureUtils.parseToLegacy("<gradient:#FF3131:#FF3131>✘</gradient>") + ChatColor.WHITE + " " + player.getName() + ChatColor.GRAY + " ["+(Bukkit.getOnlinePlayers().size()-1)+"/500]"));
    }

    public void STAFF_MODE_TYPE_EXIT(Player player){
        staffModePlayers.remove(player);
        ActionBarService.playerStates.put(player.getUniqueId(), ActionBarService.PlayerState.DEFAULT);
        InventorySaverUtils.restorePlayerInventory(player);
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {player.performCommand("v");}
    }
}
