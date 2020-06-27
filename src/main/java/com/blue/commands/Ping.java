package com.blue.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;

import reactor.core.publisher.Mono;

import com.blue.api.DefaultCommand;
import com.blue.api.Context;

import java.util.Arrays;
import java.util.List;


public class Ping extends DefaultCommand {
    public Ping() {
        super("ping", "check connection speeds");
    }

    @Override
    public void execute(Context ctx) {
        // we send a message which if done in a faster time
        // will mean our ping is lower
        Message m = ctx.channel().block()
                       .createMessage("Pinging...")
                       .block();


        if (m == null) {
            throw new IllegalStateException("Message cannot be null");
        }

        // get the difference in time between when we were able to send a message
        // and when the user had originally typed "!ping"
        long ms = m.getTimestamp().toEpochMilli() - ctx.message().getTimestamp().toEpochMilli();
        m.edit(spec -> spec.setContent("Ping: " + ms + "ms")).block();
    }
}
