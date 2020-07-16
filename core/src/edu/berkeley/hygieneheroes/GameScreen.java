package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

/**
 * GameScreen implements the Screen interface
 * and is one of the possible screens displayed by the
 * BoardGameEngine game. Displayed when the game
 * is in play.
 */
public class GameScreen implements Screen {
    public SpriteBatch batch;

    private BoardGameEngine gameUI;
    private Texture texture;
    private Sprite boardWorld;
    private GlyphLayout layout;
    private BitmapFont font;
    private GameEngine game;
    private Music victory;
    private Vector3 touchPos;

    private float boardW;
    private float boardH;
    private boolean gameNotOver;
    private PlayerGroup winner;
    private Texture congrats;

    // Message Bar Above the Board Game Image
    private int messageHeight;
    private int messageAvgLen;
    private int messagePad;

    /**
     * Constructor for a GameScreen. Sets up variables in this screen
     * to store the same values and references as those in the
     * BoardGameEngine object that is passed in. Creates the necessary
     * sprites and textures for GUI.
     */
    public GameScreen(BoardGameEngine GameUI, GameEngine curGame) {
        gameUI = GameUI;
        gameNotOver = true;

        // Transferring Values
        boardH = gameUI.boardH;
        boardW = gameUI.boardW;
        messageAvgLen = gameUI.messageAvgLen;
        messageHeight = gameUI.messageHeight;
        messagePad = gameUI.messagePad;
        batch = gameUI.batch;
        layout = gameUI.layout;
        font = gameUI.font;
        game = curGame;

        // General Game Board
        texture = new Texture(Gdx.files.internal(gameUI.configImage));

        // Set up Game Board
        boardWorld = new Sprite(texture);
        boardWorld.setPosition(0,0);
        boardWorld.setSize(boardW, boardH);

        touchPos = new Vector3();

    }

    @Override
    public void render(float delta) {
        gameUI.cameraScreen();
        gameScreen();

        // Initializes the Winning Screen Settings if and only if game just finished
        // (and all animations - zooming in / out have ended)
        // Resets the board on display and starts victory music
        if (gameNotOver && game.gameOver() && !game.stepMode && !game.destMode) {
            gameNotOver = false;
            winner = game.winner();
            victory = Gdx.audio.newMusic(Gdx.files.internal(gameUI.victory));
            victory.play();
            victory.setLooping(true);

            congrats = new Texture(Gdx.files.internal(gameUI.winningPage));
            boardWorld = new Sprite(congrats);
            boardWorld.setPosition(boardW / 2 - boardH / 2, 0);
            boardWorld.setSize(boardH, boardH);
        }
    }

    private void gameScreen() {
        batch.begin();

        // Moving camera part over
        boardWorld.draw(batch);

        // Message Bar Above the Game Board Image
        layout.setText(font, "Game Messages", Color.BLACK, boardW, Align.center, true);
        font.draw(batch, layout, 0, boardH + messageHeight - messagePad);

        layout.setText(font, "Dental Game Board", Color.BLACK, boardW, Align.center, true);
        font.draw(batch, layout, 0, boardH + messageHeight - 2 * layout.height - messagePad);

        layout.setText(font, "Player Square Action Messages:", Color.BLACK, boardW, Align.left, true);
        font.draw(batch, layout, messagePad, boardH + messageHeight - messagePad);

        layout.setText(font, "Players: " + game.getNumOfPlayers(), Color.BLACK, boardW, Align.left, true);
        font.draw(batch, layout, boardW - messageAvgLen - messagePad, boardH + messageHeight - messagePad);

        if (gameNotOver) {
            if (game != null) {
                // Draws each token for each player p
                for (PlayerGroup p : game.getPlayersList()) {
                    for (Player token : p.getTokens()) {
                        token.draw(gameUI);
                    }
                }
            }

            layout.setText(font, "Current turn: " + game.currentTurnStr(), Color.RED, boardW, Align.center, true);
            font.draw(batch, layout, 0, boardH + messageHeight - 4 * layout.height - messagePad);

            layout.setText(font, "Tap or press space to roll.", Color.BLACK, boardW, Align.center, true);
            font.draw(batch, layout, 0, boardH + messageHeight - 6 * layout.height - messagePad);

            if (game.zoomMode || game.destMode) {
                // Zooming in on a player's piece before movement
                // Zooming in on a player's piece after movement
//                 System.out.println("zooming");
                game.zoomProcess(gameUI, game.currentPlayer().getCurrentToken());
            } else if (game.moveMode) {
                // Moving player's piece and advancing turn
//                 System.out.println("game movement processing - half");
                game.moveProcess(gameUI);
            } else if (game.rollMode) {
                // Showing an image of the rolling dice
                game.rollGui(gameUI);
            } else if (game.holdMode) {
                // Holding screen at zoom out mode after piece has moved
//                 System.out.println("holding in outside large screen");
                game.holdProcess();
            } else if (game.stepMode) {
                // Showing pieces moving step by step with sound
//                 System.out.println("moving piece to destination");
                game.step();
            } else if (game.currentPlayer().getCurrentToken() != null && game.currentPlayer().getCurrentToken().isSquareAction()
                && !game.currentPlayer().getCurrentToken().getChanceAction()) {
                // Automatically continuing action for square actions (like move to this square)
                // Only automatic activation when the square action IS NOT draw a chance card
                game.activate();
            } else if (game.currentPlayer().isComputerPlayer()) {
                // Automatically making the AI move
                game.activateAI();
            } else if (Gdx.input.isTouched()) {
                // REMOVE SPACE BAR OPTION - Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                System.out.println("activated");

                // Initiate a game move
                // Getting the position touched by the player
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

                /* Debugging Lines
                System.out.println(touchPos.x + " " + touchPos.y);
                System.out.println(gameUI.viewport.getScreenX());
                System.out.println(gameUI.viewport.getScreenY());
                System.out.println(gameUI.viewport.getScreenWidth());
                System.out.println(gameUI.viewport.getScreenHeight());
                System.out.println(boardW + " " + boardH);
                 */

                // Calculating x coordinate by subtracting the viewport's starting xPos from touchPos.x
                float x = touchPos.x - gameUI.viewport.getScreenX();

                // Calculating y coordinate by first finding the offset from viewport's starting yPos
                // subtracting both touchPos.y and viewport's starting yPos from total screen height because
                // screen's y axis has 0 at the top rather than the bottom.
                float y = Gdx.graphics.getHeight() - touchPos.y - gameUI.viewport.getScreenY();

                // Finding the ratios for the xPos with respect to the whole viewport's size
                float xRatio = x / gameUI.viewport.getScreenWidth();
                float yRatio = y / gameUI.viewport.getScreenHeight();

                // Activating the touch based on board units using the ratio conversion
                System.out.println( (xRatio * boardW) + " " + (yRatio * (boardH + messageHeight)));
                game.activate((xRatio * boardW), (yRatio * (boardH + messageHeight)));
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                gameUI.toInstrScreen();
            }

            // Show Dice
            showDice();

        } else {
            // Displays Winning Screen Message if Game Over
            winningScreen();
        }
        batch.end();
    }

    private void showDice() {
        if (game.diceFace != null) {
            batch.draw(game.diceFace, game.diceX, game.diceY, game.diceW, game.diceH);
        }
    }

    private void winningScreen() {
        layout.setText(font, "Winner: " + winner.getName(), Color.RED, boardW, Align.center, true);
        font.draw(batch, layout, 0, boardH + messageHeight - 8 * layout.height - messagePad);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        gameUI.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        victory.dispose();
        texture.dispose();
    }
}
