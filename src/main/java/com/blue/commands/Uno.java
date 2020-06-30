package com.blue.commands;

import com.blue.Util;
import com.blue.api.Context;
import com.blue.api.DefaultCommand;
import discord4j.common.util.Snowflake;

import discord4j.core.object.entity.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Uno extends DefaultCommand {
    private boolean playing;
    private final List<Player> players;

    public Uno() {
        super("uno", "play uno");
        playing = false;
        players = new ArrayList<>();
    }

    @Override
    public void execute(Context ctx) {
        String[] args = ctx.getArgs();
        //Player current;

        if (args.length == 0)
            ctx.channel()
               .flatMap(messageChannel -> messageChannel.createMessage("Usage: uno join"))
               .subscribe();
        else if ("join".equals(args[0]) && !playing) {
            players.add(new Player(ctx.member().orElseThrow()));
            ctx.channel().flatMap(ch -> ch.createMessage("added to game")).block();
        } else if ("start".equals(args[0]) && !playing && players.size() > 0) {
            playing = true;
            startGame();
            ctx.channel().flatMap(ch -> ch.createMessage("starting game")).block();
        } else if ("hand".equals(args[0]) && Util.listGetter(players, Player::member).contains(ctx.member().orElseThrow()))
            players.stream()
                    .filter(p -> p.member().equals(ctx.member().orElseThrow()))
                    .findFirst()
                    .orElseThrow()
                    .showHand();
    }

    static class Player {
        Member m;
        List<Card> hand;

        public Player(Member m) {
            this.m = m;
            hand = new ArrayList<>();
        }

        public Member member() {
            return m;
        }

        public void setHand(List<Card> hand) {
            this.hand = hand;
        }

        public void showHand() {
            m.getPrivateChannel()
                    .flatMap(ch -> ch.createMessage(hand.stream()
                            .map(Card::get)
                            .reduce((card, card2) -> card + card2)
                            .orElse("Empty hand")))
                    .block();
        }


    }

    abstract static class Card {
        Color color;

        public Card(Color color) {
            this.color = color;
        }

        abstract String get();
    }

    static class NumCard extends Card {
        int value;

        public NumCard(Color color, int value) {
            super(color);
            this.value = value;
        }

        public String get() {
            return "Color: " + color + " Value: " + value + " ";
        }
    }

    static class SCard extends Card {
        Move move;

        public SCard(Color color, Move move) {
            super(color);
            this.move = move;
        }

        public String get() {
            return "Color: " + color + " Value: " + move + " ";
        }
    }

    enum Color {
        RED, YELLOW, BLUE, GREEN, SPECIAL;
    }

    enum Move {
        SKIP, REVERSE, DRAW_TWO, DRAW_FOUR, WILD;
    }

    private void startGame() {
        List<Card> deck = genDeck();
        players.forEach(p -> p.setHand(deck.subList(0, 7)));
        //players.clear();
    }

    private List<Card> genDeck() {
        List<Card> deck = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            for (Color color : Color.values()) {
                for (int j = 0; j < 10; j++) {
                    if (color == Color.SPECIAL)
                        break;
                    if (i != 0 || j != 0)
                        deck.add(new NumCard(color, j));
                }

                for (Move move : Move.values()) {
                    if (color == Color.SPECIAL)
                        break;
                    if (move == Move.DRAW_FOUR || move == Move.WILD) {
                        deck.add(new SCard(Color.SPECIAL, move));
                        continue;
                    }
                    deck.add(new SCard(color, move));
                }
            }
        }

        System.out.println(deck.size());
        Collections.shuffle(deck);
        return deck;
    }
}
