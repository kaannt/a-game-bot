package com.blue;

// packages from discord4j
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.voice.AudioProvider;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.VoiceChannel;
import discord4j.core.object.VoiceState;

// lavaplayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import com.blue.Command;
import com.blue.LavaPlayerAudioProvider;

public class Main {
    public static void main(String[] args) {
        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
		final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		// This is an optimization stragety that Discord4j can utilize. It is not important to understand
		playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		// Allow playerManager to parse remote sources like YouTube links
		AudioSourceManagers.registerRemoteSources(playerManager);
		// Create an AudioPlayer so Discord4j can receive audio data
		final AudioPlayer player = playerManager.createPlayer();
		// We will be making LavaPlayer AudioProvider in the next step
		AudioProvider provider = new LavaPlayerAudioProvider(player);

		
		commands.put("join", event -> {
			final Member member = event.getMember().orElse(null);
			if (member != null) {
				final VoiceState voiceState = member.getVoiceState().block();
				if (voiceState != null) {
				    final VoiceChannel channel = voiceState.getChannel().block();
				    if (channel != null) {
				        // join returns a VoiceConnection which would be required if we were
				        // adding disconnection features, but for now we are just ignoring it.
				        channel.join(spec -> spec.setProvider(provider)).block();
				    }
				}
			}
		});
		
		final TrackScheduler scheduler = new TrackScheduler(player);
		commands.put("play", event -> {
			final String content = event.getMessage().getContent().get();
			final List<String> command = Arrays.asList(content.split(" "));
			playerManager.loadItem(command.get(1), scheduler);
		});

        final DiscordClient client = DiscordClientBuilder.create(getKey()).build();

        client.getEventDispatcher().on(MessageCreateEvent.class)
        // subscribe is like block, in that it will *request* for
        // action to be done, but instead of blocking the thread,
        // waiting for it to finish, it will just execute the 
        // results asynchronously.
        .subscribe(event -> {
            final String content = event.getMessage().getContent().orElse("");
            for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                // We will be using ! as out "prefix" to any command in the system.
                if (content.startsWith('!' + entry.getKey())) {
                    entry.getValue().execute(event);
                    break;
                }
            }
        });

        client.login().block();
    }
    
    // keys are the names of the command, and the value is a command instance
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("ping", event -> event.getMessage()
            .getChannel().block()
            .createMessage("Pong!").block());
    }

    private static String getKey() {
        try {
            return Files.readAllLines(new File("key.txt").toPath()).get(0).trim();
        } catch(IOException e) {
            throw new IllegalStateException("key.txt needs to be here");
        }
    }
}
