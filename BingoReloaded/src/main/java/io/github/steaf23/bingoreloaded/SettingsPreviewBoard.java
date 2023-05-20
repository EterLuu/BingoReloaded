package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.util.InfoScoreboard;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.SmallCaps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

public class SettingsPreviewBoard extends InfoScoreboard
{
    private static final BaseComponent[] TITLE = new ComponentBuilder("")
            .append("Bingo", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA).bold(true)
            .append("Ⓡeloaded", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).italic(true)
            .append("", ComponentBuilder.FormatRetention.NONE).create();

    public SettingsPreviewBoard()
    {
        super(new TextComponent(TITLE).toLegacyText(), Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
       showSettings(event.getNewSettings());
    }

    public void showSettings(BingoSettings settings)
    {
        clearDisplay();
        setLineText(0, " ");
        setLineText(1, ChatColor.RED + "Waiting for the game to start...");
        setLineText(2, " ");
        setLineText(3,  ChatColor.BOLD + "Gamemode:");
        setLineText(4, " - " + settings.mode().name + " " + settings.size().size + "x" + settings.size().size);
        setLineText(5, ChatColor.BOLD + "Kit:");
        setLineText(6, " - " + settings.kit().displayName);
        setLineText(7, ChatColor.BOLD + "Effects:");
        int idx = 8;
        if (settings.effects().size() == 0)
        {
            setLineText(idx, " - " + ChatColor.GRAY + " None");
            idx ++;
        }
        else
        {
            for (var effect : settings.effects())
            {
                setLineText(idx, "     - " + ChatColor.GRAY + effect.name);
                idx++;
            }
        }

        setLineText(idx, ChatColor.BOLD + "Team Size:");
        setLineText(idx + 1, " - " + settings.maxTeamSize() + " players");
        if (settings.enableCountdown())
        {
            setLineText(idx + 2, ChatColor.BOLD + "Time:");
            setLineText(idx + 3, " - " + settings.countdownDuration() + " minutes");
        }
    }
}
