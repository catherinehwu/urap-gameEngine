package edu.berkeley.hygieneheroes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private static final double ZOOM_INTERVAL = 0.0135;
    private static final int ZOOM_HOLD_COUNT = 100;
    private static final int ZOOM_TIME = 50;
    private static final int HOLD_COUNT = 50;
    private int bigScreenHold = HOLD_COUNT;
    private int hold = ZOOM_HOLD_COUNT;
    private int zoomCount = ZOOM_TIME;
    private boolean zoomIn;
    public boolean zoomMode = false;
    public boolean moveMode = false;
    public boolean holdMode = false;
    public boolean destMode = false;
    public boolean stepMode = false;
    private boolean stepHold = false;
    private static final int STEP_HOLD = 25;
    private int stepHoldTime = STEP_HOLD;

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

        // Skip Turn Processing in Advance
        /*
        while (current.getSkipTurn()) {
            current.turnSkipped();
            advanceTurn();
            current = playersList.get(curTurnIndex);
        }
        */

        return "Current turn: " + current.getName();
    }

    public Player currentPlayer() {
        return playersList.get(curTurnIndex);
    }

    public void activate() {
        if (!zoomMode) {
            zoomMode = true;
        }
    }

    public void step() {
        Player cur = currentPlayer();
        if (cur.getDestination() == null || cur.getLocation() == cur.getDestination()) {
            cur.resetDestination();
            stepMode = false;
            destMode = true;
        } else {
            if (!stepHold) {
                cur.advance();
                stepHold = true;
            } else {
                stepHold();
            }
        }
    }

    private void stepHold() {
        if (stepHoldTime <= 0) {
            stepHoldTime = STEP_HOLD;
            stepHold = false;
        } else {
            stepHoldTime -= 1;
        }
    }

    public void holdProcess() {
        if (bigScreenHold <= 0) {
            stepMode = true;
            holdMode = false;
            bigScreenHold = HOLD_COUNT;
        } else {
            bigScreenHold -= 1;
        }
    }

    public void moveProcess(BoardGameEngine gameUI) {
        // Causes game to resume with a next turn or continuation of previous move
        turnComplete = false;
        Player p = playersList.get(curTurnIndex);

        if (p.determiningAction()) {
            // Dice must be rolled to determine the next action
            turnComplete = p.completeAction(gameUI);
        } else if (p.isSquareAction()) {
            System.out.println("square action");
            p.squareAction(gameUI);
            if (!p.isSquareAction()) {
                System.out.println("no more square action");
                turnComplete = true;
            }
        } else {
            // Completes next turn in the game
            if (p.guiTurn(gameUI)) {
                System.out.println("completed turn");
                turnComplete = true;
            }
        }

        // Only advance turn if turn has completed --> Moved to Zoom Out Part
        // (to keep player tracker correct)

        moveMode = false;
        holdMode = true;
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
                hold -= 1;
                zoomIn = false;
            } else {
                zoomOut(gameUI, increment[2], increment[3]);
            }
        }
    }

    private void zoomIn(BoardGameEngine gameUI, float x, float y) {
        gameUI.camera.zoom -= ZOOM_INTERVAL;

        float distX = x - gameUI.camera.position.x;
        float distY = y - gameUI.camera.position.y;

        gameUI.camera.translate(distX / zoomCount, distY / zoomCount, 0);
        gameUI.camera.update();
    }

    private void zoomOut(BoardGameEngine gameUI, float x, float y) {
        if (zoomCount == ZOOM_TIME) {
            setZoom = true;
            zoomMode = false;
            hold = ZOOM_HOLD_COUNT;
            if (!destMode) {
                moveMode = true;
            } else {
                moveMode = false;
                destMode = false;

                // Only advance turn if turn has completed
                if (turnComplete) {
                    System.out.println("turn advancing");
                    advanceTurn();
                }
            }
        } else {
            float distX = x - gameUI.camera.position.x;
            float distY = y - gameUI.camera.position.y;

            gameUI.camera.zoom += (ZOOM_INTERVAL);
            gameUI.camera.translate(distX / (ZOOM_TIME - zoomCount), distY / (ZOOM_TIME - zoomCount) , 0);
            gameUI.camera.update();
            zoomCount += 1;
        }
    }

    private float[] setZoom(BoardGameEngine gameUI, Player p) {
        float xFraction = ((float) p.getLocation().getX()) / board.getXrange();
        float yFraction = ((float) p.getLocation().getY()) / board.getYrange();
        float x = xFraction * gameUI.boardW;
        float y = yFraction * gameUI.boardH;

        return new float[]{x, y, gameUI.camera.position.x, gameUI.camera.position.y};
    }

    private void advanceTurn() {
        curTurnIndex += (1 * direction);
        while (curTurnIndex < 0) {
            curTurnIndex += numOfPlayers;
        }
        while (curTurnIndex >= numOfPlayers) {
            curTurnIndex -= numOfPlayers;
        }

        while (currentPlayer().getSkipTurn()) {
            currentPlayer().turnSkipped();
            curTurnIndex += (1 * direction);
            while (curTurnIndex < 0) {
                curTurnIndex += numOfPlayers;
            }
            while (curTurnIndex >= numOfPlayers) {
                curTurnIndex -= numOfPlayers;
            }
        }

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
