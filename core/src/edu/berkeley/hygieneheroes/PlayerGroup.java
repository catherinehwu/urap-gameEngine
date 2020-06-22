package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

public class PlayerGroup {
    private ArrayList<Player> tokens;
    private String name;
    private boolean computerPlayer;

    public PlayerGroup(ArrayList<Player> tokens, String name) {
        this.tokens = tokens;
        this.name = name;
        computerPlayer = false;
    }

    public PlayerGroup(ArrayList<Player> tokens, String name, boolean AI) {
        this(tokens, name);
        computerPlayer = AI;
    }

    public ArrayList<Player> getTokens() {
        return tokens;
    }

    public Player getTokenNumber(int i) {
        if (i > tokens.size()) {
            System.out.println("Can't get this token");
            return null;
        } else {
            return tokens.get(i - 1);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isComputerPlayer() {
        return computerPlayer;
    }
}
