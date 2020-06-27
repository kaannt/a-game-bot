package com.blue;

// packages from discord4j
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.voice.AudioProvider;
import discord4j.core.object.entity.Member;
import discord4j.core.object.VoiceState;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.GatewayDiscordClient;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Optional;

import com.blue.commands.*;
import com.blue.api.Command;
import com.blue.api.Context;

public class Main {
    public static void main(String[] args) {
        final DiscordClient client = DiscordClientBuilder.create(getKey()).build();        
        final GatewayDiscordClient gateway = client.login().block();

        gateway.on(ReadyEvent.class)
               .subscribe(ready -> {
                   System.out.println("Logged in as " + ready.getSelf().getUsername());
               });

        gateway.getEventDispatcher()
               .on(MessageCreateEvent.class)
               .subscribe(event -> {
                   String message = event.getMessage().getContent();

                   if (event.getMessage().getAuthor().get().isBot() || 
                       !Util.getPrefix(message) && 
                       !Util.listGetter(commands, c -> c.getName()).contains(message.substring(1)))
                       return;
                   
                   Context ctx = new Context(message, event.getMessage().getChannel(), event.getMessage());
                   Optional<Command> cmd = commands.stream()
                                                   .filter(c -> c.getName().equals(message.substring(1)))
                                                   .findFirst();
                   if (!cmd.isPresent())
                       return;
                   
                   cmd.get().execute(ctx);
               });

        gateway.onDisconnect().block();

    }
    
    // keys are the names of the command, and the value is a command instance
    public static final List<Command> commands = new LinkedList<>();

    static {
        commands.add(new Ping());
        commands.add(new Help());
    }

    private static String getKey() {
        try {
            return Files.readAllLines(new File("key.txt").toPath()).get(0).trim();
        } catch(IOException e) {
            throw new IllegalStateException("key.txt needs to be here");
        }
    }
}
