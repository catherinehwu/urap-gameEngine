package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class BoardGameEngine implements ApplicationListener {
	// Game Specific Config Input
	private String configFileName = "dentalColor.txt";
	private String configImage = "dentalColor.png";
//	private String configFileName = "dental.txt";
//	private String configImage = "rectangularBoard.png";

	// Overall GUI
	public SpriteBatch batch;
	public BitmapFont font;
	public GlyphLayout layout;
	private Music victory;

	// Game Functionality Parameters
	private boolean mainMenu;
	private boolean gameNotOver;
	private Player winner;
	private int numOfPlayers;
	private GameEngine game;
	private String gameMessage;
	private int gameMessNum;

	// Background Image Layout
	public OrthographicCamera camera;
	public FitViewport viewport;
	private Sprite boardWorld;
	private Texture texture;
	private Image background;

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

	// Main Menu with Stage & Buttons
	private Stage stage;
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

	@Override
	public void create () {
		// Overall GUI
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

		// General Game Board
		texture = new Texture(Gdx.files.internal(configImage));

		// Set up Game Board, Camera, Viewport
		boardWorld = new Sprite(texture);
		boardWorld.setPosition(0,0);
		boardWorld.setSize(boardW, boardH);
		camera = new OrthographicCamera();
		viewport = new FitViewport(boardW, boardH + messageHeight, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

		// Game Functionality
		mainMenu = true;
		gameNotOver = true;
		winner = null;
		gameMessage = "";
		gameMessNum = 0;

		// Main Menu Screen with Stage & Buttons
		setMainMenu();

	}

	@Override
	public void render () {
		cameraScreen();
		if (mainMenu) {
			// BUTTON MAIN MENU
			mainMenuButton();

		} else {
			// GAME SCREEN
			gameScreen();

			// checking for wins
			if (gameNotOver && game.gameOver()) {
				gameNotOver = false;
				winner = game.winner();
				victory = Gdx.audio.newMusic(Gdx.files.internal("victory.wav"));
				victory.play();
				victory.setLooping(true);
			}
		}
	}

	private void setMainMenu() {
		// Camera Initiate
		cameraScreen();

		stage = new Stage();
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		// Single Player Button
		singlePlay = new TextButton("1 Player", skin);
		singlePlay.setWidth(windWidth / 5);
		singlePlay.setHeight(windHeight / 5);
		singlePlay.setPosition(windWidth / 8, windHeight / 3, Align.center);
		singlePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlayerDetails(1);
			}
		});

		// Two Player Button
		doublePlay = new TextButton("2 Player", skin);
		doublePlay.setWidth(windWidth / 5);
		doublePlay.setHeight(windHeight / 5);
		doublePlay.setPosition(3 * windWidth / 8, windHeight / 3, Align.center);
		doublePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlayerDetails(2);
			}
		});

		// Three Player Button
		triplePlay = new TextButton("3 Player", skin);
		triplePlay.setWidth(windWidth / 5);
		triplePlay.setHeight(windHeight / 5);
		triplePlay.setPosition(5 * windWidth / 8, windHeight / 3, Align.center);
		triplePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlayerDetails(3);
			}
		});

		// Four Player Button
		quadPlay = new TextButton("4 Player", skin);
		quadPlay.setWidth(windWidth / 5);
		quadPlay.setHeight(windHeight / 5);
		quadPlay.setPosition(7 * windWidth / 8, windHeight / 3, Align.center);
		quadPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlayerDetails(4);
			}
		});

		// Label for Name
		name = new Label("Hygiene Heroes", skin);
		name.setColor(Color.BLACK);
		name.setX(windWidth / 2, Align.center);
		name.setY(windHeight - windHeight / 12);

		// Label for Specific Game Name
		gameName = new Label("Dental Hygiene Game", skin);
		gameName.setColor(Color.BLACK);
		gameName.setX(windWidth / 2, Align.center);
		gameName.setY(windHeight - 2 * windHeight / 12);

		// Label for Instructions
		instruction = new Label("Choose number of players to start game!", skin);
		instruction.setColor(Color.BLACK);
		instruction.setX(windWidth / 2, Align.center);
		instruction.setY(windHeight - 3 * windHeight / 12);

		// Adding Buttons & Labels to the Stage
		stage.addActor(singlePlay);
		stage.addActor(doublePlay);
		stage.addActor(triplePlay);
		stage.addActor(quadPlay);
		stage.addActor(name);
		stage.addActor(gameName);
		stage.addActor(instruction);
		stage.setViewport(viewport);
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
				player4.setPosition(windWidth / 2, (windHeight - (windHeight / 5))- 4 * windHeight / 6, Align.center);
				Label player4Des = new Label("Type in Player 4's Name", skin);
				player4Des.setColor(Color.BLUE);
				player4Des.setPosition(windWidth / 2, (windHeight - (windHeight / 8))- 4 * windHeight / 6, Align.center);
				stage.addActor(player4);
				stage.addActor(player4Des);
			case 3:
				player3 = new TextField("Player 3", skin);
				player3.setPosition(windWidth / 2, (windHeight - (windHeight / 5))- 3 * windHeight / 6, Align.center);
				Label player3Des = new Label("Type in Player 3's Name", skin);
				player3Des.setColor(Color.BLUE);
				player3Des.setPosition(windWidth / 2, (windHeight - (windHeight / 8))- 3 * windHeight / 6, Align.center);
				stage.addActor(player3);
				stage.addActor(player3Des);
			case 2:
				player2 = new TextField("Player 2", skin);
				player2.setPosition(windWidth / 2, (windHeight - (windHeight / 5))- 2 * windHeight / 6, Align.center);
				Label player2Des = new Label("Type in Player 2's Name", skin);
				player2Des.setColor(Color.BLUE);
				player2Des.setPosition(windWidth / 2, (windHeight - (windHeight / 8))- 2 * windHeight / 6, Align.center);
				stage.addActor(player2);
				stage.addActor(player2Des);
			case 1:
				player1 = new TextField("Player 1", skin);
				player1.setPosition(windWidth / 2, (windHeight - (windHeight / 5))- windHeight / 6, Align.center);
				Label player1Des = new Label("Type in Player 1's Name", skin);
				player1Des.setColor(Color.BLUE);
				player1Des.setPosition(windWidth / 2, (windHeight - (windHeight / 8))- windHeight / 6, Align.center);
				stage.addActor(player1);
				stage.addActor(player1Des);
				break;
		}

		// Submission Button that processes player names & starts the game
		TextButton submit = new TextButton("Start Game!", skin);
		submit.setColor(Color.GREEN);
		submit.setPosition(windWidth / 2, windHeight / 15, Align.center);
		submit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				buttonSetGame(num);
				switch(num){
					case 4:
						String p4Name = player4.getText();
						setPlayer(p4Name, 4);
					case 3:
						String p3Name = player3.getText();
						setPlayer(p3Name, 3);
					case 2:
						String p2Name = player2.getText();
						setPlayer(p2Name, 2);
					case 1:
						String p1Name = player1.getText();
						setPlayer(p1Name, 1);
						break;
				}
			}
		});
		stage.addActor(submit);
	}

	private void mainMenuButton() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		stage.draw();
		batch.end();
	}

	private void winningScreen() {
		layout.setText(font, "Winner: " + winner.getName(), Color.BLACK, boardW, Align.center, true);
		font.draw(batch, layout, 0, boardH + messageHeight - 8 * layout.height - messagePad);
	}

	private void cameraScreen() {
		camera.update();
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
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

		layout.setText(font, "Players: " + numOfPlayers, Color.BLACK, boardW, Align.left, true);
		font.draw(batch, layout, boardW - messageAvgLen - messagePad, boardH + messageHeight - messagePad);

		if (game != null) {
			for (Player p : game.getPlayersList()) {
				p.draw(this);
			}
		}

		if (gameNotOver) {
			layout.setText(font, game.currentTurnStr(), Color.RED, boardW, Align.center, true);
			font.draw(batch, layout, 0, boardH + messageHeight - 4 * layout.height - messagePad);

			layout.setText(font, "Tap or press space to roll.", Color.BLACK, boardW, Align.center, true);
			font.draw(batch, layout, 0, boardH + messageHeight - 6 * layout.height - messagePad);

			if (game.zoomMode || game.destMode) {
				// Zooming in on a player's piece before movement
				// Zooming in on a player's piece after movement
				// System.out.println("zooming");
				game.zoomProcess(this, game.currentPlayer());
			} else if (game.moveMode) {
				// Moving player's piece and advancing turn
				// System.out.println("game movement processing - half");
				game.moveProcess(this);
			} else if (game.rollMode) {
				// Showing an image of the rolling dice
				game.rollGui(this);
			} else if (game.holdMode) {
				// Holding screen at zoom out mode after piece has moved
				// System.out.println("holding in outside large screen");
				game.holdProcess();
			} else if (game.stepMode) {
				// System.out.println("moving piece to destination");
				game.step();
			} else if (game.currentPlayer().isSquareAction()) {
				game.activate();
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
				// Initiate a game move
				// System.out.println("activated");
				game.activate();
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

	private void buttonSetGame(int num) {
		numOfPlayers = num;
		mainMenu = false;
		game.setNumOfPlayers(num);
		stage.clear();
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
	private void setPlayer(String name, int num) {
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
		texture.dispose();
		stage.dispose();
		skin.dispose();
		victory.dispose();
	}
}
