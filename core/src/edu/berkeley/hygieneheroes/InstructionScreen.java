package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class InstructionScreen implements Screen {
    private BoardGameEngine gameUI;
    private Stage stage;
    private String uiFile = "uiskin.json";
    private Skin skin;

    private Label name;
    private Label gameName;
    private Label instruction;
    private TextButton start;

    public InstructionScreen(BoardGameEngine GameUI) {
        gameUI = GameUI;
        setInstructionScreen();
    }

    private void setInstructionScreen() {
        gameUI.cameraScreen();

        stage = new Stage();
        skin = new Skin(Gdx.files.internal(uiFile));

        // Label for Name
        name = new Label("Hygiene Heroes", skin);
        name.setColor(Color.BLUE);
        name.setX(gameUI.windWidth / 2, Align.center);
        name.setY(gameUI.windHeight);

        // Label for Specific Game Name
        gameName = new Label(gameUI.gameName, skin);
        gameName.setColor(Color.BLUE);
        gameName.setX(gameUI.windWidth / 2, Align.center);
        gameName.setY(gameUI.windHeight - gameUI.windHeight / 20);

        // Label for Instructions
        FileHandle instrFile = Gdx.files.internal(gameUI.instructionsFile);
        String instr = instrFile.readString();
        instruction = new Label(instr, skin);
        instruction.setColor(Color.BLACK);
        instruction.setX(gameUI.windWidth / 2, Align.center);
        instruction.setY(gameUI.windHeight - 10 * gameUI.windHeight / 20);

        // Start Game Button
        start = new TextButton("Start Game", skin);
        start.setWidth(gameUI.windWidth / 5);
        start.setHeight(gameUI.windHeight / 10);
        start.setPosition(gameUI.windWidth / 2, gameUI.windHeight / 5, Align.center);
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameUI.toNextScreen();
            }
        });

        stage.addActor(name);
        stage.addActor(gameName);
        stage.addActor(instruction);
        stage.addActor(start);
        stage.setViewport(gameUI.viewport);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

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
        //stage.dispose();
        //skin.dispose();
    }
}
