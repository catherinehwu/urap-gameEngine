package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

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
    private Sprite playerSprite;
    private Texture playerTexture;
    private int sizeWidth = 32;
    private int sizeHeight = 32;
    private int prevRoll = 0;
    private int playerNum;
    private Square prevLocation;

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

        playerTexture = new Texture(Gdx.files.internal(imageFileName));
        playerSprite = new Sprite(playerTexture);
    }

    public boolean guiTurn(BoardGameEngine gameUI) {
        Square curLoc = location;
        int stepVal = guiRoll(true);
        boolean complete = guiMove(stepVal, gameUI);
        prevLocation = curLoc;
        return complete;
    }

    public void draw(BoardGameEngine gameUI) {
        // debugging render texts
//        System.out.println(name);
//        System.out.println(prevRoll);
//        System.out.println(location.getSeqNum());

        // getting ratios
        float xFraction = ((float) location.getX()) / game.getBoard().getXrange();
        float yFraction = ((float) location.getY()) / game.getBoard().getYrange();

        // drawing player piece
//        gameUI.batch.draw(playerTexture,
//                xFraction * Gdx.graphics.getWidth(), yFraction * Gdx.graphics.getHeight(),
//                sizeWidth, sizeHeight);

        // drawing player piece based on board world
        gameUI.batch.draw(playerTexture, xFraction * gameUI.boardW, yFraction * gameUI.boardH,
                sizeWidth, sizeHeight);

        // outputting data about player's move
        gameUI.font.draw(gameUI.batch, name + " previous moving roll: " + prevRoll, 0, 440 - 20 * playerNum);
        gameUI.font.draw(gameUI.batch, name + " previous position: " + prevLocation.getSeqNum(), 0, 380 - 20 * playerNum);
        gameUI.font.draw(gameUI.batch, name + " current position: " + location.getSeqNum(), 0, 300 - 20 * playerNum);

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
        // location = futureLoc;

        draw(gameUI);
        game.display(); // debugging line

        boolean complete = destination.getActions().isEmpty();
//        boolean complete = destination.getActions().isEmpty();
//        if (!complete) {
//            squareAction = true;
//        }

        for (String action : destination.getActions()) {
            // assuming movement isn't an issue because game logic makes sense
            // (as in don't roll again and move to another square)
            // boolean used to relay whether turn is complete
            complete = this.guiDisplayAct(action, gameUI) && complete;
            draw(gameUI);
        }
        return complete;
        /*
        for (String action : location.getActions()) {
            // assuming movement isn't an issue because game logic makes sense
            // (as in don't roll again and move to another square)
            // boolean used to relay whether turn is complete
            complete = complete && this.guiAct(action, gameUI);
            draw(gameUI);
        }
        */
        // return complete;
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
                gameUI.setGameMessage(name + " roll again!", playerNum);
                return false;
            case 'b':
            case 'B':
                // change square by certain number
                // positive is move forward
                int steps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + steps + " forward!");
                gameUI.setGameMessage(name + " moving " + steps + " forward!", playerNum);
                guiMove(steps, gameUI);
                return true;
            case 'c':
            case 'C':
                // change square by certain number
                // negative is move backward
                int backSteps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + backSteps + " backwards!");
                gameUI.setGameMessage(name + " moving " + backSteps + " backwards!", playerNum);
                guiMove(backSteps * -1, gameUI);
                return true;
            case 'd':
            case 'D':
                // go to a certain square
                int sqNum = Integer.valueOf(key.substring(1));
                System.out.println("Moving to square #" + sqNum + "!");
                gameUI.setGameMessage(name + " Moving to square #" + sqNum + "!", playerNum);
                guiMoveTo(sqNum, gameUI);
                return true;
            case 'e':
            case 'E':
                // skip this player's next turn
                skipTurn = true;
                System.out.println("Next turn skipped!");
                gameUI.setGameMessage(name + " Next turn skipped!", playerNum);
                return true;
            case 'f':
            case 'F':
                // cycle reversed
                game.reverse();
                System.out.println("Turn order reversed!");
                gameUI.setGameMessage(name + " caused a turn order to reverse!", playerNum);
                return true;
            case 'g':
            case 'G':
                // if roll certain number
                // do the following action
                System.out.println("Roll again to determine action!");
                gameUI.setGameMessage(name + " Roll again to determine action!", playerNum);
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
                gameUI.setGameMessage(name + " roll again!", playerNum);
                squareAction = false;
                break;
            case 'b':
            case 'B':
                // change square by certain number
                // positive is move forward
                int steps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + steps + " forward!");
                gameUI.setGameMessage(name + " moving " + steps + " forward!", playerNum);
                squareAction = true;
                break;
            case 'c':
            case 'C':
                // change square by certain number
                // negative is move backward
                int backSteps = Integer.valueOf(key.substring(1));
                System.out.println("Moving " + backSteps + " backwards!");
                gameUI.setGameMessage(name + " moving " + backSteps + " backwards!", playerNum);
                squareAction = true;
                break;
            case 'd':
            case 'D':
                // go to a certain square
                int sqNum = Integer.valueOf(key.substring(1));
                System.out.println("Moving to square #" + sqNum + "!");
                gameUI.setGameMessage(name + " Moving to square #" + sqNum + "!", playerNum);
                squareAction = true;
                break;
            case 'e':
            case 'E':
                // skip this player's next turn
                System.out.println("Next turn skipped!");
                gameUI.setGameMessage(name + " Next turn skipped!", playerNum);
                squareAction = false;
                break;
            case 'f':
            case 'F':
                // cycle reversed
                System.out.println("Turn order reversed!");
                gameUI.setGameMessage(name + " caused a turn order to reverse!", playerNum);
                squareAction = false;
                break;
            case 'g':
            case 'G':
                // if roll certain number
                // do the following action
                System.out.println("Roll again to determine action!");
                gameUI.setGameMessage(name + " Roll again to determine action!", playerNum);
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

        String format = "[gG]([\\d]+)(.*)";
        Matcher match = Pattern.compile(format).matcher(savedAction);
        int target = -1;
        if (match.matches()) {
            target = Integer.valueOf(match.group(1));
        }
        if (number == target) {
            gameUI.setGameMessage(name + " rolled a " + target + "! Action continuing.", playerNum);
            return guiAct(match.group(2), gameUI);
        } else {
            System.out.println("No action occurred!");
            gameUI.setGameMessage(name + " No action occurred!", playerNum);
            return true;
        }
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
