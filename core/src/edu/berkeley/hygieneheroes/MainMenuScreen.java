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

public class MainMenuScreen implements Screen {
    private BoardGameEngine gameUI;
    private Stage stage;
    private String uiFile = "uiskin.json";

    // From Before
    private Skin skin;
    private TextButton singlePlay;
    private TextButton doublePlay;
    private TextButton triplePlay;
    private TextButton quadPlay;
    private Label name;
    private Label gameName;
    private Label instruction;
    private TextField player4;
    private TextField player3;
    private TextField player2;
    private TextField player1;

    public MainMenuScreen(BoardGameEngine GameUI) {
        gameUI = GameUI;
        setMainMenu();
    }

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

        // Label for Name
        name = new Label("Hygiene Heroes", skin);
        name.setColor(Color.BLACK);
        name.setX(gameUI.windWidth / 2, Align.center);
        name.setY(gameUI.windHeight - gameUI.windHeight / 12);

        // Label for Specific Game Name
        gameName = new Label("Dental Hygiene Game", skin);
        gameName.setColor(Color.BLACK);
        gameName.setX(gameUI.windWidth / 2, Align.center);
        gameName.setY(gameUI.windHeight - 2 * gameUI.windHeight / 12);

        // Label for Instructions
        instruction = new Label("Choose number of players to start game!", skin);
        instruction.setColor(Color.BLACK);
        instruction.setX(gameUI.windWidth / 2, Align.center);
        instruction.setY(gameUI.windHeight - 3 * gameUI.windHeight / 12);

        // Adding Buttons & Labels to the Stage
        stage.addActor(singlePlay);
        stage.addActor(doublePlay);
        stage.addActor(triplePlay);
        stage.addActor(quadPlay);
        stage.addActor(name);
        stage.addActor(gameName);
        stage.addActor(instruction);
        stage.setViewport(gameUI.viewport);
        Gdx.input.setInputProcessor(stage);
    }

    private void getPlayerDetails(final int num) {
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
                player4.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 5))- 4 * gameUI.windHeight / 6, Align.center);
                Label player4Des = new Label("Type in Player 4's Name", skin);
                player4Des.setColor(Color.BLUE);
                player4Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 8))- 4 * gameUI.windHeight / 6, Align.center);
                stage.addActor(player4);
                stage.addActor(player4Des);
            case 3:
                player3 = new TextField("Player 3", skin);
                player3.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 5))- 3 * gameUI.windHeight / 6, Align.center);
                Label player3Des = new Label("Type in Player 3's Name", skin);
                player3Des.setColor(Color.BLUE);
                player3Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 8))- 3 * gameUI.windHeight / 6, Align.center);
                stage.addActor(player3);
                stage.addActor(player3Des);
            case 2:
                player2 = new TextField("Player 2", skin);
                player2.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 5))- 2 * gameUI.windHeight / 6, Align.center);
                Label player2Des = new Label("Type in Player 2's Name", skin);
                player2Des.setColor(Color.BLUE);
                player2Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 8))- 2 * gameUI.windHeight / 6, Align.center);
                stage.addActor(player2);
                stage.addActor(player2Des);
            case 1:
                player1 = new TextField("Player 1", skin);
                player1.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 5))- gameUI.windHeight / 6, Align.center);
                Label player1Des = new Label("Type in Player 1's Name", skin);
                player1Des.setColor(Color.BLUE);
                player1Des.setPosition(gameUI.windWidth / 2, (gameUI.windHeight - (gameUI.windHeight / 8))- gameUI.windHeight / 6, Align.center);
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
                gameUI.buttonSetGame(num);
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
            }
        });
        stage.addActor(submit);
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
