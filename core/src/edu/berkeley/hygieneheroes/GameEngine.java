package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GameEngine {
    // Game Features
    private Dice dice;
    private Board board;
    private ArrayList<PlayerGroup> playersList;
    private int curTurnIndex;
    private int direction;
    private int numOfPlayers;
    public int tokensPerPlayer;
    private Random rand;

    // GUI Dice Images
    private ArrayList<Texture> die;
    public Texture diceFace;
    public boolean rollMode = false;
    private int ROLL_REPEAT = 12;
    private int count = 0;
    private int pauseCount = 0;
    private int DICE_PAUSE_TIME = 5;
    public float diceW;
    public float diceH;
    public float diceX;
    public float diceY;

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
    private boolean clickToken;

    // Sound Effects
    private Sound stepTap;
    private Music flySound;
    private boolean stepSound;

    // Changing constructor to take in floats
    public GameEngine(float Xrange, float Yrange, int squareTotal) {
        board = new Board(Xrange, Yrange, squareTotal);
        playersList = new ArrayList<>();
        curTurnIndex = 0;
        direction = 1;
        tokensPerPlayer = 1;
        rand = new Random();

        //standard dice with 1~6
        dice = new Dice(6);
        die = new ArrayList<Texture>();
        Texture die1 = new Texture(Gdx.files.internal("die1.png"));
        Texture die2 = new Texture(Gdx.files.internal("die2.png"));
        Texture die3 = new Texture(Gdx.files.internal("die3.png"));
        Texture die4 = new Texture(Gdx.files.internal("die4.png"));
        Texture die5 = new Texture(Gdx.files.internal("die5.png"));
        Texture die6 = new Texture(Gdx.files.internal("die6.png"));
        die.add(die1);
        die.add(die2);
        die.add(die3);
        die.add(die4);
        die.add(die5);
        die.add(die6);

        // sound
        stepTap = Gdx.audio.newSound(Gdx.files.internal("step.wav"));
        flySound = Gdx.audio.newMusic(Gdx.files.internal("whee.wav"));

        //clicking for movement not enabled
        clickToken = false;
    }

    public GameEngine(float Xrange, float Yrange, int squareTotal, int tokens) {
        this(Xrange, Yrange, squareTotal);
        tokensPerPlayer = tokens;
    }

    public void addSquare(int num, float sqX, float sqY,
                          String picture, String text, String sound,
                          ArrayList<String> listOfActions) {
        board.setSquare(num, sqX, sqY, picture, text, sound, listOfActions);
    }

    public void addPlayer(String name, String imageFile, int num) {
        ArrayList<Player> tokens = new ArrayList<Player>();
        for (int i = 0; i < tokensPerPlayer; i += 1) {
            tokens.add(new Player(name, imageFile, this, num, i)); // PRINTING WILL BE UGLY
        }
        PlayerGroup newPlayer = new PlayerGroup(tokens, name, board);
        playersList.add(newPlayer);

//        Player newPerson = new Player(name, imageFile, this, num);
//        playersList.add(newPerson);
        sortPlayers();
    }

    // FIXME AI Implementation
    public void addAI(String name, String imageFile, int num) {
        ArrayList<Player> tokens = new ArrayList<Player>();
        for (int i = 0; i < tokensPerPlayer; i += 1) {
            tokens.add(new ComputerPlayer(name, imageFile, this, num, i)); // PRINTING WILL BE UGLY
        }
        PlayerGroup computer = new PlayerGroup(tokens, name, board,true);
        playersList.add(computer);

//        Player computer = new ComputerPlayer(name, imageFile, this, num);
//        playersList.add(computer);
        sortPlayers();
    }

    public void reverse() {
        direction *= -1;
    }

    public String currentTurnStr() {
        PlayerGroup current = playersList.get(curTurnIndex);

//        Player current = playersList.get(curTurnIndex);
        return current.getName();
    }

    // FIXME changed to player group
    public PlayerGroup currentPlayer() {
        return playersList.get(curTurnIndex);
    }

    public void activate(float x, float y) {
        if (currentPlayer().getCurrentToken() == null) {
            if (!clickToken) {
                for (int i = 1; i <= currentPlayer().getTokens().size(); i += 1) {
                    Player token = currentPlayer().getTokenNumber(i);
                    if (!token.getLocation().equals(board.getEnd())) {
                        currentPlayer().setCurrentToken(i);
                        return;
                    }
                }
            }

            if (tokensPerPlayer == 1) {
                currentPlayer().setCurrentToken(1);
            } else {
                int buffer = 32;
                for (int i = 1; i <= currentPlayer().getTokens().size(); i += 1) {
                    System.out.println(currentPlayer().getTokens().size());
                    Player token = currentPlayer().getTokenNumber(i);
                    float locX = token.getLocation().getX();
                    float locY = token.getLocation().getY();
                    System.out.println("touch: " + x + " " + y);
                    System.out.println("token: " + locX + " " + locY);
                    System.out.println(locX <= x && locX + buffer >= x);
                    System.out.println(locY <= y && locY + buffer >= y);
                    if (locX <= x && locX + buffer >= x &&
                        locY <= y && locY + buffer >= y) {
                        currentPlayer().setCurrentToken(i);
                        break;
                    }
                }
                if (currentPlayer().getCurrentToken() == null) {
                    currentPlayer().getTokenNumber(1).setMessage("Click token to move!");
                    return;
                }
            }
        }
        activate();
    }

    public void activateAI() {
        if (currentPlayer().getCurrentToken() == null) {
            if (tokensPerPlayer == 1) {
                currentPlayer().setCurrentToken(1);
            } else {
                int range = currentPlayer().getTokens().size();
                int index = rand.nextInt(range) + 1;
                while (currentPlayer().getTokenNumber(index).getLocation().getSeqNum() == board.getEnd().getSeqNum()) {
                    index = rand.nextInt(range) + 1;
                }
                currentPlayer().setCurrentToken(index);
            }
        }
        activate();
    }

    public void activate() {
        if (!zoomMode) {
            zoomMode = true;
        }
    }

    // FIXME changed to current token
    public void step() {
        Player cur = currentPlayer().getCurrentToken();
        if (cur.getDestination() == null || cur.getLocation() == cur.getDestination()) {
            cur.resetDestination();
            if (flySound.isPlaying()) {
                flySound.stop();
            }
            stepMode = false;
            destMode = true;
        } else {
            if (!stepHold) {
                cur.advance();
                if (stepSound) {
                    stepTap.play();
                } else if (!flySound.isPlaying()) {
                    flySound.play();
                    flySound.setLooping(true);
                }
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

    // FIXME changed to get current token
    public void moveProcess(BoardGameEngine gameUI) {
        // Causes game to resume with a next turn or continuation of previous move
        turnComplete = false;
        stepSound = true;
        boolean rolled = false;
        Player p = playersList.get(curTurnIndex).getCurrentToken();

        if (p.determiningAction()) {
            // Dice must be rolled to determine the next action
            turnComplete = p.completeAction(gameUI);
            stepSound = false;
            rolled = true;
        } else if (p.isSquareAction()) {
            System.out.println("square action");
            p.squareAction(gameUI);
            if (!p.isSquareAction()) {
                System.out.println("no more square action");
                turnComplete = true;
            }
            stepSound = false;
        } else {
            // Completes next turn in the game
            if (p.guiTurn(gameUI)) {
                System.out.println("completed turn");
                turnComplete = true;
            }
            rolled = true;
        }

        // Only advance turn if turn has completed --> Moved to Zoom Out Part
        // (to keep player tracker correct)

        moveMode = false;
        if (rolled) {
            rollMode = true;
        }
        holdMode = true;
    }

    public void zoomProcess(BoardGameEngine gameUI, Player p) {
        if (setZoom) {
            increment = setZoom(gameUI, p);
            zoomIn = true;
            setZoom = false;
            if (destMode && p.getLocation().getSquareSound() != null) {
                String file = p.getLocation().getSquareSound();
                Sound sqSound = Gdx.audio.newSound(Gdx.files.internal(file));
                sqSound.play();
            }
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
                    currentPlayer().resetCurrentToken();
                    advanceTurn();
                } else if (currentPlayer().isComputerPlayer()) {
                    this.activateAI();
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
        float x = p.getLocation().getX();
        float y = p.getLocation().getY();
        return new float[]{x, y, gameUI.camera.position.x, gameUI.camera.position.y};
    }

    // FIXME changed to get current token
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

        // FIXME AI Implementation
        if(currentPlayer().isComputerPlayer()) {
            System.out.println("true");
            this.activateAI();
        }
    }

    // FIXME changed to get current token
    public void rollGui(BoardGameEngine gameUI) {
        Player current = currentPlayer().getCurrentToken();
        diceX = gameUI.boardW - gameUI.messageAvgLen - gameUI.messagePad - gameUI.messageHeight;
        diceY = gameUI.boardH;
        diceW = gameUI.messageHeight;
        diceH = gameUI.messageHeight;
        if (current.getPrevRoll() != 0) {
            if (pauseCount > 0) {
                pauseCount -= 1;
            } else if (count < ROLL_REPEAT + current.getPrevRoll()) {
                diceFace = die.get(count % 6);
                count += 1;
                pauseCount = DICE_PAUSE_TIME;
            } else if (count == ROLL_REPEAT + current.getPrevRoll()) {
                rollMode = false;
                count = 0;
            }
        }
    }

    public boolean gameOver() {
        for (PlayerGroup player : playersList) {
            if (player.finished()) {
                return true;
            }
        }
        return false;
//        for (Player p : playersList) {
//            if (p.getLocation().equals(board.getEnd())) {
//                return true;
//            }
//        }
//        return false;
    }

    // FIXME changed to player group
    public PlayerGroup winner() {
        for (PlayerGroup player : playersList) {
            if (player.finished()) {
                return player;
            }
        }
        return null;

//        for (Player p : playersList) {
//            if (p.getLocation().equals(board.getEnd())) {
//                return p;
//            }
//        }
//        return null;
    }

    private void sortPlayers() {
        Comparator<PlayerGroup> comp = new AlphabetComparator();
        Collections.sort(playersList, comp);
    }

    public Board getBoard() {
        return board;
    }

    public Dice getDice() {
        return dice;
    }

    // output for troubleshooting
    // FIXME player group
    public void display() {
        System.out.println();
        System.out.print("==========");
        for (PlayerGroup p : playersList) {
            System.out.println(p.getName() + " is at :");
            for (Player token : p.getTokens()) {
                System.out.println(token.getLocation().getSeqNum());
            }
//            System.out.println(p.getName() + " is at " + p.getLocation().getSeqNum());
        }
        System.out.println("==========");
    }

    // accessor methods
    public ArrayList<PlayerGroup> getPlayersList() {
        return playersList;
    }

    public void setNumOfPlayers(int num) {
        numOfPlayers = num;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setClickToken(boolean cond) {
        clickToken = cond;
    }
}
