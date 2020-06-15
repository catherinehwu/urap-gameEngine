package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class BoardGameEngine extends Game {
	// Game Specific Config Input
	private GameEngine game;
	public String victory = "victory.wav";
	public String configFileName = "dentalColor.txt";
	public String configImage = "dentalColor.png";
//	private String configFileName = "dental.txt";
//	private String configImage = "rectangularBoard.png";

	// Overall GUI
	public SpriteBatch batch;
	public BitmapFont font;
	public GlyphLayout layout;
	public OrthographicCamera camera;
	public FitViewport viewport;

	// Scaling Game Board
	public float boardW;
	public float boardH;
	public float windWidth;
	public float windHeight;
	private final int CONSTANTW = 960;
	private float ratio;

	// Message Bar (FIXME - MESSAGE BAR)
	public int messageHeight = 150;
	public int messageAvgLen = 100;
	public int messagePad = 20;

	@Override
	public void create () {
		// Overall Shared GUI
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		layout = new GlyphLayout();

		// Set up Game
		try {
			initialize();
			System.out.println("done init");
		} catch (Exception e){
			System.out.println("error");
			System.out.println(e.getMessage());
		}

		// Set up Camera & Viewport
		camera = new OrthographicCamera();
		viewport = new FitViewport(boardW, boardH + messageHeight, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

		// Main Menu Screen with Stage & Buttons
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	public void cameraScreen() {
		camera.update();
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
	}

	public void buttonSetGame(int num) {
		game.setNumOfPlayers(num);
		this.setScreen(new GameScreen(this, this.game));
	}

	private void initialize() {
		// General Config File
		FileHandle configText = Gdx.files.internal(configFileName);

		// Regex approach
		String config = configText.readString();
		String[] lines = config.split("\n");
		String setUp = lines[0];
		String[] setUpSettings = setUp.split(" ");

		float rowNum = Float.valueOf(setUpSettings[0]);
		float colNum = Integer.valueOf(setUpSettings[1]);
		int endPosNum = Integer.valueOf(setUpSettings[2]);

		// Scaling Changes
		ratio = CONSTANTW / rowNum;
		boardW = windWidth = rowNum * ratio;
		boardH = windHeight = colNum * ratio;

		// FIXME - DEBUGGING LINES
		System.out.println(CONSTANTW + " " + boardW);
		System.out.println(ratio);
		System.out.println(boardW);
		System.out.println(boardH);

		game = new GameEngine(rowNum, colNum, endPosNum);

		for (int i = 1; i < lines.length; i += 1) {
			setUpSquare(lines[i]);
		}

	}

	private void setUpSquare(String settings) {
		String[] line = settings.split(" ");
		int seqNum = Integer.valueOf(line[0]);
		float xVal = Float.valueOf(line[1]) * ratio;
		float yVal = Float.valueOf(line[2]) * ratio;

		String[] attributes = new String[3];
		for (int i = 0; i < 3; i += 1) {
			attributes[i] = line[i + 3];
			if (attributes[i].equals("*")) {
				attributes[i] = null;
			}
		}

		ArrayList<String> listOfActions = new ArrayList<>();
		for (int i = 6; i < line.length; i += 1) {
			listOfActions.add(line[i]);
		}

		game.addSquare(seqNum, xVal, yVal,
				attributes[0], attributes[1], attributes[2], listOfActions);

	}

	// Setting an individual player with default image
	public void setPlayer(String name, int num) {
		String image = "player" + num + ".png";
		game.addPlayer(name, image, num);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2,0);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
	}
}
