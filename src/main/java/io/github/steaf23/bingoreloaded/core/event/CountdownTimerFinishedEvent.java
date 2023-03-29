package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.BingoSession;
import org.bukkit.event.HandlerList;

public class CountdownTimerFinishedEvent extends BingoEvent
{
    private static final HandlerList HANDLERS = new HandlerList();

    public CountdownTimerFinishedEvent(BingoSession session)
    {
        super(session);
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}