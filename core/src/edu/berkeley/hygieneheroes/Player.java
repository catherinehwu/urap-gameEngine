package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {
    // Player attributes
    private String name;
    private String imageFileName;
    private Square location;
    private GameEngine game;
    private boolean skipTurn;

    // Player GUI details
    private Texture playerTexture;
    private int sizeWidth = 32;
    private int sizeHeight = 32;
    private int prevRoll = 0;
    private int playerNum;
    private Square prevLocation;
    private String message;

    // Player turn continuation tracker
    private boolean determineAction;
    private String savedAction;

    // Allow for step by step movement
    private Square destination;
    private boolean squareAction;

    public Player(String playerName, String imageFile, GameEngine curGame, int pNum) {
        name = playerName;
        imageFileName = imageFile;
        game = curGame;
        location = game.getBoard().getStart();
        skipTurn = false;

        playerNum = pNum;
        prevLocation = game.getBoard().getStart();
        determineAction = false;
        savedAction = "";
        message = "";

        playerTexture = new Texture(Gdx.files.internal(imageFileName));
    }

    public boolean guiTurn(BoardGameEngine gameUI) {
        message = "";
        Square curLoc = location;
        int stepVal = guiRoll(true);
        boolean complete = guiMove(stepVal, gameUI);
        prevLocation = curLoc;
        return complete;
    }

    public void draw(BoardGameEngine gameUI) {
        // FIXME - DEBUGGING text
//        System.out.println(name);
//        System.out.println(prevRoll);
//        System.out.println(location.getSeqNum());

        // Real Game Board
        gameUI.batch.draw(playerTexture, location.getX(), location.getY(),
                sizeWidth, sizeHeight);

        // DIALOG display BOX (FIXME - MESSAGE BAR)
        gameUI.layout.setText(gameUI.font, name + "'s roll: " + prevRoll, Color.BLACK, gameUI.messageAvgLen + gameUI.messagePad, Align.left, true);
        gameUI.font.draw(gameUI.batch, gameUI.layout, gameUI.boardW - gameUI.messageAvgLen - gameUI.messagePad, gameUI.boardH + gameUI.messageHeight - gameUI.messagePad - playerNum * 2 * gameUI.layout.height);
        gameUI.layout.setText(gameUI.font, name + ":" + message, Color.BLACK, gameUI.boardW, Align.left, true);
        gameUI.font.draw(gameUI.batch, gameUI.layout, gameUI.messagePad, gameUI.boardH + gameUI.messageHeight - gameUI.messagePad - playerNum * 2 * gameUI.layout.height);
    }

    private int guiRoll(boolean set) {
        int roll = game.getDice().nextVal();
        if (set) {
            prevRoll = roll;
        }
        System.out.println(name + " rolled a " + prevRoll); // debugging line
        return roll;
    }

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

    private void guiMoveTo(int sqNum, BoardGameEngine gameUI) {
        int curNum = location.getSeqNum();
        int difference = sqNum - curNum;
        guiMove(difference, gameUI);
    }

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
                guiMove(steps, gameUI);
                return true;
            case 'c':
            case 'C':
                // change square by certain number
                // negative is move backward
                int backSteps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + backSteps + " backwards!");
                guiMove(backSteps * -1, gameUI);
                return true;
            case 'd':
            case 'D':
                // go to a certain square
                int sqNum = Integer.valueOf(key.substring(1));
                System.out.println("Moving to square #" + sqNum + "!");
                guiMoveTo(sqNum, gameUI);
                return true;
            case 'e':
            case 'E':
                // skip this player's next turn
                skipTurn = true;
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
            default:
                System.out.println("Unknown command.");
                break;
        }
        return false;
    }

    private boolean guiDisplayAct(String key, BoardGameEngine gameUI) {
        char type = key.charAt(0);
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
                skipTurn = true;
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
            default:
                System.out.println("Unknown command.");
                break;
        }
        return false;
    }

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
                return guiAct(partMatch.group(2), gameUI);
            }
        }
        message = "No additional action!";
        return true;
//        Matcher match = Pattern.compile(format).matcher(savedAction);
//        int target = -1;
//        if (match.matches()) {
//            target = Integer.valueOf(match.group(1));
//        }
//        if (number == target) {
//            message = " Rolled a " + target + "! Action continuing.";
//            return guiAct(match.group(2), gameUI);
//        } else {
//            System.out.println("No action occurred!");
//            message = " Did not roll a " + target + "! No additional action.";
//            return true;
//        }
    }

    public void squareAction(BoardGameEngine gameUI) {
        boolean complete = true;

        for (String action : location.getActions()) {
            // assuming movement isn't an issue because game logic makes sense
            // (as in don't roll again and move to another square)
            // boolean used to relay whether turn is complete
            complete = complete && this.guiAct(action, gameUI);
            draw(gameUI);
        }
        squareAction = !complete;
        if (destination != null && !destination.getActions().isEmpty()) {
            squareAction = true;
        }
    }

    public void advance() {
        Board board = game.getBoard();
        if (location.getSeqNum() < destination.getSeqNum()) {
            location = board.getSquare(location.getSeqNum() + 1);
        } else {
            location = board.getSquare(location.getSeqNum() - 1);
        }
    }

    public String getName() {
        return name;
    }

    public Square getLocation() {
        return location;
    }

    public boolean getSkipTurn() {
        return skipTurn;
    }

    public void turnSkipped() {
        skipTurn = false;
        message = "";
    }

    public int getPrevRoll() {
        return prevRoll;
    }

    public boolean determiningAction() {
        return determineAction;
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
