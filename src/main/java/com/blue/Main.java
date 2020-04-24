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

import com.blue.commands.Ping;
import com.blue.Command;

public class Main {
    public static void main(String[] args) {
        final DiscordClient client = DiscordClientBuilder.create(getKey()).build();        
        GatewayDiscordClient gateway = client.login().block();

        gateway.on(ReadyEvent.class)
                .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));

        gateway.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(event -> {
                    for (Command command : commands) {
                        if (event.getMessage().getContent().startsWith('!' + command.getNames().get(0))) {
                            command.execute(event);
                            break;
                        }
                    }
                });

        gateway.onDisconnect().block();

    }
    
    // keys are the names of the command, and the value is a command instance
    private static final List<Command> commands = new LinkedList<>();

    static {
        commands.add(new Ping());
    }

    private static String getKey() {
        try {
            return Files.readAllLines(new File("key.txt").toPath()).get(0).trim();
        } catch(IOException e) {
            throw new IllegalStateException("key.txt needs to be here");
        }
    }
}
