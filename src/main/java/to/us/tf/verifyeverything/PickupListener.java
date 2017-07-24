package to.us.tf.verifyeverything;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created on 7/24/2017.
 *
 * The obvious
 *
 * @author RoboMWM
 */
public class PickupListener implements Listener
{
    JavaPlugin instance;

    PickupListener(JavaPlugin plugin)
    {
        instance = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPickup(PlayerPickupItemEvent event)
    {
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        ItemStack itemStack = event.getItem().getItemStack();

        int itemAmount = itemStack.getAmount();
        instance.getLogger().info(String.valueOf(itemAmount));
        int inventoryAmount = getAmount(playerInventory, itemStack);
        int totalOughtToHave = itemAmount + inventoryAmount;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                int newAmount = getAmount(event.getPlayer().getInventory(), itemStack);
                if (newAmount != (totalOughtToHave))
                {
                    instance.getLogger().warning("Discrepency detected in amount of items picked up (potentially duplicating items?)");
                    instance.getLogger().warning("Player: " + event.getPlayer().getName()
                            + " Dropped item: " + itemStack.getType().name()
                            + " Dropped itemstack amount: " + itemAmount
                            + " Previous player amount of item: " + inventoryAmount
                            + " New player amount of item: " + newAmount);
                    //TODO: config/command option
                    int failedToRemove = lowerAmount(event.getPlayer(), itemStack, newAmount - (itemAmount + inventoryAmount));
                    if (failedToRemove > 0)
                        instance.getLogger().warning("Failed to remove " + failedToRemove + "items.");
                }
            }
        }.runTaskLater(instance, 2L); //Which means we should log-only
    }

    private int getAmount(PlayerInventory inventory, ItemStack itemStack)
    {
        int amount = 0;
        for (ItemStack item : inventory)
        {
            if (item == null)
                continue;
            if (item.isSimilar(itemStack))
                amount += item.getAmount();
        }
        return amount;
    }

    private int lowerAmount(Player player, ItemStack itemStack, int amountToRemove)
    {
        for (ItemStack item : player.getInventory())
        {
            if (item.getMaxStackSize() == 1 && item.getAmount() > 1)
            {
                amountToRemove -= item.getAmount() - 1;
                item.setAmount(1);
            }

            while (item.getAmount() > 1)
            {
                item.setAmount(item.getAmount() - 1);
                amountToRemove--;
                if (amountToRemove <= 0)
                    return amountToRemove;
            }
        }
        return amountToRemove;
    }
}
