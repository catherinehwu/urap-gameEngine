package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for each individual token of a player (PlayerGroup).
 * Each player of the actual game has a collection of player instances
 * based on how many tokens are needed in that game.
 */
public class Player {
    // Player attributes
    private String name;
    private String imageFileName;
    private Square location;
    private GameEngine game;
    private PlayerGroup playerGroup;
    private int tokenNum; //off by 1 - token number 1 is given the index 0

    // Animation Images
    private int imageIndex;
    private ArrayList<Texture> costumes;

    // Player GUI details
    private Texture playerTexture;
    private int sizeWidth = 64; //32;
    private int sizeHeight = 64; //32;
    private int prevRoll = 0;
    private int playerNum;
    private String message;

    // Player turn continuation tracker
    private boolean determineAction;
    private String savedAction;

    // Allow for step by step movement
    private Square destination;
    private boolean squareAction;

    // Tracking Chance Cards
    private ChanceCard drawn;
    private boolean chanceAction;
    private String chanceActStored;

    public Player(String playerName, String imageFile, GameEngine curGame, int pNum, int tokenN) {
        name = playerName;
        imageFileName = imageFile;
        game = curGame;
        location = game.getBoard().getStart();
        tokenNum = tokenN;

        playerNum = pNum;
        determineAction = false;
        savedAction = "";
        message = "";

        playerTexture = new Texture(Gdx.files.internal(imageFileName));
        costumes = new ArrayList<>();
        costumes.add(playerTexture);
    }

    public Player(String playerName, String[] imageFiles, GameEngine curGame, int pNum, int tokenN) {
        this(playerName, imageFiles[0], curGame, pNum, tokenN);
        imageIndex = 0;
        costumes = new ArrayList<>();

        for (String img : imageFiles) {
            Texture t = new Texture(Gdx.files.internal(img));
            costumes.add(t);
        }
    }

    /**
     * Initiates the turn for this token. Rolls the dice
     * and moves the token. Returns whether or not the turn is complete
     * (as in if the turn should advance to the next player).
     *
     * @param gameUI - GUI object for display purposes
     * @return if this token / player's turn is complete
     */
    public boolean guiTurn(BoardGameEngine gameUI) {
        message = "";
        Square curLoc = location;
        int stepVal = guiRoll(true);
        boolean complete = guiMove(stepVal, gameUI);
        return complete;
    }

    /**
     * Draws the individual token at the token's x and y location.
     * @param gameUI - GUI instance to display to
     */
    public void draw(BoardGameEngine gameUI) {
        // FIXME - DEBUGGING text
//        System.out.println(name);
//        System.out.println(prevRoll);
//        System.out.println(location.getSeqNum());

        // Real Game Board
        gameUI.batch.draw(playerTexture,
                location.getX() + (playerNum - 1) * gameUI.distBetwPlayers, location.getY(),
                sizeWidth, sizeHeight);

        // DIALOG display BOX (FIXME - MESSAGE BAR)
        int padding = 10;
        gameUI.layout.setText(gameUI.font, name + "'s roll: " + prevRoll, Color.BLACK, gameUI.messageAvgLen + gameUI.messagePad, Align.left, true);
        gameUI.font.draw(gameUI.batch, gameUI.layout, gameUI.boardW - gameUI.messageAvgLen - gameUI.messagePad, gameUI.boardH + gameUI.messageHeight - gameUI.messagePad - (playerNum * 2 * gameUI.layout.height) - (game.tokensPerPlayer - 1) * 2 * gameUI.layout.height * (playerNum - 1) - tokenNum * 2 * gameUI.layout.height);

//        gameUI.font.draw(gameUI.batch, gameUI.layout, gameUI.boardW - gameUI.messageAvgLen - gameUI.messagePad, gameUI.boardH + gameUI.messageHeight - gameUI.messagePad - playerNum * 2 * gameUI.layout.height);
        gameUI.layout.setText(gameUI.font, name + ":" + message, Color.BLACK, gameUI.boardW, Align.left, true);
        gameUI.font.draw(gameUI.batch, gameUI.layout, gameUI.messagePad, gameUI.boardH + gameUI.messageHeight - gameUI.messagePad - playerNum * 2 * gameUI.layout.height - (game.tokensPerPlayer - 1) * 2 * gameUI.layout.height * (playerNum - 1) - tokenNum * 2 * gameUI.layout.height);

//        gameUI.font.draw(gameUI.batch, gameUI.layout, gameUI.messagePad, gameUI.boardH + gameUI.messageHeight - gameUI.messagePad - playerNum * 2 * gameUI.layout.height);
    }

    /**
     * Rolls the game dice. If SET is true, then it sets the last roll
     * tracker to the result of the new roll (which is needed for game display).
     *
     * @param set - whether or not to set this as the last / previous roll
     * @return the number rolled ( 1 ~ 6 for a typical dice)
     */
    private int guiRoll(boolean set) {
        int roll = game.getDice().nextVal();
        if (set) {
            prevRoll = roll;
        }
        System.out.println(name + " rolled a " + prevRoll); // debugging line
        return roll;
    }

    /**
     * Moves the token by calculating its future location. Processes
     * the actions on the destination square (i.e roll again).
     *
     * @param num - number to move the dice (could be positive or negative)
     * @param gameUI - GUI object for display purposes
     * @return if this token / player's turn is complete
     */
    private boolean guiMove(int num, BoardGameEngine gameUI) {
        Board gameBoard = game.getBoard();
        int newLocationSeqNum = safeMove(num, gameBoard);

        Square futureLoc = gameBoard.getSquare(newLocationSeqNum);
        destination = futureLoc;

        draw(gameUI);

        boolean complete = true;

        for (String action : destination.getActions()) {
            // assuming movement isn't an issue because game logic makes sense
            // (as in don't roll again and move to another square)
            // boolean used to relay whether turn is complete
            complete = this.guiDisplayAct(action, gameUI) && complete;
            draw(gameUI);
        }
        return complete;
    }

    /**
     * Moves the token to a specific square with sequence number SQNUM.
     * Uses function guiMove for the actual movement, but first calculates the positive
     * or negative steps needed to move to the specific square.
     *
     * @param sqNum - square to move to
     * @param gameUI - GUI object for display purposes
     * @return if this token / player's turn is complete
     */
    private boolean guiMoveTo(int sqNum, BoardGameEngine gameUI) {
        int curNum = location.getSeqNum();
        int difference = sqNum - curNum;
        return guiMove(difference, gameUI);
    }

    /**
     * Moves the token NUM steps. If positive, moves forward NUM steps.
     * If negative, moves backward NUM steps.
     *
     * @param num - number token needs to move
     * @param gameBoard - GUI object for display purposes
     * @return the seq number of destination location
     */
    private int safeMove(int num, Board gameBoard) {
        int locationSeqNum = location.getSeqNum();
        if (num < 0) {
            int counter = num;
            while (counter < 0 && locationSeqNum >= 0) {
                locationSeqNum -= 1;
                counter += 1;
            }
        } else {
            int counter = 0;
            while (counter < num && locationSeqNum < gameBoard.getTotalSqNum() - 1) {
                locationSeqNum += 1;
                counter += 1;
            }
        }
        return locationSeqNum;
    }

    /**
     * Action taken due to square actions. Actually makes the
     * automatic movement (flying to a different square or moving
     * a certain steps forward). Only squareAction moves enter this
     * method.
     *
     * Roll again, skip, reverse situations should never enter this method.
     *
     * @param key - action being processed
     * @param gameUI - GUI object for display
     * @return - whether or not this token / player's turn is complete
     */
    private boolean guiAct(String key, BoardGameEngine gameUI) {
        char type = key.charAt(0);
        switch(type) {
            case 'a':
            case 'A':
                // roll again
                System.out.println("Roll again!");
                return false;
            case 'b':
            case 'B':
                // change square by certain number
                // positive is move forward
                int steps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + steps + " forward!");
                return guiMove(steps, gameUI);
                // return true;
            case 'c':
            case 'C':
                // change square by certain number
                // negative is move backward
                int backSteps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + backSteps + " backwards!");
                return guiMove(backSteps * -1, gameUI);
                // return true;
            case 'd':
            case 'D':
                // go to a certain square
                int sqNum = Integer.valueOf(key.substring(1));
                System.out.println("Moving to square #" + sqNum + "!");
                return guiMoveTo(sqNum, gameUI);
                // return true;
            case 'e':
            case 'E':
                // skip this player's next turn
                playerGroup.skipTurn();
                System.out.println("Next turn skipped!");
                return true;
            case 'f':
            case 'F':
                // cycle reversed
                game.reverse();
                System.out.println("Turn order reversed!");
                return true;
            case 'g':
            case 'G':
                // if roll certain number
                // do the following action
                System.out.println("Roll again to determine action!");
                determineAction = true;
                savedAction = key;
                return false;
            case 'H':
                // Drawing chance card
                String chanceType = key.substring(1);
                drawn = game.draw(chanceType);
                System.out.println("Drew this card: " + drawn);
                return newChanceCardAct(gameUI);
            default:
                System.out.println(key);
                System.out.println("Unknown command.");
                break;
        }
        return false;
    }

    /**
     * Initiates the action on the square (displays message and warning to the player)
     * but does not actually start the specified movements or actions yet. Sets
     * SQUAREACTION to true or false depending of if this square requires an automatic
     * move continuation (i.e move to sq num 0). Most actions apply just to this token
     * but actions like skip turn apply to the PlayerGroup and reverse applies to
     * the whole game. Drawing a chance card only displays the message - the act of drawing
     * a Chance Card happens later when guiAct is called.
     *
     * @param key - action to display or process
     * @param gameUI - GUI object for display purposes
     * @return - whether or not turn has been completed for this token / player
     */
    private boolean guiDisplayAct(String key, BoardGameEngine gameUI) {
        char type = key.charAt(0);

        // Setting default sounds based on parsing
        if (destination != null && destination.getSquareSound() == null) {
            String defaultSound = game.getSoundFromList(String.valueOf(type));
            destination.setDefaultSound(defaultSound);
        }

        switch(type) {
            case 'a':
            case 'A':
                // roll again
                System.out.println("Roll again!");
                message = " Roll again!";
                squareAction = false;
                break;
            case 'b':
            case 'B':
                // change square by certain number
                // positive is move forward
                int steps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + steps + " forward!");
                message = " Moving " + steps + " forward!";
                squareAction = true;
                break;
            case 'c':
            case 'C':
                // change square by certain number
                // negative is move backward
                int backSteps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + backSteps + " backwards!");
                message = " Moving " + backSteps + " backwards!";
                squareAction = true;
                break;
            case 'd':
            case 'D':
                // go to a certain square
                int sqNum = Integer.valueOf(key.substring(1));
                System.out.println("Moving to square #" + sqNum + "!");
                message = " Moving to square #" + sqNum + "!";
                squareAction = true;
                break;
            case 'e':
            case 'E':
                // skip this player's next turn
                playerGroup.skipTurn();
                System.out.println("Next turn skipped!");
                message = " Next turn skipped!";
                squareAction = false;
                return true;
            case 'f':
            case 'F':
                // cycle reversed
                game.reverse();
                System.out.println("Turn order reversed!");
                message = " Caused a turn order to reverse!";
                squareAction = false;
                return true;
            case 'g':
            case 'G':
                // if roll certain number
                // do the following action
                System.out.println("Roll again to determine action!");
                message = " Roll again to determine action!";
                determineAction = true;
                savedAction = key;
                squareAction = false;
                break;
            case 'H':
                // drawing a chance card
                System.out.println("Drawing chance card of type: " + key.substring(1));
                message = " Drawing chance card of type: " + key.substring(1);
                squareAction = true;
                chanceAction = true;
                break;
            default:
                System.out.println("Unknown command.");
                break;
        }
        return false;
    }

    /**
     * Completes the action for a roll again to determine action square.
     * Rolls the game dice again and sees if it matches with any of the targets
     * on the current location square. If it matches, proceeds to process the specified
     * action. Otherwise, do nothing.
     *
     * @param gameUI - GUI object for display purposes
     * @return - whether or not turn is completed
     */
    public boolean completeAction(BoardGameEngine gameUI) {
        determineAction = false;
        // Records this rolls in output
        int number = guiRoll(true);
        // Doesn't record rolls in output
        // int number = guiRoll(false);

        String[] groups = savedAction.substring(1).split("\\.");
        String format = "([\\d]+)(.*)";
        System.out.println("saved action - " + savedAction);

        for (String opt : groups) {
            Matcher partMatch = Pattern.compile(format).matcher(opt);
            int target = -1;
            if (partMatch.matches()) {
                target = Integer.valueOf(partMatch.group(1));
            }
            if (number == target) {
                message = " Rolled a " + target + "! Action continuing.";
                System.out.println(message);
                return guiAct(partMatch.group(2), gameUI);
            }
        }
        message = "No additional action!";
        System.out.println(message);
        return true;
    }

    /**
     * Initiates the automatic processing of a square's action
     * (i.e move to start, move 10 forward). Updates squareAction
     * variable to reflect if there are any more automatic
     * square action processes to consider.
     *
     * @param gameUI - GUI object for display purposes
     */
    public boolean squareAction(BoardGameEngine gameUI) {
        // Reset squareAction to be false because the squareAction is currently being
        // addressed and taken care of.
        squareAction = false;
        boolean complete = true;

        // Chance Card Continuation Situation
        if (chanceActStored != null) {
            System.out.println("Doing a saved chance action");
            complete = this.guiAct(chanceActStored, gameUI) && complete;
            chanceActStored = null;
            if (destination != null) {
                for (String act : destination.getActions()) {
                    // If destination requires user to draw chance card, makes sure game knows so that
                    // it will wait for user input before drawing the card.
                    if (act.contains("H")) {
                        chanceAction = true;
                    }
                }
            }
            return complete;
        }

        for (String action : location.getActions()) {
            // assuming movement isn't an issue because game logic makes sense
            // (as in don't roll again and move to another square)
            // boolean used to relay whether turn is complete
            complete = this.guiAct(action, gameUI) && complete;
            draw(gameUI);
        }
        return complete;
    }

    /**
     * Draws a new chance card. Turns the chance action off so that automatic
     * actions can continue to happen - telling the system it no longer
     * needs to wait for user input to draw a chance card. Sets square action to false because
     * the square action will be completed in this method.
     *
     * Goes through all the actions of the chance card and displays them. The completion of these
     * actions may or may not happen automatically, based on what they are. (similar to a normal
     * move or turn structure).
     *
     * @param gameUI - used for GUI display
     * @return whether or not turn has been completed
     */
    private boolean newChanceCardAct(BoardGameEngine gameUI) {
        chanceAction = false;
        squareAction = false;
        boolean complete = true;
        for (String action : drawn.getActions()) {
            complete = guiDisplayAct(action, gameUI) && complete;
            if (squareAction) {
                chanceActStored = action;
            }
        }
        return complete;
    }

    /**
     * Advances this token my moving its current location one step
     * forward or backward (in the direction of the destination location).
     */
    public void advance() {
        Board board = game.getBoard();
        if (location.getSeqNum() < destination.getSeqNum()) {
            location = board.getSquare(location.getSeqNum() + 1);
        } else {
            location = board.getSquare(location.getSeqNum() - 1);
        }
        costumeAdvance();
    }

    private void costumeAdvance() {
        imageIndex += 1;
        playerTexture = costumes.get(imageIndex % costumes.size());
    }

    public String getName() {
        return name;
    }

    public Square getLocation() {
        return location;
    }

    public void setMessage(String s) {
        message = s;
    }

    public void setPlayerGroup(PlayerGroup player) {
        playerGroup = player;
    }

    public int getPrevRoll() {
        return prevRoll;
    }

    public boolean determiningAction() {
        return determineAction;
    }

    public boolean getChanceAction() {
        return chanceAction;
    }

    public Square getDestination() {
        return destination;
    }

    public void resetDestination() {
        destination = null;
    }

    public boolean isSquareAction() {
        return squareAction;
    }
}
