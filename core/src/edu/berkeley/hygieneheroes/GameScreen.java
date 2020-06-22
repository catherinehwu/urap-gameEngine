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

    // Message Bar (FIXME - MESSAGE BAR)
    private int messageHeight;
    private int messageAvgLen;
    private int messagePad;

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

        // Message Bar (FIXME - MESSAGE BAR)
        layout.setText(font, "Game Messages", Color.BLACK, boardW, Align.center, true);
        font.draw(batch, layout, 0, boardH + messageHeight - messagePad);

        layout.setText(font, "Dental Game Board", Color.BLACK, boardW, Align.center, true);
        font.draw(batch, layout, 0, boardH + messageHeight - 2 * layout.height - messagePad);

        layout.setText(font, "Player Square Action Messages:", Color.BLACK, boardW, Align.left, true);
        font.draw(batch, layout, messagePad, boardH + messageHeight - messagePad);

        layout.setText(font, "Players: " + game.getNumOfPlayers(), Color.BLACK, boardW, Align.left, true);
        font.draw(batch, layout, boardW - messageAvgLen - messagePad, boardH + messageHeight - messagePad);

//        if (game != null) {
//            for (Player p : game.getPlayersList()) {
//                p.draw(gameUI);
//            }
//        }

        if (gameNotOver) {
            if (game != null) {
                // FIXME change to player group
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
                // System.out.println("zooming");
                game.zoomProcess(gameUI, game.currentPlayer().getCurrentToken());
            } else if (game.moveMode) {
                // Moving player's piece and advancing turn
                // System.out.println("game movement processing - half");
                game.moveProcess(gameUI);
            } else if (game.rollMode) {
                // Showing an image of the rolling dice
                game.rollGui(gameUI);
            } else if (game.holdMode) {
                // Holding screen at zoom out mode after piece has moved
                // System.out.println("holding in outside large screen");
                game.holdProcess();
            } else if (game.stepMode) {
                // System.out.println("moving piece to destination");
                game.step();
            } else if (game.currentPlayer().getCurrentToken() != null && game.currentPlayer().getCurrentToken().isSquareAction()) {
                game.activate();
            } else if (game.currentPlayer().isComputerPlayer() && game.currentPlayer().getCurrentToken() == null) {
                game.activateAI();
            } else if (Gdx.input.isTouched()) {
                // REMOVE SPACE BAR OPTION - Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                // Initiate a game move
                System.out.println("activated");
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//                gameUI.camera.unproject(touchPos);
//                gameUI.camera.unproject(touchPos);
                System.out.println(touchPos.x + " " + touchPos.y);
                System.out.println(gameUI.viewport.getScreenX());
                System.out.println(gameUI.viewport.getScreenY());
                System.out.println(gameUI.viewport.getScreenWidth());
                System.out.println(gameUI.viewport.getScreenHeight());
                System.out.println(boardW + " " + boardH);
//                System.out.println(gameUI.viewport.getWorldWidth() + " " + (gameUI.viewport.getWorldHeight() - gameUI.messageHeight));
//                gameUI.camera.unproject(touchPos, 0, 0, gameUI.boardW, gameUI.boardH);

//                gameUI.camera.unproject(touchPos, gameUI.viewport.getScreenX(), gameUI.viewport.getScreenY(), gameUI.boardW, gameUI.boardH);


//                gameUI.camera.unproject(touchPos, gameUI.viewport.getScreenX(), gameUI.viewport.getScreenY(), gameUI.boardW, gameUI.boardH);
//                System.out.println(temp.x + " " + temp.y);
//                game.activate(touchPos.x, touchPos.y);
                float x = touchPos.x - gameUI.viewport.getScreenX();
                float y = gameUI.viewport.getScreenHeight() - touchPos.y;
                float xRatio = x / gameUI.viewport.getScreenWidth();
                float yRatio = y / gameUI.viewport.getScreenHeight();
                System.out.println( (xRatio * boardW) + " " + (yRatio * (boardH + messageHeight)));
                game.activate((xRatio * boardW), (yRatio * (boardH + messageHeight)));

//                System.out.println((touchPos.x - gameUI.viewport.getScreenX()) + " " + (boardH + messageHeight - touchPos.y));
//                game.activate((touchPos.x - gameUI.viewport.getScreenX()), (boardH + messageHeight - touchPos.y));
//                game.activate();
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
