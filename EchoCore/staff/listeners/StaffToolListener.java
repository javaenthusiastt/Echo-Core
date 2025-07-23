package sorryplspls.EchoCore.staff.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sorryplspls.EchoCore.staff.commands.StaffCommand;
import sorryplspls.EchoCore.utils.InventoryUtils;
import sorryplspls.EchoCore.utils.StaffUtils;
import java.util.List;
import java.util.stream.Collectors;

public class StaffToolListener implements Listener {

    private String FAILED_TO_PERFORM_ACTION_IN_STAFF = "&c&l(!) &cYou can't perform this action in STAFF MODE.";

    private final StaffCommand staffCommand;

    public StaffToolListener(StaffCommand staffCommand) {
        this.staffCommand = staffCommand;
    }

    private boolean isPlayerInStaffMode(Player player) {
        return staffCommand.staffModePlayers.contains(player);
    }

    // Util
    // Util
    // Util
    // Util
    // Util

    private void checkForWE(Player player){
        if(!(player.isOp())){
            StaffUtils.sendMessageInStaffFormat(player, "&7You can't use this tool due to permissions lacking.");
        }
    }
    private void randomTeleport(Player player) {
        List<Player> candidates = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            StaffUtils.sendMessageInStaffFormat(player, "&7Found no one to teleport to.");
            return;
        }

        Player target = candidates.get((int) (Math.random() * candidates.size()));

        player.teleport(target);
        StaffUtils.sendMessageInStaffFormat(player, "&7You teleported to: &f" + target.getName() + "&7.");
    }
    private void sendOnlineStaffList(Player player) {
        List<String> staffOnline = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("staff"))
                .map(Player::getName)
                .collect(Collectors.toList());

        if (staffOnline.isEmpty()) {
            StaffUtils.sendMessageInStaffFormat(player, "&7No staff members are currently online.");
            return;
        }

        StaffUtils.sendMessageInStaffFormat(player, "&7Online staff members (&b" + staffOnline.size() + "&7):");
        for (String name : staffOnline) {
            player.sendMessage(ChatColor.GRAY + " - " + ChatColor.AQUA + name);
        }
    }
    private void checkForVanish(Player player, String s){
        if(s.equalsIgnoreCase("enable vanish")){
            player.performCommand("v");
            player.getInventory().setItem(8, InventoryUtils.createItem(Material.GRAY_DYE, "&3Disable Vanish", "", "&9[Echo Mod]", staffCommand.LONG_LINE_LORE, "&7Exit vanish mode and become visible.", staffCommand.LONG_LINE_LORE));
        }else if (s.equalsIgnoreCase("disable vanish")){
            player.performCommand("v");
            player.getInventory().setItem(8, InventoryUtils.createItem(Material.LIME_DYE, "&3Enable Vanish", "", "&9[Echo Mod]", staffCommand.LONG_LINE_LORE, "&7Enter vanish mode and become invisible.", staffCommand.LONG_LINE_LORE));
        }
    }

    // Util
    // Util
    // Util
    // Util
    // Util

    // Main Listeners
    // Main Listeners
    // Main Listeners
    // Main Listeners
    // Main Listeners

    @EventHandler
    public void onStaffItemUse(PlayerInteractEvent event) {
        if (!isPlayerInStaffMode(event.getPlayer())) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String displayName = ChatColor.stripColor(meta.getDisplayName());

        Player player = event.getPlayer();

        switch (displayName.toLowerCase()) {
            case "wand (we)" -> {
                checkForWE(player);
            }

            case "random teleport" -> {
                event.setCancelled(true);
                randomTeleport(player);
            }

            case "online staff" -> {
                event.setCancelled(true);
                sendOnlineStaffList(player);
            }

            case "disable vanish" -> {
                event.setCancelled(true);
                checkForVanish(player, displayName);
            }

            case "enable vanish" -> {
                event.setCancelled(true);
                checkForVanish(player, displayName);
            }
        }
    }

    @EventHandler
    public void onStaffInspectPlayer(PlayerInteractEntityEvent event) {
        if (!isPlayerInStaffMode(event.getPlayer())) return;
        if (!(event.getRightClicked() instanceof Player target)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player staff = event.getPlayer();

        if (staff.getInventory().getItemInMainHand().getItemMeta() == null || !staff.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;
        String displayName = ChatColor.stripColor(staff.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).toLowerCase();

        switch (displayName) {
            case "inspection book" -> {
                event.setCancelled(true);
                staff.openInventory(target.getInventory());
                StaffUtils.sendMessageInStaffFormat(staff, "&7You are now inspecting &f" + target.getName() + "&7's inventory.");
            }

            case "freeze player" -> {
                staff.performCommand("vulcan freeze " + target.getName());
                event.setCancelled(true);
            }
        }
    }

    // Main Listeners
    // Main Listeners
    // Main Listeners
    // Main Listeners
    // Main Listeners

    // Default Listeners
    // Default Listeners
    // Default Listeners
    // Default Listeners
    // Default Listeners
    // Default Listeners

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isPlayerInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
            StaffUtils.sendMessageInStaffFormat(event.getPlayer(), FAILED_TO_PERFORM_ACTION_IN_STAFF);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isPlayerInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
            StaffUtils.sendMessageInStaffFormat(event.getPlayer(), FAILED_TO_PERFORM_ACTION_IN_STAFF);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isPlayerInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
            StaffUtils.sendMessageInStaffFormat(event.getPlayer(), FAILED_TO_PERFORM_ACTION_IN_STAFF);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && isPlayerInStaffMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (isPlayerInStaffMode(player)) {
                event.setCancelled(true);
                player.setItemOnCursor(null);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && isPlayerInStaffMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && isPlayerInStaffMode(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && isPlayerInStaffMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player && isPlayerInStaffMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if(isPlayerInStaffMode(event.getPlayer())){
            staffCommand.STAFF_MODE_TYPE_EXIT(event.getPlayer());
        }
    }

    // Default Listeners
    // Default Listeners
    // Default Listeners
    // Default Listeners
    // Default Listeners
    // Default Listeners
}
