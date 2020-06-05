package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.*;
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
	// Overall GUI
	public SpriteBatch batch;
	public BitmapFont font;
	public GlyphLayout layout;

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
	private int windWidth = 800;
	private int windHeight = 480;
	private int newWindW = 920;
	public int boardW = 800;
	public int boardH = 480;

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

		// Background board image
		texture = new Texture(Gdx.files.internal("rectangularBoard.png"));
		boardWorld = new Sprite(texture);
		boardWorld.setPosition(0,0);
		boardWorld.setSize(boardW, boardH);
		float ratio = (float)Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
		camera = new OrthographicCamera();
		viewport = new FitViewport(boardW, boardH, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

		// Using Image Class
//		background = new Image(texture);
//		Vector2 pos = Scaling.fit.apply(texture.getWidth(), texture.getHeight(), imageW, imageH);
//		background.setSize(pos.x, pos.y);

		// Game Functionality
		mainMenu = true;
		gameNotOver = true;
		winner = null;
		gameMessage = "";
		gameMessNum = 0;

		// Main Menu Screen with Stage & Buttons
		setMainMenu();

//		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		cameraScreen();
		if (mainMenu) {
			// Camera set up
			// cameraScreen();

			// BUTTON MAIN MENU
			mainMenuButton();

			// ORIGINAL MAIN MENU
//			mainMenu();

			// Switch from Main to Game for ORIGINAL MAIN MENU
			if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)
					|| Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1)) {
				setGame(1);
			} else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)
					|| Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
				setGame(2);
			} else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)
					|| Gdx.input.isKeyPressed(Input.Keys.NUMPAD_3)) {
				setGame(3);
			} else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)
					|| Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)) {
				setGame(4);
			}

		} else {
			// GAME SCREEN
			gameScreen();

			// checking for wins
			if (gameNotOver && game.gameOver()) {
				gameNotOver = false;
				winner = game.winner();
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
		singlePlay.setWidth(150);
		singlePlay.setHeight(100);
		singlePlay.setPosition(windWidth / 8, 150, Align.center);
		singlePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlayerDetails(1);
			}
		});

		// Two Player Button
		doublePlay = new TextButton("2 Player", skin);
		doublePlay.setWidth(150);
		doublePlay.setHeight(100);
		doublePlay.setPosition(3 * windWidth / 8, 150, Align.center);
		doublePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlayerDetails(2);
			}
		});

		// Three Player Button
		triplePlay = new TextButton("3 Player", skin);
		triplePlay.setWidth(150);
		triplePlay.setHeight(100);
		triplePlay.setPosition(5 * windWidth / 8, 150, Align.center);
		triplePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlayerDetails(3);
			}
		});

		// Four Player Button
		quadPlay = new TextButton("4 Player", skin);
		quadPlay.setWidth(150);
		quadPlay.setHeight(100);
		quadPlay.setPosition(7 * windWidth / 8, 150, Align.center);
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
				player4.setPosition(windWidth / 2, (windHeight - 90)- 4 * windHeight / 6, Align.center);
				Label player4Des = new Label("Type in Player 4's Name", skin);
				player4Des.setColor(Color.BLUE);
				player4Des.setPosition(windWidth / 2, (windHeight - 60)- 4 * windHeight / 6, Align.center);
				stage.addActor(player4);
				stage.addActor(player4Des);
			case 3:
				player3 = new TextField("Player 3", skin);
				player3.setPosition(windWidth / 2, (windHeight - 90)- 3 * windHeight / 6, Align.center);
				Label player3Des = new Label("Type in Player 3's Name", skin);
				player3Des.setColor(Color.BLUE);
				player3Des.setPosition(windWidth / 2, (windHeight - 60)- 3 * windHeight / 6, Align.center);
				stage.addActor(player3);
				stage.addActor(player3Des);
			case 2:
				player2 = new TextField("Player 2", skin);
				player2.setPosition(windWidth / 2, (windHeight - 90)-  2 * windHeight / 6, Align.center);
				Label player2Des = new Label("Type in Player 2's Name", skin);
				player2Des.setColor(Color.BLUE);
				player2Des.setPosition(windWidth / 2, (windHeight - 60)- 2 * windHeight / 6, Align.center);
				stage.addActor(player2);
				stage.addActor(player2Des);
			case 1:
				player1 = new TextField("Player 1", skin);
				player1.setPosition(windWidth / 2, (windHeight - 90)- windHeight / 6, Align.center);
				Label player1Des = new Label("Type in Player 1's Name", skin);
				player1Des.setColor(Color.BLUE);
				player1Des.setPosition(windWidth / 2, (windHeight - 60)- windHeight / 6, Align.center);
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

	private void mainMenu() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		layout.setText(font, "Hygiene Heroes", Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0,  height / 2 + layout.height / 2 + 100);

		layout.setText(font, "Dental Hygiene Game", Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2);

		layout.setText(font, "Enter number of players (1 ~ 4) to start game!", Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 - 100);

		batch.end();
	}

	private void winningScreen(int lineHeight) {
//		int width = Gdx.graphics.getWidth();
//		int height = Gdx.graphics.getHeight();
		int width = boardW;
		int height = boardH;
		layout.setText(font, "Winner: " + winner.getName(), Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);
	}

	private void setUpGameScreen() {
		// Stage Game Screen Version
		stage.clear();
		background.setPosition(0,0);
//		WidgetGroup backgroundImage = new WidgetGroup(background);
//		backgroundImage.setPosition(0,0);
		stage.addActor(background);

		Label name = new Label("Dental Game Board", skin);
		name.setColor(Color.BLACK);
//		name.setAlignment(Align.left, Align.center);
//		name.setPosition(background.getImageWidth(), 0);
//		WidgetGroup labelling = new WidgetGroup(name);
//		labelling.setPosition(backgroundImage.getX(), 0);
//		SplitPane split = new SplitPane(backgroundImage, labelling, false, skin);
//		stage.addActor(split);
		stage.addActor(name);

		Table table = new Table(skin);
		table.add("Testing Board");
		table.row();
		table.add(name);
		table.setColor(Color.WHITE);
		table.setPosition(background.getImageWidth(), 200);
		stage.addActor(table);
		stage.draw();
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

		// Stage Game Screen Version
		// setUpGameScreen();

		// Temporary variables
		int width = boardW;
		int height = boardH;

		layout.setText(font, "Dental Game Board", Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 + 100);

		layout.setText(font, "Players: " + numOfPlayers, Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 + 50);

		int lineHeight = 0;
		if (game != null) {
			for (Player p : game.getPlayersList()) {
				layout.setText(font, p.getName(), Color.BLACK, width, Align.center, true);
				font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);
				p.draw(this);
				lineHeight += 50;
			}
		}

		if (gameNotOver) {
			layout.setText(font, game.currentTurnStr(), Color.BLACK, width, Align.center, true);
			font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);
			lineHeight += 50;

			layout.setText(font, "Tap or press space to roll.", Color.BLACK, width, Align.center, true);
			font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);

			if (game.zoomMode || game.destMode) {
				// Zooming in on a player's piece before movement
				// Zooming in on a player's piece after movement
				System.out.println("zooming");
				game.zoomProcess(this, game.currentPlayer());
			} else if (game.moveMode) {
				// Moving player's piece and advancing turn
				System.out.println("game movement processing - half");
				game.moveProcess(this);
			} else if (game.holdMode) {
				// Holding screen at zoom out mode after piece has moved
				System.out.println("holding in outside large screen");
				game.holdProcess();
			} else if (game.stepMode) {
				System.out.println("moving piece to destination");
				game.step();
			} else if (game.currentPlayer().isSquareAction()) {
				game.activate();
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
				// Initiate a game move
				System.out.println("activated");
				game.activate();
			}

			// Displays message about the details of the last special move
			displayGameMessage();

		} else {
			// Displays Winning Screen Message if Game Over
			winningScreen(lineHeight);
		}
		batch.end();
	}

	public void setGameMessage(String message, int num) {
		// Setting the game message (moved to sq #) for a specific player NUM
		gameMessage = message;
		gameMessNum = num;
	}
	private void displayGameMessage() {
		font.draw(batch, gameMessage, 0, 100 - 20 * gameMessNum);
	}

	private void setGame(int num) {
		numOfPlayers = num;
		mainMenu = false;
		try {
			initialize();
			setPlayers();
		} catch (Exception e) {
			// do something - File Reading Errors
		}
	}

	private void buttonSetGame(int num) {
		numOfPlayers = num;
		mainMenu = false;
		try {
			initialize();
			game.setNumOfPlayers(num);
			stage.clear();
		} catch (Exception e) {
			// do something - File Reading Errors
		}
	}

	private void initialize() throws FileNotFoundException {
		// Currently Reading Config File for Dental Game
		FileHandle configText = Gdx.files.internal("dental.txt");

		// Regex approach
		String config = configText.readString();
		String[] lines = config.split("\n");
		String setUp = lines[0];
		String[] setUpSettings = setUp.split(" ");

		int rowNum = Integer.valueOf(setUpSettings[0]);
		int colNum = Integer.valueOf(setUpSettings[1]);
		int endPosNum = Integer.valueOf(setUpSettings[2]);
		game = new GameEngine(rowNum, colNum, endPosNum);

		for (int i = 1; i < lines.length; i += 1) {
			setUpSquare(lines[i]);
		}

	}

	private void setUpSquare(String settings) {
		String[] line = settings.split(" ");
		int seqNum = Integer.valueOf(line[0]);
		int xVal = Integer.valueOf(line[1]);
		int yVal = Integer.valueOf(line[2]);

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

	// Setting up players for default input (with keyboard)
	private void setPlayers() {
		for (int i = 1; i <= numOfPlayers; i += 1) {
			String name = "Player " + i;
			String image = "player" + i + ".png";
			game.addPlayer(name, image, i);
		}
		game.setNumOfPlayers(numOfPlayers);
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
	}
}
