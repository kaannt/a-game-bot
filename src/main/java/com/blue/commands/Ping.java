package com.blue.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

import com.blue.api.DefaultCommand;

import java.util.Arrays;
import java.util.List;
import java.awt.Color;


public class Ping extends DefaultCommand {
    public Ping() {
        super(Arrays.asList("ping"), "check connection speeds");
    }

    @Override
    public void execute(MessageCreateEvent event) {
        if (event.getMessage().getContent().replaceFirst("!ping ", "").equals("help")) {
            event.getMessage().getChannel().block()
                    .createEmbed(spec -> spec.setTitle("Ping: ")
                                         .setDescription(getShorthelp())
                                         .setColor(Color.BLUE))
                                         .block();
        } else { 
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
}
