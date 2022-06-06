package me.steven.bingoreloaded.player;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.data.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TeamChat implements Listener, CommandExecutor
{
    private final List<Player> enabledPlayers;

    private final TeamManager teamManager;

    public TeamChat(TeamManager teamManager)
    {
        this.teamManager = teamManager;
        this.enabledPlayers = new ArrayList<>();

        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        if (plugin == null) return;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerSendMessage(final AsyncPlayerChatEvent event)
    {
        if (!enabledPlayers.contains(event.getPlayer())) return;

        BingoTeam team = teamManager.getTeamOfPlayer(event.getPlayer());
        if (team == null) return;

        String message = event.getMessage();
        sendMessage(team, event.getPlayer(), message);

        event.setCancelled(true);

    }

    public void sendMessage(BingoTeam team, Player player, String message)
    {
        for (String entry : team.team.getEntries())
        {
            Player member = Bukkit.getPlayer(entry);
            if (member == null) continue;

            if (!member.isOnline()) continue;

            member.sendMessage(ChatColor.DARK_RED + "[" + team.getColor() + ChatColor.BOLD + team.getName() + ChatColor.DARK_RED + "]" +
                    ChatColor.RESET  + "<" + player.getName() + "> " + message);
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (commandSender instanceof Player p)
        {
            if (!teamManager.getParticipants().contains(p))
            {
                MessageSender.send("game.team.chat_off", p, null, ChatColor.RED);
                return false;
            }

            if (enabledPlayers.contains(p))
            {
                enabledPlayers.remove(p);
                MessageSender.send("game.team.chat_off", p, List.of("/btc"), ChatColor.RED);
            }
            else
            {
                enabledPlayers.add(p);
                MessageSender.send("game.team.chat_on", p, List.of("/btc"), ChatColor.RED);
            }
        }
        return false;
    }
}