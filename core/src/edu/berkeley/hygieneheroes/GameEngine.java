package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.*;

/**
 * GameEngine represents the underlying model for
 * the game. It keeps tracks of the board, the squares,
 * the players, the dice, etc. It also keeps track of the
 * current player.
 */
public class GameEngine {
    // Game Features
    private Dice dice;
    private Board board;
    private ArrayList<PlayerGroup> playersList;
    private int curTurnIndex;
    private int direction;
    private int numOfPlayers;
    public int maxPlayers;
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

    // Default Square Sounds HashMap
    private HashMap<String, String> actionSounds = new HashMap<>();

    // Changing constructor to take in floats
    public GameEngine(float Xrange, float Yrange, int squareTotal) {
        board = new Board(Xrange, Yrange, squareTotal);
        playersList = new ArrayList<>();
        curTurnIndex = 0;
        direction = 1;
        tokensPerPlayer = 1; // DEFAULT VALUES
        maxPlayers = 4; // DEFAULT VALUES
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

        // default sounds
        actionSounds.put("A", "defaultSounds/a.wav");
        actionSounds.put("B", "defaultSounds/b.wav");
        actionSounds.put("C", "defaultSounds/c.wav");
        actionSounds.put("D", "defaultSounds/d.wav");
        actionSounds.put("E", "defaultSounds/e.wav");
        actionSounds.put("F", "defaultSounds/f.wav");
        actionSounds.put("G", "defaultSounds/g.wav");

        //clicking for movement not enabled
        clickToken = false;
    }

    // Constructor that takes in number of tokens per player
    public GameEngine(float Xrange, float Yrange, int squareTotal, int tokens) {
        this(Xrange, Yrange, squareTotal);
        tokensPerPlayer = tokens;
    }

    // Constructor that takes in number of max players
    public GameEngine(float Xrange, float Yrange, int squareTotal, int tokens, int maxNumPlayers) {
        this(Xrange, Yrange, squareTotal, tokens);
        maxPlayers = maxNumPlayers;
    }

    // Adds a square with specified location and settings to the board
    public void addSquare(int num, float sqX, float sqY,
                          String picture, String text, String sound,
                          ArrayList<String> listOfActions) {
        board.setSquare(num, sqX, sqY, picture, text, sound, listOfActions);
    }

    // Adds a player with specified name and token image to the game
    public void addPlayer(String name, String imageFile, int num) {
        ArrayList<Player> tokens = new ArrayList<Player>();
        for (int i = 0; i < tokensPerPlayer; i += 1) {
            tokens.add(new Player(name, imageFile, this, num, i)); // PRINTING WILL BE UGLY
        }

        // Creating a PlayerGroup for Human Player
        PlayerGroup newPlayer = new PlayerGroup(tokens, name, board);
        playersList.add(newPlayer);
        sortPlayers();
    }

    // Adds a player with specified name and token animated images to the game
    public void addPlayer(String name, String[] imageFiles, int num) {
        ArrayList<Player> tokens = new ArrayList<Player>();
        for (int i = 0; i < tokensPerPlayer; i += 1) {
            tokens.add(new Player(name, imageFiles, this, num, i)); // PRINTING WILL BE UGLY
        }

        // Creating a PlayerGroup for Human Player
        PlayerGroup newPlayer = new PlayerGroup(tokens, name, board);
        playersList.add(newPlayer);
        sortPlayers();
    }

    // Adds an AI player with specified image to the game
    public void addAI(String name, String imageFile, int num) {
        ArrayList<Player> tokens = new ArrayList<Player>();
        for (int i = 0; i < tokensPerPlayer; i += 1) {
            tokens.add(new Player(name, imageFile, this, num, i)); // PRINTING WILL BE UGLY
        }

        // Creating an AI Computer PlayerGroup
        PlayerGroup computer = new PlayerGroup(tokens, name, board,true);
        playersList.add(computer);
        sortPlayers();
    }

    // Adds an AI player with specified image files (animated) to the game
    public void addAI(String name, String[] imageFiles, int num) {
        ArrayList<Player> tokens = new ArrayList<Player>();
        for (int i = 0; i < tokensPerPlayer; i += 1) {
            tokens.add(new Player(name, imageFiles, this, num, i)); // PRINTING WILL BE UGLY
        }

        // Creating an AI Computer PlayerGroup
        PlayerGroup computer = new PlayerGroup(tokens, name, board,true);
        playersList.add(computer);
        sortPlayers();
    }

    // Reverses the turn cycling direction of the game
    public void reverse() {
        direction *= -1;
    }

    // Returns the name of the current player
    public String currentTurnStr() {
        PlayerGroup current = playersList.get(curTurnIndex);
        return current.getName();
    }

    // Returns the PlayerGroup object representing the current player
    public PlayerGroup currentPlayer() {
        return playersList.get(curTurnIndex);
    }

    /**
     * If the click token feature is not enabled, simply sets the current token to
     * be the player's first token that is not already at the finish line.
     *
     * If the click token feature is enabled, sets the current token to be the token the player
     * clicked on (with a small room for buffer). If the click is not within the buffer range of
     * any of the player's tokens, sends a message back to the game and turn is not completed or
     * activated.
     *
     * Activating the turn involves picking the token to move and then calling the
     * helper function activate - which begins the GUI zooming in / out and panning
     * functions.
     *
     * @param x - x coordinate for touch point
     * @param y - y coordinate for touch point
     */
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

    /**
     * Activate function for AI Computer Player. AI simply choses a random token
     * to move (as long as the token isn't already at the finish line).
     *
     * Calls helper function activate to being zooming in/out and panning functions.
     */
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

    /**
     * Initiates the GUI zooming in / out and movement procedure.
     */
    public void activate() {
        if (!zoomMode) {
            zoomMode = true;
        }
    }

    /**
     * Moves the player's token from current location to destination (As specified
     * by what they rolled on the dice). Moves token one square at a time, pausing at
     * each square and making a short tap sound each time.
     *
     * If the movement is automatic from a square action (and not from rolling the dice), a
     * different sound is played and no individual taps are made.
     */
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

    /**
     * Helper function to keep track of the pause time at each square.
     */
    private void stepHold() {
        if (stepHoldTime <= 0) {
            stepHoldTime = STEP_HOLD;
            stepHold = false;
        } else {
            stepHoldTime -= 1;
        }
    }

    /**
     * Function to keep track of the pause time between zooming in/out on
     * start location and the initialization of moving the token pieces. Pause time
     * on the large screen board.
     */
    public void holdProcess() {
        if (bigScreenHold <= 0) {
            stepMode = true;
            holdMode = false;
            bigScreenHold = HOLD_COUNT;
        } else {
            bigScreenHold -= 1;
        }
    }

    /**
     * Initializes the game action determining steps (i.e rolling a dice
     * or continuing a previous move due to a square's action).
     *
     * Keeps track of whether the move being processed is due to a roll or from
     * a previous square action. Keeps track of whether or not the dice was rolled
     * and if step sounds should be made. If the turn is complete after this move
     * (as in the turn goes to the next player), changes the instance variable turncomplete
     * so that the turn will advance later.
     *
     * Initiates the rollMode and holdMode.
     *
     * @param gameUI - used for displaying GUI in other methods that are called
     */
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

    /**
     * Overall function that controls the zooming in and out process.
     * Times the zooming in and out and transitions from one to another.
     * First, uses the setZoom helper function to set the necessary variables
     * needed for a smooth zoom in / out. Calls zoom in and zoom out based on
     * a counter / timer.
     *
     * @param gameUI - used for zooming in/out (changing camera)
     * @param p - the player token to zoom into
     */
    public void zoomProcess(BoardGameEngine gameUI, Player p) {
        if (setZoom) {
            increment = setZoom(gameUI, p);
            zoomIn = true;
            setZoom = false;
            if (destMode && p.getLocation().getSound() != null) {
                String file = p.getLocation().getSound();
                Sound sqSound = Gdx.audio.newSound(Gdx.files.internal(file));
                sqSound.play();
                p.getLocation().resetSound();
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

    /**
     * Helper function for zooming in.
     * @param gameUI - used for zooming in/out (changing camera)
     * @param x - destination x location for zoom in
     * @param y - destination y location for zoom in
     */
    private void zoomIn(BoardGameEngine gameUI, float x, float y) {
        gameUI.camera.zoom -= ZOOM_INTERVAL;

        float distX = x - gameUI.camera.position.x;
        float distY = y - gameUI.camera.position.y;

        gameUI.camera.translate(distX / zoomCount, distY / zoomCount, 0);
        gameUI.camera.update();
    }

    /**
     * Helper function for zooming out.
     *
     * When zooming out terminates, initializes the following processes based on condition:
     *      1. starts the moving mode (if this was the zoom in/out procedure for start location)
     *      2. checks to see if turn was completed and advances turn
     *      (if this was zoom in/out procedure for end location)
     *      3. if turn advanced and next player is AI, automatically activate AI's turn
     *
     * @param gameUI - used for zooming in/out (changing camera)
     * @param x - destination x location for zoom out
     * @param y - destination y location for zoom out
     */
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

    /**
     * Helper function that finds the location and variables needed for zooming in/out calculations.
     *
     * @param gameUI - used for zooming in / out (finding camera location and position_
     * @param p - player token to zoom in on
     * @return a 4 element array with the x and y positions of the player's token and
     * the x and y positions of the GUI camera
     */
    private float[] setZoom(BoardGameEngine gameUI, Player p) {
        float x = p.getLocation().getX();
        float y = p.getLocation().getY();
        return new float[]{x, y, gameUI.camera.position.x, gameUI.camera.position.y};
    }


    public void setSoundInList(String key, String soundFile) {
        if (key != null) {
            actionSounds.put(key, soundFile);
        }
    }

    public String getSoundFromList(String key) {
        if (actionSounds.containsKey(key)) {
            return actionSounds.get(key);
        }
        return null;
    }

    /**
     * Advances the turn by incrementing the curTurnIndex.
     * If the curTurnIndex goes out of bounds of the length of the players list,
     * modulo like math is performed to make it within bounds again.
     *
     * Keeps incrementing the turn index until it finds a player
     * whose turn is not supposed to be skipped. For every player it encounters in which
     * the player's turn should be skipped, resets the player's settings to state that
     * turn has already been skipped.
     *
     * If next turn is AI, automatically initiates AI's turn and move.
     */
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

        // AI Turn Implementation
        if(currentPlayer().isComputerPlayer()) {
            System.out.println("true");
            this.activateAI();
        }
    }

    /**
     * Displays animation for a rolling dice. Cycles through different
     * dice faces and images and lands on the one specified by
     * the player's roll. Pauses between each image to create a rolling effect.
     *
     * @param gameUI - GUI instance to display on
     */
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

    /**
     * Checks to see if any player has won. A player wins
     * when all of his/her tokens have made it to the end square.
     * @return true if the game is over - a player has won
     */
    public boolean gameOver() {
        for (PlayerGroup player : playersList) {
            if (player.finished()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assumes the game is over and there is a winner.
     * Finds the winner and returns the winner as a
     * PlayerGroup object
     * @return the player (PlayerGroup object) that won the game.
     * If there is no winner, returns null.
     */
    public PlayerGroup winner() {
        for (PlayerGroup player : playersList) {
            if (player.finished()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Sorts the PlayerGroups based on the alphabet.
     */
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

    // accessor (set/get) methods
    public ArrayList<PlayerGroup> getPlayersList() {
        return playersList;
    }

    public void setNumOfPlayers(int num) {
        numOfPlayers = num;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * Sets the clickToken attribute to COND.
     * clickToken should be true if designer wants the feature
     * of having to click on a token before rolling to specify
     * which token will be moved.
     * @param cond condition to set clickToken to
     */
    public void setClickToken(boolean cond) {
        clickToken = cond;
    }

    // output for troubleshooting
    public void display() {
        System.out.println();
        System.out.print("==========");
        for (PlayerGroup p : playersList) {
            System.out.println(p.getName() + " is at :");
            for (Player token : p.getTokens()) {
                System.out.println(token.getLocation().getSeqNum());
            }
        }
        System.out.println("==========");
    }
}
