package sorryplspls.EchoCore.catamines;
import com.sk89q.worldedit.math.BlockVector3;
import de.c4t4lysm.catamines.schedulers.MineManager;
import de.c4t4lysm.catamines.utils.mine.components.CataMineBlock;
import de.c4t4lysm.catamines.utils.mine.components.CataMineLootItem;
import de.c4t4lysm.catamines.utils.mine.mines.CuboidCataMine;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import sorryplspls.EchoCore.utils.AdventureUtils;
import sorryplspls.EchoCore.Main;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class CataMinesMineSystem implements Listener {

    private static final Map<Material, Material> BLOCK_CONVERSION_MAP = new HashMap<>();

    static {
        BLOCK_CONVERSION_MAP.put(Material.STONE, Material.COBBLESTONE);
        BLOCK_CONVERSION_MAP.put(Material.COAL_ORE, Material.COAL);
        BLOCK_CONVERSION_MAP.put(Material.COPPER_ORE, Material.RAW_COPPER);
        BLOCK_CONVERSION_MAP.put(Material.IRON_ORE, Material.RAW_IRON);
        BLOCK_CONVERSION_MAP.put(Material.EMERALD_ORE, Material.EMERALD);
        BLOCK_CONVERSION_MAP.put(Material.DIAMOND_ORE, Material.DIAMOND);
        BLOCK_CONVERSION_MAP.put(Material.LAPIS_ORE, Material.LAPIS_LAZULI);
        BLOCK_CONVERSION_MAP.put(Material.REDSTONE_ORE, Material.REDSTONE);
        BLOCK_CONVERSION_MAP.put(Material.GOLD_ORE, Material.RAW_GOLD);
        BLOCK_CONVERSION_MAP.put(Material.NETHER_QUARTZ_ORE, Material.QUARTZ);
    }

    private boolean isInsideCataMine(Location loc) {
        BlockVector3 vec = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        return MineManager.getInstance().getMines().stream()
                .anyMatch(mine -> mine.getRegion().contains(vec));
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Player player = event.getPlayer();
        Location blockLoc = event.getBlock().getLocation();
        BlockVector3 blockVec = BlockVector3.at(blockLoc.getBlockX(), blockLoc.getBlockY(), blockLoc.getBlockZ());

        Optional<CuboidCataMine> foundMineOpt = findContainingMine(blockVec);

        if (foundMineOpt.isEmpty()){
            return;
        }

        CuboidCataMine foundMine = foundMineOpt.get();

        event.setDropItems(false);
        event.setExpToDrop(0);

        Optional<CataMineBlock> targetBlockOpt = findMatchingBlock(foundMine, event.getBlock().getBlockData().getMaterial(), event.getBlock().getBlockData());
        if (targetBlockOpt.isEmpty()) return;

        int requiredEfficiency = foundMine.getMinEfficiencyLvl();
        int playerEfficiency = getEfficiencyLevel(player);

        if (playerEfficiency < requiredEfficiency) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Your tool is too weak for this mine!");
            player.playSound(player, Sound.BLOCK_ANVIL_LAND, 1f, 0.6f);
            return;
        }

        CataMineBlock targetBlock = targetBlockOpt.get();
        double multiplier = sorryplspls.EchoCore.EchoBoxCore.MultiplierManager.getMiningMultiplier(player);

        if (targetBlock.getLootTable().isEmpty()) {
            handleFallbackDrop(event, player, multiplier);
            return;
        }

        for (CataMineLootItem lootItem : targetBlock.getLootTable()) {
            if (Math.random() > lootItem.getChance()) continue;

            ItemStack drop = lootItem.getItem().clone();

            int b = drop.getAmount();
            int m = (int) Math.max(1, Math.round(b * multiplier));
            int f = getFortuneLevel(player);
            int e = getFortuneFormula(f);
            int a = m + e;

            drop.setAmount(a);
            give(player, drop);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakCleanup(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            for (var entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
                if (entity instanceof Item droppedItem) {
                    droppedItem.remove();
                }
            }
        }, 1L);
    }

    private Optional<CuboidCataMine> findContainingMine(BlockVector3 blockVec) {
        MineManager mineManager = MineManager.getInstance();
        return mineManager.getMines().stream()
                .filter(mine -> mine.getRegion().contains(blockVec))
                .findFirst();
    }

    private Optional<CataMineBlock> findMatchingBlock(CuboidCataMine mine, Material brokenMaterial, org.bukkit.block.data.BlockData brokenBlockData) {
        return mine.getBlocks().stream()
                .filter(cmb -> cmb.getBlockData().getMaterial() == brokenBlockData.getMaterial())
                .findFirst();
    }

    private void handleFallbackDrop(BlockBreakEvent event, Player player, double multiplier) {
        Material original = event.getBlock().getType();
        Material converted = BLOCK_CONVERSION_MAP.getOrDefault(original, original);
        ItemStack fallbackDrop = new ItemStack(converted, 1);
        int baseAmount = fallbackDrop.getAmount();
        int multipliedAmount = (int) Math.max(1, Math.round(baseAmount * multiplier));
        int fortuneLevel = getFortuneLevel(player);
        int extraDrops = getFortuneFormula(fortuneLevel);
        int finalAmount = multipliedAmount + extraDrops;
        fallbackDrop.setAmount(finalAmount);
        give(player, fallbackDrop);
    }

    private void give(Player player, ItemStack item) {
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        int leftoverAmount = leftover.values().stream().mapToInt(ItemStack::getAmount).sum();
        int givenAmount = item.getAmount() - leftoverAmount;

        if (!leftover.isEmpty()) {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', AdventureUtils.parseToLegacy("<#DC143C>&lINVENTORY FULL!")),
                    ChatColor.translateAlternateColorCodes('&', AdventureUtils.parseToLegacy("<#DC143C>Clear up your inventory!")),
                    10, 20, 10);
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }

    private int getEfficiencyLevel(Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool != null && tool.containsEnchantment(Enchantment.DIG_SPEED)) {
            return tool.getEnchantmentLevel(Enchantment.DIG_SPEED);
        }
        return 0;
    }

    private int getFortuneLevel(Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool != null && tool.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            return tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        }
        return 0;
    }

    private int getFortuneFormula(int fortuneLevel) {
        if (fortuneLevel <= 0) return 0;
        int extra = ThreadLocalRandom.current().nextInt(fortuneLevel + 2) - 1;
        return Math.max(0, extra);
    }
}
