package io.github.steaf23.brpartymode;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoReloadedExtension;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.command.BingoTabCompleter;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.gui.base.MenuEventListener;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public final class BRPartyMode extends BingoReloadedExtension
{
    public static final String NAME = "BingoReloadedPartyMode";

    private BingoSession session;
    private BingoEventListener listener;
    private MenuEventListener menuManager;

    @Override
    public void onEnable()
    {
        this.core.onEnable();

        //TODO: replace with world_name config option
        this.session = new BingoSession("world", core.config());
        this.listener = new BingoEventListener(world ->
            BingoReloadedCore.getWorldNameOfDimension(world).equals(session.worldName) ? session : null
        , false, false);
        this.menuManager = new MenuEventListener(inventoryView -> {
            String worldName = BingoReloadedCore.getWorldNameOfDimension(inventoryView.getPlayer().getWorld());
            return worldName.equals(session.worldName);
        });

        core.registerCommand("bingo", new PartyBingoCommand(core.config(), session), new BingoTabCompleter());

        Bukkit.getPluginManager().registerEvents(listener, this);
        Bukkit.getPluginManager().registerEvents(menuManager, this);

        Message.log(ChatColor.GREEN + "Enabled " + this.getName());
    }

    @Override
    public void onDisable()
    {
        this.core.onDisable();
        HandlerList.unregisterAll(listener);
        HandlerList.unregisterAll(menuManager);

        Bukkit.getLogger().info(org.bukkit.ChatColor.RED + "Disabled " + this.getName());
    }
}
