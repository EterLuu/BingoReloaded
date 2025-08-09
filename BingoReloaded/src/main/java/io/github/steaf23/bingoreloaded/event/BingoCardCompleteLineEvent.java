package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;

import java.util.Set;

public class BingoCardCompleteLineEvent extends BingoEvent {
    private final Set<Integer> completedLines;
    private final BingoTeam team;

    public BingoCardCompleteLineEvent(BingoSession session, BingoTeam t, Set<Integer> c) {
        super(session);
        this.completedLines = c;
        this.team = t;
    }

    public Set<Integer> getCompletedLines() {
        return completedLines;
    }

    public BingoTeam getTeam() {
        return team;
    }
}
