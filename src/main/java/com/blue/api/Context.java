package com.blue.api;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import reactor.core.publisher.Mono;


public class Context {
    private Mono<MessageChannel> channel;
    private Message message;
    private String content;

    public Context(String content, Mono<MessageChannel> channel, Message message) {
        this.content = content;
        this.channel = channel;
        this.message = message;
    }

    public Mono<MessageChannel> channel() {
        return channel;
    }

    public Message message() {
        return message;
    }
}
