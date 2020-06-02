package edu.berkeley.hygieneheroes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class GameEngine {
    // Game Features
    private Dice dice;
    private Board board;
    private ArrayList<Player> playersList;
    private int curTurnIndex;
    private int direction;
    private int numOfPlayers;

    // Interacting with GUI and User
    private boolean turnComplete;

    public GameEngine(int Xrange, int Yrange, int endPosNum) {
        board = new Board(Xrange, Yrange, endPosNum);
        playersList = new ArrayList<>();
        curTurnIndex = 0;
        direction = 1;

        //standard dice with 1~6
        dice = new Dice(6);
    }

    public void addSquare(int num, int sqX, int sqY,
                          String picture, String text, String sound,
                          ArrayList<String> listOfActions) {
        board.setSquare(num, sqX, sqY, picture, text, sound, listOfActions);
    }

    public void addPlayer(String name, String imageFile, int num) {
        Player newPerson = new Player(name, imageFile, this, num);
        playersList.add(newPerson);
        sortPlayers();
    }

    public void reverse() {
        direction *= -1;
    }

    public String currentTurnStr() {
        Player current = playersList.get(curTurnIndex);
        while (current.getSkipTurn()) {
            current.turnSkipped();
            advanceTurn();
            current = playersList.get(curTurnIndex);
        }

        return "Current turn: " + current.getName();
    }

    public Player currentPlayer() {
        return playersList.get(curTurnIndex);
    }

    public void activate(BoardGameEngine gameUI) {
        // Causes game to resume with a next turn or continuation of previous move
        turnComplete = false;
        Player p = playersList.get(curTurnIndex);

        if (p.determiningAction()) {
            // Dice must be rolled to determine the next action
            turnComplete = p.completeAction(gameUI);
        } else if (p.guiTurn(gameUI)) {
            // Completes next turn in the game
            turnComplete = true;
        }

        // Only advance turn if turn has completed
        if (turnComplete) {
            advanceTurn();
        }
    }

    private void advanceTurn() {
        curTurnIndex += (1 * direction);
        while (curTurnIndex < 0) {
            curTurnIndex += numOfPlayers;
        }
        while (curTurnIndex >= numOfPlayers) {
            curTurnIndex -= numOfPlayers;
        }
    }

    public boolean gameOver() {
        for (Player p : playersList) {
            if (p.getLocation().equals(board.getEnd())) {
                return true;
            }
        }
        return false;
    }

    public Player winner() {
        for (Player p : playersList) {
            if (p.getLocation().equals(board.getEnd())) {
                return p;
            }
        }
        return null;
    }

    private void sortPlayers() {
        Comparator<Player> comp = new AlphabetComparator();
        Collections.sort(playersList, comp);
    }

    public Board getBoard() {
        return board;
    }

    public Dice getDice() {
        return dice;
    }

    // processing input from System.in
//    public String readLine() {
//
//        // promptHuman();
//        promptApproval();
//
//        if (input.hasNextLine()) {
//            return input.nextLine().trim();
//        } else {
//            return null;
//        }
//    }

    // output for troubleshooting
    public void display() {
        System.out.println();
        System.out.print("==========");
        board.display();
        for (Player p : playersList) {
            System.out.println(p.getName() + " is at " + p.getLocation().getSeqNum());
        }
        System.out.println("==========");
    }

    // accessor methods
    public ArrayList<Player> getPlayersList() {
        return playersList;
    }

    public void setNumOfPlayers(int num) {
        numOfPlayers = num;
    }
}
