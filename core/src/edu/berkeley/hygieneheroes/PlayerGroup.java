package edu.berkeley.hygieneheroes;

import java.util.ArrayList;

/**
 * Represents one player in the game.
 * Stores the collection of Player objects (tokens)
 * associated with this player. Keeps track of whether
 * or not this player is AI or Human Player. Keeps track
 * of whether or not this player should have next
 * turn skipped.
 */
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

    // Constructor for AI Player
    public PlayerGroup(ArrayList<Player> tokens, String name, Board board, boolean AI) {
        this(tokens, name, board);
        computerPlayer = AI;
    }

    /**
     * Returns true if this player has finished (moved all pieces
     * to the finish square).
     *
     * @return true if player has successfully moved all square to finish square
     */
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

    /**
     * Returns token number (indexed from 1 - # of Tokens per Player)
     * but tokens stored in a list indexed from 0 - (# of Tokens per Player - 1).
     *
     * @param i - token number to retrieve
     * @return - returns the token / player object associated with that index
     */
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

    /**
     * Sets the current token (the one in play or movement)
     * to a specific token object.
     * @param i - index of the token that should be set to current token.
     */
    public void setCurrentToken(int i) {
        if (i > tokens.size()) {
            System.out.println("Can't get this token");
            return;
        }
        currentToken = tokens.get(i - 1);
    }

    /**
     * Resets current token to null. Happens at the end of a player's turn.
     */
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
        for (Player token : tokens) {
            token.setMessage("");
        }
    }

    public boolean getSkipTurn() {
        return skipTurn;
    }
}
