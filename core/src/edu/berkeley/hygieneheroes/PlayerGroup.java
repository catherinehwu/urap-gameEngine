package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

public class PlayerGroup {
    private ArrayList<Player> tokens;
    private String name;
    private boolean computerPlayer;
    private Player currentToken;
    private boolean skipTurn;
    private Board board;

    public PlayerGroup(ArrayList<Player> tokens, String name, Board board) {
        this.tokens = tokens;
        this.name = name;
        computerPlayer = false;
        skipTurn = false;
        this.board = board;
        currentToken = null;

        for (Player p : tokens) {
            p.setPlayerGroup(this);
        }
    }

    public PlayerGroup(ArrayList<Player> tokens, String name, Board board, boolean AI) {
        this(tokens, name, board);
        computerPlayer = AI;
    }

    public boolean finished() {
        for (Player token : tokens) {
            if (!token.getLocation().equals(board.getEnd())) {
                return false;
            }
        }
        return true;
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

    public void setCurrentToken(int i) {
        if (i > tokens.size()) {
            System.out.println("Can't get this token");
            return;
        }
        currentToken = tokens.get(i - 1);
    }

    public void resetCurrentToken() {
        currentToken = null;
    }

    public Player getCurrentToken() {
        return currentToken;
    }

    public void skipTurn() {
        skipTurn = true;
    }

    public void turnSkipped() {
        skipTurn = false;
        currentToken.setMessage("");
    }

    public boolean getSkipTurn() {
        return skipTurn;
    }
}
