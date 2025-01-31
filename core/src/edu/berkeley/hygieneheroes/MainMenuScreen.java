package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * MainMenuScreen implements the Screen interface
 * and is a type of screen the BoardGameEngine game
 * can display.
 */
public class MainMenuScreen implements Screen {
    private BoardGameEngine gameUI;
    private GameEngine game;
    private Stage stage;
    private String uiFile = "uiskin.json";

    // For Buttons and Player Name Input
    private Skin skin;
    private TextButton singlePlay;
    private TextButton doublePlay;
    private TextButton triplePlay;
    private TextButton quadPlay;
    private TextButton instr;
    private Label name;
    private Label gameName;
    private Label instruction;
    private TextField player4;
    private TextField player3;
    private TextField player2;
    private TextField player1;

    // Saving state
    private int num;

    public MainMenuScreen(BoardGameEngine GameUI, GameEngine curGame) {
        gameUI = GameUI;
        game = curGame;
        setMainMenu();
    }

    /**
     * Sets up the main menu with 4 buttons for 1, 2, 3, or 4 player game mode.
     * Colors the button MAROON if it isn't an allowed game mode for this specific game.
     * When a button is clicked, will transition into a screen that asks for
     * player's game name.
     */
    private void setMainMenu() {
        // Camera Initiate
        gameUI.cameraScreen();

        stage = new Stage();
        skin = new Skin(Gdx.files.internal(uiFile));

        // Single Player Button
        singlePlay = new TextButton("1 Player", skin);
        singlePlay.setWidth(gameUI.windWidth / 5);
        singlePlay.setHeight(gameUI.windHeight / 5);
        singlePlay.setPosition(gameUI.windWidth / 8, gameUI.windHeight / 3, Align.center);
        singlePlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getPlayerDetails(1);
            }
        });

        // Two Player Button
        doublePlay = new TextButton("2 Player", skin);
        doublePlay.setWidth(gameUI.windWidth / 5);
        doublePlay.setHeight(gameUI.windHeight / 5);
        doublePlay.setPosition(3 * gameUI.windWidth / 8, gameUI.windHeight / 3, Align.center);
        doublePlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getPlayerDetails(2);
            }
        });

        // Three Player Button
        triplePlay = new TextButton("3 Player", skin);
        triplePlay.setWidth(gameUI.windWidth / 5);
        triplePlay.setHeight(gameUI.windHeight / 5);
        triplePlay.setPosition(5 * gameUI.windWidth / 8, gameUI.windHeight / 3, Align.center);
        triplePlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getPlayerDetails(3);
            }
        });

        // Four Player Button
        quadPlay = new TextButton("4 Player", skin);
        quadPlay.setWidth(gameUI.windWidth / 5);
        quadPlay.setHeight(gameUI.windHeight / 5);
        quadPlay.setPosition(7 * gameUI.windWidth / 8, gameUI.windHeight / 3, Align.center);
        quadPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getPlayerDetails(4);
            }
        });

        // Instruction Button
        instr = new TextButton("Back to Instructions", skin);
        instr.setColor(Color.RED);
        instr.setWidth(gameUI.windWidth / 5);
        instr.setHeight(gameUI.windHeight / 10);
        instr.setPosition(gameUI.windWidth / 2, gameUI.windHeight / 6, Align.center);
        instr.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameUI.toInstrScreen();
            }
        });

        // Label for Name
        name = new Label("Hygiene Heroes", skin);
        name.setColor(Color.BLACK);
        name.setX(gameUI.windWidth / 2, Align.center);
        name.setY(gameUI.windHeight);
//        name.setY(gameUI.windHeight - gameUI.windHeight / 12);

        // Label for Specific Game Name
        gameName = new Label(gameUI.gameName, skin);
        gameName.setColor(Color.BLACK);
        gameName.setX(gameUI.windWidth / 2, Align.center);
        gameName.setY(gameUI.windHeight - gameUI.windHeight / 20);

        // Label for Instructions
        instruction = new Label("Choose number of players to start game!", skin);
        instruction.setColor(Color.BLACK);
        instruction.setX(gameUI.windWidth / 2, Align.center);
        instruction.setY(gameUI.windHeight - 3 * gameUI.windHeight / 20);

        // Adding Buttons & Labels to the Stage
        // Removing Unnecessary Buttons - based on Max Num of Players
        /*
        switch(game.maxPlayers) {
            case 4:
                stage.addActor(quadPlay);
            case 3:
                stage.addActor(triplePlay);
            case 2:
                stage.addActor(doublePlay);
            case 1:
                stage.addActor(singlePlay);
                break;
        } */

        // Nulling Out Buttons that are Invalid - due to Max Num Of Players
        switch(game.maxPlayers) {
            case 1:
                doublePlay.setColor(Color.MAROON);
                doublePlay.clearListeners();
            case 2:
                triplePlay.setColor(Color.MAROON);
                triplePlay.clearListeners();
            case 3:
                quadPlay.setColor(Color.MAROON);
                quadPlay.clearListeners();
                break;
            default:
                break;
        }
        stage.addActor(singlePlay);
        stage.addActor(doublePlay);
        stage.addActor(triplePlay);
        stage.addActor(quadPlay);

        // Adding additional actors (labels)
        stage.addActor(instr);
        stage.addActor(name);
        stage.addActor(gameName);
        stage.addActor(instruction);
        stage.setViewport(gameUI.viewport);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Removes the buttons on the main menu screen
     * and switches to a screen where players can input their names.
     * @param num - number of players selected for current game
     */
    private void getPlayerDetails(final int num) {
        this.num = num;
        singlePlay.remove();
        doublePlay.remove();
        triplePlay.remove();
        quadPlay.remove();

        instruction.setText("Enter the name of each player.");
        instruction.setAlignment(Align.center);

        // Adds TextFields for each player to enter their name
        switch(num) {
            case 4:
                player4 = new TextField("Player 4", skin);
                player4.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 15))- 4 * gameUI.windHeight / 6, Align.center);
                Label player4Des = new Label("Type in Player 4's Name", skin);
                player4Des.setColor(Color.BLUE);
                player4Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight)- 4 * gameUI.windHeight / 6, Align.center);
                stage.addActor(player4);
                stage.addActor(player4Des);
            case 3:
                player3 = new TextField("Player 3", skin);
                player3.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 15))- 3 * gameUI.windHeight / 6, Align.center);
                Label player3Des = new Label("Type in Player 3's Name", skin);
                player3Des.setColor(Color.BLUE);
                player3Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight)- 3 * gameUI.windHeight / 6, Align.center);
                stage.addActor(player3);
                stage.addActor(player3Des);
            case 2:
                player2 = new TextField("Player 2", skin);
                player2.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 15))- 2 * gameUI.windHeight / 6, Align.center);
                Label player2Des = new Label("Type in Player 2's Name", skin);
                player2Des.setColor(Color.BLUE);
                player2Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight)- 2 * gameUI.windHeight / 6, Align.center);
                stage.addActor(player2);
                stage.addActor(player2Des);
            case 1:
                player1 = new TextField("Player 1", skin);
                player1.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 15))- gameUI.windHeight / 6, Align.center);
//                player1.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 5))- gameUI.windHeight / 6, Align.center);
                Label player1Des = new Label("Type in Player 1's Name", skin);
                player1Des.setColor(Color.BLUE);
                player1Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight) - gameUI.windHeight / 6, Align.center);
//                player1Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 8))- gameUI.windHeight / 6, Align.center);
                stage.addActor(player1);
                stage.addActor(player1Des);
                break;
        }

        // Submission Button that processes player names & starts the game
        TextButton submit = new TextButton("Start Game!", skin);
        submit.setColor(Color.GREEN);
        submit.setPosition(gameUI.windWidth / 2, gameUI.windHeight / 15, Align.center);
        submit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.clear();
                switch(num){
                    case 4:
                        String p4Name = player4.getText();
                        gameUI.setPlayer(p4Name, 4);
                    case 3:
                        String p3Name = player3.getText();
                        gameUI.setPlayer(p3Name, 3);
                    case 2:
                        String p2Name = player2.getText();
                        gameUI.setPlayer(p2Name, 2);
                    case 1:
                        String p1Name = player1.getText();
                        gameUI.setPlayer(p1Name, 1);
                        break;
                }

                // If single player, sets the game up as a Two Player game with AI
                if (num == 1) {
                    gameUI.setAI("Computer Player", 2);
                    gameUI.buttonSetGame(num + 1);
                } else {
                    gameUI.buttonSetGame(num);
                }
            }
        });
        stage.addActor(submit);
    }

    /**
     * Sets up the main menu again so that all the buttons are valid
     * and will listen for input. If it was at beginning, only sets up the screen
     * asking for number of players. If number of players was already specified,
     * sets up the screen asking for player names.
     */
    public void reactivate() {
        setMainMenu();
        if (num != 0) {
            getPlayerDetails(num);
        }
    }

    @Override
    public void render(float delta) {
        gameUI.cameraScreen();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameUI.batch.begin();
        stage.draw();
        gameUI.batch.end();
    }

    // Unused methods
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
        stage.dispose();
        skin.dispose();
    }
}
