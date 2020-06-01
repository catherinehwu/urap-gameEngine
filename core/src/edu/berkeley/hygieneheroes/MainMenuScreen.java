package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {
    final BoardGameEngine gameUI;
    OrthographicCamera camera;
    private Stage stage;
    private TextButton player1;

    public MainMenuScreen(BoardGameEngine GameUI) {
        gameUI = GameUI;

        // Camera - Size 800 x 480
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Button Handling
        stage = new Stage();
        stage.setViewport(new FitViewport(800, 400));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = gameUI.font;
        player1 = new TextButton("Single Player", style);
        player1.setColor(Color.GOLD);
        player1.setSize(200, 100);
        player1.setPosition(100, 200);
        stage.addActor(player1);

        // Testing Third Party Source
        Button button2 = new TextButton("Text Button",style);
        button2.setSize(50, 50);
        button2.setPosition(200,200);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                // outputLabel.setText("Press a Button");
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                // outputLabel.setText("Pressed Text Button");
                return true;
            }
        });
        stage.addActor(button2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.796875f, 0.796875f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        camera.update();
//        gameUI.batch.setProjectionMatrix(camera.combined);

		gameUI.batch.begin();
		gameUI.font.draw(gameUI.batch, "Hygiene Heroes", 350, 300);
//        stage.act();
//        stage.draw();
		gameUI.batch.end();


    }

    // Unused methods
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
