package com.blue.commands;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.time.Duration;
import java.util.Random;

import org.jsoup.Jsoup;

import com.blue.api.DefaultCommand;
import com.blue.api.Context;

public class Xkcd extends DefaultCommand {
    private final HttpClient client;
    private static final String base = "https://xkcd.com/";
    private static final int TOTAL_COMICS = 2326;

    public Xkcd() {
        super("xkcd", "embeds a random xkcd comic");
        client = HttpClient.newHttpClient();
    }

    @Override
    public void execute(Context ctx) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(base + (ctx.getArgs().length > 0 ? ctx.getArgs()[0] : new Random().nextInt(TOTAL_COMICS)) + "/"))
            .timeout(Duration.ofMinutes(1))
            .GET()
            .build();

        client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(body -> {
                String comicImg = Jsoup.parse(body)
                    .select("img")
                    .get(2)
                    .attr("src");

                ctx.channel()
                   .flatMap(channel -> channel.createMessage("https:" + comicImg))
                   .subscribe();
            });
    }
}
