package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.event.BingoCardCompleteLineEvent;
import io.github.steaf23.bingoreloaded.gui.inventory.card.CardMenu;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompleteTaskCard extends TaskCard
{
    private final int completeGoal;

    public CompleteTaskCard(@NotNull CardMenu menu, CardSize size, int completeGoal)
    {
        super(menu, size);
        menu.setInfo(BingoMessage.INFO_COMPLETE_NAME.asPhrase(),
                BingoMessage.INFO_COMPLETE_DESC.asMultiline());
        this.completeGoal = completeGoal;
    }

    @Override
    public boolean hasTeamWon(@NotNull BingoTeam team) {
        List<GameTask> allTasks = getTasks();
        Set<Integer> completedLines = new HashSet<>();

        int row = 0;
        int col = 5;
        //check for rows and columns
        for (int y = 0; y < size.size; y++) {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.size; x++) {
                int indexRow = size.size * y + x;
                if (!allTasks.get(indexRow).isCompletedByTeam(team)) {
                    completedRow = false;
                }

                int indexCol = size.size * x + y;
                if (!allTasks.get(indexCol).isCompletedByTeam(team)) {
                    completedCol = false;
                }
            }
            if (completedRow) {
                completedLines.add(row);
            }
            if (completedCol) {
                completedLines.add(col);
            }
            row++;
            col++;
        }

        // check for diagonals
        boolean completedDiagonal1 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.size + 1) {
            if (!allTasks.get(idx).isCompletedByTeam(team)) {
                completedDiagonal1 = false;
                break;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.size - 1) {
            if (idx != 0 && idx != size.fullCardSize - 1) {
                if (!allTasks.get(idx).isCompletedByTeam(team)) {
                    completedDiagonal2 = false;
                    break;
                }
            }
        }
        if(completedDiagonal1) {
            completedLines.add(10);
        }
        if(completedDiagonal2) {
            completedLines.add(11);
        }

        var event = new BingoCardCompleteLineEvent(null, team, completedLines);
        Bukkit.getPluginManager().callEvent(event);

        return getCompleteCount(team) == Math.min(completeGoal, size.fullCardSize);
    }

    @Override
    public TaskCard copy(@Nullable Component alternateTitle) {
        CompleteTaskCard card = new CompleteTaskCard(menu.copy(alternateTitle), this.size, this.completeGoal);
        List<GameTask> newTasks = new ArrayList<>();
        for (var t : getTasks())
        {
            newTasks.add(t.copy());
        }
        card.setTasks(newTasks);
        return card;
    }

    @Override
    public boolean canGenerateSeparateCards() {
        return true;
    }
}
