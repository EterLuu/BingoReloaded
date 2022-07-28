package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract class for Inventory based GUIs.
 * Delegates click events to a separate method that Inheritors must override.
 */
public abstract class AbstractGUIInventory
{
    /**
     * Event delegate to handle custom click behaviour for inventory items
     * @param event The event that gets fired when an item in the inventory was clicked.
     * @param slotClicked The slot that was clicked on by the player.
     *                    Slot can be null.
     * @param player The player that clicked on the item.
     */
    public abstract void delegateClick(final InventoryClickEvent event, int slotClicked, Player player);

    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;

    protected Inventory inventory = null;
    private final AbstractGUIInventory parent;

    public AbstractGUIInventory(int size, String title, AbstractGUIInventory parent)
    {
        this.parent = parent;

        inventory = Bukkit.createInventory(new GUIHolder(), size, BingoReloaded.PRINT_PREFIX + ChatColor.DARK_RED + title);
        UIManager.addInventory(this);
    }

    public void handleClick(final InventoryClickEvent event)
    {
        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);

            if (event.getRawSlot() < event.getInventory().getSize() && event.getRawSlot() >= 0)
            {
                delegateClick(event, event.getSlot(), (Player)event.getWhoClicked());
            }
        }
    }

    public void handleDrag(final InventoryDragEvent event)
    {
        if (inventory == null) return;
        if (event.getInventory() == inventory)
        {
            event.setCancelled(true);
        }
    }

    public void handleOpen(final InventoryOpenEvent event)
    {

    }

    public void handleClose(final InventoryCloseEvent event)
    {

    }

    protected void fillOptions(InventoryItem[] options)
    {
        for (InventoryItem option : options)
        {
            addOption(option);
        }
    }

    /**
     * Adds items into the specified slot. If slot is -1, it will use the next available slot or merge using Inventory#addItem().
     * @param option menu item to put in the inventory
     */
    protected void addOption(InventoryItem option)
    {
        if (option.getSlot() == -1)
        {
            inventory.addItem(option);
        }
        else
        {
            inventory.setItem(option.getSlot(), option);
        }
    }

    public InventoryItem getOption(int slot)
    {
        ItemStack stack = inventory.getItem(slot);
        if (stack == null || stack.getType().isAir()) return null;

        return new InventoryItem(slot, inventory.getItem(slot));
    }

    public void open(HumanEntity player)
    {
        player.openInventory(inventory);
    }

    public void close(HumanEntity player)
    {
        if (parent != null)
            parent.open(player);
        else
            player.closeInventory();
    }

    public GUIHolder getHolder()
    {
        return (GUIHolder)inventory.getHolder();
    }
}
