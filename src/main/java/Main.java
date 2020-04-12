package com.blue;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final DiscordClient client = new DiscordClientBuilder(getKey()).build();
        client.login().block();
    }

    private static String getKey() {
        try {
            return Files.readAllLines(new File("key.txt").toPath()).get(0).trim();
        } catch(IOException e) {
            throw new IllegalStateException("key.txt needs to be here");
        }
    }
}
