package com.blue.commands;

import com.blue.api.Context;
import com.blue.api.DefaultCommand;

import discord4j.core.object.entity.Member;

import java.util.*;
import java.util.stream.IntStream;

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

        if (args.length == 0) {
            ctx.sendPublic("uhh... specify sum");
            return;
        }

        Optional<Player> ingame = players.stream()
                .filter(p -> p.member().equals(ctx.member().orElseThrow()))
                .findFirst();

        if (ingame.isEmpty() && !playing && "join".equals(args[0])) {
            players.add(new Player(ctx.member().orElseThrow()));
            ctx.sendPublic("added " + ctx.member().orElseThrow().getNicknameMention() + " to the game");
        } else if (ingame.isEmpty() && playing)
            ctx.sendPublic("THERE IS A GAME RN");
        else if (ingame.isEmpty())
            ctx.sendPublic("use join to join");
        else if ("join".equals(args[0]) && players.contains(ingame.get()))
            ctx.sendPublic("you're already in the game!");
        else if ("start".equals(args[0]) && players.size() > 0 && !playing) {
            playing = true;
            ctx.sendPublic("starting game");
            startGame(ctx);
        } else if ("hand".equals(args[0]) && playing)
            ingame.get().showHand();
        else if ("draw".equals(args[0]) && playing)
            ingame.get().drawFromMiddle();
        else
            continueGame(ingame.get(), args, ctx);
    }

    class Player {
        Member m;
        List<Card> hand;

        public Player(Member m) {
            this.m = m;
            hand = new ArrayList<>();
        }

        public Member member() {
            return m;
        }

        public void addToHand(Card card) {
            hand.add(card);
        }

        public void showHand() {
            m.getPrivateChannel()
                    .flatMap(ch -> ch.createMessage(hand.stream()
                            .map(c -> c.getColor().getEmoji() + (c instanceof SCard ? ((SCard) c).move.emoji : c.getValue()))
                            .reduce((card, card2) -> card + " | " + card2)
                            .orElse("Empty hand")))
                    .block();
        }

        public void drawFromMiddle() {
            hand.add(deck.remove(0));
            showHand();
        }

        public List<Card> getHand() {
            return hand;
        }
    }

    abstract static class Card {
        Color color;

        public Card(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        abstract String getValue();
        abstract boolean goOnTopOf(Card c);
    }

    static class NumCard extends Card {
        int value;

        public NumCard(Color color, int value) {
            super(color);
            this.value = value;
        }

        @Override
        public String getValue() {
            return Integer.toString(value);
        }

        @Override
        boolean goOnTopOf(Card c) {
            if (this.getColor().equals(c.getColor()))
                return true;
            else
                return this.getValue().equals(c.getValue());
        }
    }

    static class SCard extends Card {
        Move move;

        public SCard(Color color, Move move) {
            super(color);
            this.move = move;
        }

        @Override
        public String getValue() {
            return move.toString();
        }

        @Override
        boolean goOnTopOf(Card c) {
            if (this.getColor() == Color.SPECIAL)
                return true;
            else if (this.getColor().equals(c.getColor()))
                return true;
            else
                return this.getValue().equals(c.getValue());
        }
    }

    enum Color {
        RED(":red_circle:"), YELLOW(":yellow_circle:"), BLUE(":blue_circle:"),
        GREEN(":green_circle:"), SPECIAL(":black_circle:");

        private final String emoji;

        Color(String emoji) {
            this.emoji = emoji;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    enum Move {
        SKIP(":x:"), REVERSE(":repeat:"), DRAW_TWO(":two:"),
        DRAW_FOUR(":1234:"), WILD(":art:");

        private final String emoji;

        Move(String emoji) {
            this.emoji = emoji;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    private List<Card> deck;
    private Card middle;
    private int next;

    private void startGame(Context ctx) {
        deck = genDeck();
        while ((middle = deck.remove(0)) instanceof SCard) {
            deck.add(middle);
            middle = deck.remove(0);
        }
        Collections.shuffle(players);
        next = new Random().nextInt(players.size());

        players.forEach(p -> {
            for (int i = 0; i < 7; i++)
                p.addToHand(deck.remove(0));
        });

        rotate(ctx);
    }

    private void continueGame(Player player, String[] args, Context ctx) {
        Optional<Card> choice;
        if (!players.get(next).equals(player))
            ctx.sendPublic(player.m.getNickname() + " not your turn");
        else if (args.length != 2)
            ctx.sendPublic("Specify card like: !uno <color> <value>");
        else if ((choice = player.getHand()
                .stream()
                .filter(c -> c.getColor().toString().toLowerCase().equals(args[0])
                        && c.getValue().toLowerCase().equals(args[1]))
                .findFirst()).isEmpty()) {
            ctx.sendPublic("you do not have that card");
        } else if (choice.get().goOnTopOf(middle)) {
            if (choice.get() instanceof SCard) {
                switch (((SCard) choice.get()).move) {
                    case DRAW_FOUR:
                        for (int i = 0; i < 4; i++) {
                            players.get((next + 1) % players.size())
                                    .getHand()
                                    .add(deck.remove(0));
                        }
                        ctx.sendPublic(players.get((next + 1) % players.size()).m.getUsername() + " had to draw four");
                        update(player, choice.get(), ctx, 1);
                        break;
                    case SKIP:
                        ctx.sendPublic(players.get((next + 1) % players.size()).m.getUsername() + " got skipped");
                        update(player, choice.get(), ctx, 2);
                        break;
                    case REVERSE:
                        ctx.sendPublic("game reversed");
                        Collections.reverse(players);
                        update(player, choice.get(), ctx, 1);
                        break;
                    case DRAW_TWO:
                        IntStream.range(0, 3)
                                .forEach(i -> players.get((next + 1) % players.size())
                                        .getHand()
                                        .add(deck.remove(0)));
                        ctx.sendPublic(players.get((next + 1) % players.size()).m.getUsername() + " had to draw two");
                        update(player, choice.get(), ctx, 1);
                        break;
                    case WILD:
                        ctx.sendPublic("wild card");
                        update(player, choice.get(), ctx, 1);
                        break;
                }
            } else
                update(player, choice.get(), ctx, 1);
        }
    }

    private void update(Player player, Card choice, Context ctx, int forward) {
        player.getHand().remove(choice);
        deck.add(middle);
        middle = choice;
        next = (next + forward) % players.size();
        rotate(ctx);
    }

    private void rotate(Context ctx) {
        ctx.sendPublic("Card in the middle: " + middle.getColor().getEmoji() +
                        (middle instanceof SCard ? ((SCard) middle).move.getEmoji() : middle.getValue()));
        ctx.sendPublic(players.get(next).member().getNicknameMention() + "s turn");
        players.get(next).showHand();
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
                    if (move == Move.DRAW_FOUR && i == 0) {
                        deck.add(new SCard(Color.SPECIAL, move));
                        continue;
                    } else if (move == Move.DRAW_FOUR)
                        continue;
                    else if (move == Move.WILD && i == 1) {
                        deck.add(new SCard(Color.SPECIAL, move));
                        continue;
                    } else if (move == Move.WILD)
                        continue;
                    deck.add(new SCard(color, move));
                }
            }
        }
        Collections.shuffle(deck);
        return deck;
    }
}
