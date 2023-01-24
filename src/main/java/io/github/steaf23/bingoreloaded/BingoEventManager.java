package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.gui.UIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class BingoEventManager implements Listener
{
    public BingoEventManager()
    {
        BingoReloaded.registerListener(this);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        String worldName = GameWorldManager.getWorldName(event.getWhoClicked().getWorld());
        if (GameWorldManager.get().doesGameWorldExist(worldName))
        {
            UIManager.onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        String worldName = GameWorldManager.getWorldName(event.getWhoClicked().getWorld());
        if (GameWorldManager.get().doesGameWorldExist(worldName))
        {
            UIManager.onInventoryDrag(event);
        }
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event)
    {
        String worldName = GameWorldManager.getWorldName(event.getPlayer().getWorld());
        if (GameWorldManager.get().doesGameWorldExist(worldName))
        {
            UIManager.onInventoryOpen(event);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event)
    {
        String worldName = GameWorldManager.getWorldName(event.getPlayer().getWorld());
        if (GameWorldManager.get().doesGameWorldExist(worldName))
        {
            UIManager.onInventoryClose(event);
        }
    }
}