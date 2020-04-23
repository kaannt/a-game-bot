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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Stream;
import com.blue.Command;

public class Main {
    public static void main(String[] args) {
        final DiscordClient client = DiscordClientBuilder.create(getKey()).build();        
        GatewayDiscordClient gateway = client.login().block();

        gateway.on(ReadyEvent.class)
                .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));

        gateway.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(event -> {
                    for (String key : commands.keySet()) {
                        if (event.getMessage().getContent().startsWith('!' + key)) {
                            commands.get(key).execute(event);
                            break;
                        }
                    }
                });

        gateway.onDisconnect().block();

    }
    
    // keys are the names of the command, and the value is a command instance
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("ping", new Command() {
            @Override
            public void execute(MessageCreateEvent event) {
                event.getMessage().getChannel().block()
                        .createMessage("Pong!").block();
            }
        });
    }

    private static String getKey() {
        try {
            return Files.readAllLines(new File("key.txt").toPath()).get(0).trim();
        } catch(IOException e) {
            throw new IllegalStateException("key.txt needs to be here");
        }
    }
}
