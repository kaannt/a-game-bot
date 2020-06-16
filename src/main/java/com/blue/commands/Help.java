package com.blue.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Color;

import java.util.Arrays;

import com.blue.api.DefaultCommand;
import com.blue.Main;

public class Help extends DefaultCommand {
    public Help() {
        super(Arrays.asList("help"), "displays menu for all commands");
    }
     
    @Override
    public void execute(MessageCreateEvent event) {
        event.getMessage().getChannel().block()
                .createEmbed(spec -> 
                Main.commands.forEach(command -> 
                spec.addField(command.getNames().get(0), command.getShorthelp(), false)
                .setColor(Color.CYAN)
                .setTitle("All Commands:")))
                .block();
    }
}
