package com.blue;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.List;

public interface Command {
    /**
     *@return all the command names associated with this command
     */
    List<String> getNames();
    /**
     *
     *
     *@param event whenever a user types something it should be handled
     */
    void execute(MessageCreateEvent event);
}
