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
    private boolean setZoom = true;
    private float[] increment;
    private static final int HOLD_COUNT = 75;
    private static final int ZOOM_TIME = 50;
    private int bigScreenHold = HOLD_COUNT;
    private int hold = HOLD_COUNT;
    private int zoomCount = ZOOM_TIME;
    private boolean zoomIn;
    public boolean zoomMode = false;
    public boolean moveMode = false;
    public boolean holdMode = false;
    public boolean destMode = false;

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
//        turnComplete = false;
//        Player p = playersList.get(curTurnIndex);

        if (!zoomMode) {
            zoomMode = true;
            return;
        }

        /*
        // Human controlled zoom
        if (setZoom) {
            increment = setZoom(gameUI, p);
            zoomIn = true;
            setZoom = false;
            return;
        } else {
            if (zoomIn & (zoomCount > 0)) {
                zoomIn(gameUI, increment[0], increment[1]);
                zoomCount -= 1;
                return;
            } else if (zoomCount == 0) {
//                gameUI.camera.position.set(increment[2], increment[3], 0);
                zoomOut(gameUI, increment[2], increment[3]);
                zoomIn = false;
            } else {
                zoomOut(gameUI, increment[2], increment[3]);
            }
        }
         */

        /*
        if (p.determiningAction()) {
            // Dice must be rolled to determine the next action
            turnComplete = p.completeAction(gameUI);
        } else {
            // Completes next turn in the game
            if (p.guiTurn(gameUI)) {
                turnComplete = true;
            }
        }

        // Only advance turn if turn has completed
        if (turnComplete) {
            advanceTurn();
        }
        */

        // Reset Zoom
        // zoomOut(gameUI, increment[2], increment[3]);

    }

    public void holdProcess() {
        System.out.println("hold");
        if (bigScreenHold <= 0) {
            destMode = true;
            holdMode = false;
            bigScreenHold = HOLD_COUNT;
        } else {
            bigScreenHold -= 1;
        }
    }

    public void moveProcess(BoardGameEngine gameUI) {
        turnComplete = false;
        Player p = playersList.get(curTurnIndex);

        if (p.determiningAction()) {
            // Dice must be rolled to determine the next action
            turnComplete = p.completeAction(gameUI);
        } else {
            // Completes next turn in the game
            if (p.guiTurn(gameUI)) {
                turnComplete = true;
            }
        }

        // Only advance turn if turn has completed --> Moved to Zoom Out Part
        // (to keep player tracker correct)

        moveMode = false;
        holdMode = true;
//        destMode = true;
    }

    public void zoomProcess(BoardGameEngine gameUI, Player p) {
        if (setZoom) {
            increment = setZoom(gameUI, p);
            zoomIn = true;
            setZoom = false;
            return;
        } else {
            if (zoomIn & (zoomCount > 0)) {
                zoomIn(gameUI, increment[0], increment[1]);
                zoomCount -= 1;
                return;
            } else if ((zoomCount == 0) && hold > 0) {
//                zoomOut(gameUI, increment[2], increment[3]);
                hold -= 1;
                zoomIn = false;
            } else {
                zoomOut(gameUI, increment[2], increment[3]);
            }
        }
    }

    private void zoomIn(BoardGameEngine gameUI, float x, float y) {
        gameUI.camera.zoom -= 0.015;

        float distX = x - gameUI.camera.position.x;
        float distY = y - gameUI.camera.position.y;

        gameUI.camera.translate(distX / zoomCount, distY / zoomCount, 0);
        gameUI.camera.update();
        System.out.println("in");
    }

    private void zoomOut(BoardGameEngine gameUI, float x, float y) {
        System.out.println("out");
        if (zoomCount == ZOOM_TIME) {
            setZoom = true;
            zoomMode = false;
            hold = HOLD_COUNT;
            if (!destMode) {
                moveMode = true;
            } else {
                moveMode = false;
                destMode = false;

                // Only advance turn if turn has completed
                if (turnComplete) {
                    advanceTurn();
                }
            }
        } else {
            float distX = x - gameUI.camera.position.x;
            float distY = y - gameUI.camera.position.y;

            gameUI.camera.zoom += (0.015);
            gameUI.camera.translate(distX / (ZOOM_TIME - zoomCount), distY / (ZOOM_TIME - zoomCount) , 0);
            gameUI.camera.update();
            zoomCount += 1;
        }

        // Quick Zoom Out
//        gameUI.camera.zoom += (0.015) * 50;
//        gameUI.camera.position.set(x, y, 0);
//        zoomCount = 50;
//        setZoom = true;

        // Mixture
//        gameUI.camera.position.set(x, y, 0);
//        while (zoomCount < 50) {
//            gameUI.camera.zoom += (0.015);
//            zoomCount += 1;
//        }
//        setZoom = true;

        // Slow Quick Zoom Out
//        while (zoomCount < 50) {
//            System.out.println("zoomcount " + zoomCount);
//            gameUI.camera.zoom += 0.015;
//
//            float distX = x - gameUI.camera.position.x;
//            float distY = y - gameUI.camera.position.y;
//
//            gameUI.camera.translate(x / (50 - zoomCount), y / (50 - zoomCount) , 0);
//            gameUI.camera.update();
//            zoomCount += 1;
//            setZoom = true;
//        }
    }

    private float[] setZoom(BoardGameEngine gameUI, Player p) {
        float xFraction = ((float) p.getLocation().getX()) / board.getXrange();
        float yFraction = ((float) p.getLocation().getY()) / board.getYrange();
        float x = xFraction * gameUI.boardW;
        float y = yFraction * gameUI.boardH;

        return new float[]{x, y, gameUI.camera.position.x, gameUI.camera.position.y};

//        float distX = gameUI.camera.position.x - x;
//        float distY = gameUI.camera.position.y - y;

//        return new float[]{distX / 50, distY / 50};
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
