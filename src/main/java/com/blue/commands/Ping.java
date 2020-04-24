package com.blue.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

import com.blue.Command;

import java.util.Arrays;
import java.util.List;


public class Ping implements Command {
    private List<String> names;

    public Ping() {
        names = Arrays.asList("ping");
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        // we send a message which if done in a faster time
        // will mean our ping is lower
        Message m = event.getMessage().getChannel().block()
                            .createMessage("Pinging...").block();

        if (m == null) {
            throw new IllegalStateException("Message cannot be null");
        }

        // get the difference in time between when we were able to send a message
        // and when the user had originally typed "!ping"
        long ms = m.getTimestamp().toEpochMilli() - event.getMessage().getTimestamp().toEpochMilli();
        m.edit(spec -> spec.setContent("Ping: " + ms + "ms")).block();
    }
}
