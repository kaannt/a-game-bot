package com.blue.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Color;

import reactor.core.publisher.Mono;

import java.util.Arrays;

import com.blue.api.DefaultCommand;
import com.blue.api.Context;
import com.blue.Main;

public class Help extends DefaultCommand {
    public Help() {
        super("help", "displays menu for all commands");
    }
     
    @Override
    public void execute(Context ctx) {
        ctx.message()
           .getChannel()
           .flatMap(channel -> channel.createEmbed(spec -> {
               Main.commands.forEach(command -> 
                   spec.addField(command.getName(), command.getShorthelp(), false)
                       .setColor(Color.CYAN)
                       .setTitle("All Commands:"));
           }))
           .subscribe();
    }
}
