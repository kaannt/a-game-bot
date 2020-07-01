package com.blue.api;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import reactor.core.publisher.Mono;

import java.util.Optional;


public class Context {
    private final Mono<MessageChannel> channel;
    private final Message message;
    private final String[] args;
    private final Optional<Member> member;

    public Context(String[] args, Mono<MessageChannel> channel, Message message, Optional<Member> member) {
        this.args = args;
        this.channel = channel;
        this.message = message;
        this.member = member;
    }

    public void sendPublic(String message) {
        channel.flatMap(ch -> ch.createMessage(message))
                .block();
    }

    public String[] getArgs() {
        return args;
    }

    public Mono<MessageChannel> channel() {
        return channel;
    }

    public Optional<Member> member() {
        return member;
    }

    public Message message() {
        return message;
    }
}
