package edu.berkeley.hygieneheroes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class GameEngine {

    private Dice dice;
    private Board board;
    private ArrayList<Player> playersList;
    private int curTurnIndex;
    private int direction;
    private int numOfPlayers;
    private Scanner input;

    private boolean turnComplete;

    public GameEngine(int Xrange, int Yrange, int endPosNum) {
        board = new Board(Xrange, Yrange, endPosNum);
        playersList = new ArrayList<>();
        curTurnIndex = 0;
        direction = 1;
        input = new Scanner(System.in);
        dice = new Dice(6); //standard dice with 1~6
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
        turnComplete = false;
        Player p = playersList.get(curTurnIndex);
        if (p.determiningAction()) {
            turnComplete = p.completeAction(gameUI);
        } else if (p.guiTurn(gameUI)) {
            turnComplete = true;
        }

        if (turnComplete) {
            advanceTurn();
        }

//        playersList.get(curTurnIndex).guiTurn();
//        System.out.println("activate end");
//        advanceTurn();
//        System.out.println("activate end2");
//        p.draw(gameUI);
    }

    public void setUpPlayers() {
        System.out.println("Please input number of players (at least 1).");
        if (input.hasNextLine()) {
            numOfPlayers = Integer.valueOf(input.nextLine());
        }

        for (int i = 1; i <= numOfPlayers; i += 1) {
            String playerName;
            String playerImage;
            Scanner playerSetUp;
            System.out.println("Player " + i + ": Input name and image file separated with a space.");
            if (input.hasNextLine()) {
                playerSetUp = new Scanner(input.nextLine());
                playerName = playerSetUp.next();
                playerImage = playerSetUp.next();
                addPlayer(playerName, playerImage, i);
                System.out.println("Added " + playerName + " with image file " + playerImage + " to the game.");
            }
        }

    }

    public void play() {
        boolean playing = true;
        while (playing) {
            if (gameOver()) {
                Player winner = this.winner();
                System.out.println(winner.getName() + " won the game!");
                playing = false;
            } else {
                Player current = playersList.get(curTurnIndex);
                while (current.getSkipTurn()) {
                    current.turnSkipped();
                    advanceTurn();
                    current = playersList.get(curTurnIndex);
                }
                this.display();
                current.turn();
                advanceTurn();
            }

        }
    }

    private void advanceTurn() {
        System.out.println(curTurnIndex);
        curTurnIndex += (1 * direction);
        while (curTurnIndex < 0) {
            curTurnIndex += numOfPlayers;
            System.out.println("less than loop");
            System.out.println(curTurnIndex);
        }
        while (curTurnIndex >= numOfPlayers) {
            curTurnIndex -= numOfPlayers;
            System.out.println("greater than loop");
            System.out.println(curTurnIndex);
        }
    }

    // changed to public from Java version
    public boolean gameOver() {
        for (Player p : playersList) {
            if (p.getLocation().equals(board.getEnd())) {
                return true;
            }
        }
        return false;
    }

    // changed to public from Java version
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
        playersList.sort(comp);
    }

    public Board getBoard() {
        return board;
    }

    public Dice getDice() {
        return dice;
    }

    // processing input from System.in
    public String readLine() {

        // promptHuman();
        promptApproval();

        if (input.hasNextLine()) {
            return input.nextLine().trim();
        } else {
            return null;
        }
    }

    private void promptHuman() {
        System.out.println("Current turn: " + playersList.get(curTurnIndex).getName());
        System.out.println("Please input a number for your roll.");
    }

    private void promptApproval() {
        System.out.println("Current turn: " + playersList.get(curTurnIndex).getName());
        System.out.println("Please enter anything to roll the dice.");
    }

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
