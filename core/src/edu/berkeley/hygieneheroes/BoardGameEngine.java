package edu.berkeley.hygieneheroes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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
	private Sprite sprite;
	private Texture texture;
	private int windWidth = 800;
	private int windHeight = 480;

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
		sprite = new Sprite(texture);

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
//		super.render();
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();

		if (mainMenu) {
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

		} else if (gameNotOver){
			// GAME SCREEN
			gameScreen();

			// checking for wins
			if (game.gameOver()) {
				gameNotOver = false;
				winner = game.winner();
			}
		} else {
			// WINNING - GAME OVER SCREEN
			winningScreen();
		}
	}

	private void setMainMenu() {
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("uiskin.json"));

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

		name = new Label("Hygiene Heroes", skin);
		name.setColor(Color.BLACK);
		name.setX(windWidth / 2, Align.center);
		name.setY(windHeight - windHeight / 12);

		gameName = new Label("Dental Hygiene Game", skin);
		gameName.setColor(Color.BLACK);
		gameName.setX(windWidth / 2, Align.center);
		gameName.setY(windHeight - 2 * windHeight / 12);

		instruction = new Label("Choose number of players to start game!", skin);
		instruction.setColor(Color.BLACK);
		instruction.setX(windWidth / 2, Align.center);
		instruction.setY(windHeight - 3 * windHeight / 12);

		stage.addActor(singlePlay);
		stage.addActor(doublePlay);
		stage.addActor(triplePlay);
		stage.addActor(quadPlay);
		stage.addActor(name);
		stage.addActor(gameName);
		stage.addActor(instruction);
		Gdx.input.setInputProcessor(stage);
	}

	private void getPlayerDetails(final int num) {
		singlePlay.remove();
		doublePlay.remove();
		triplePlay.remove();
		quadPlay.remove();

		instruction.setText("Enter the name of each player.");
		instruction.setAlignment(Align.center);

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

	private void winningScreen() {
		Gdx.gl.glClearColor(0, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		batch.draw(texture, 0, 0, 800, 480);

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

		layout.setText(font, "Winner: " + winner.getName(), Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);
		batch.end();
	}

	private void gameScreen() {
		Gdx.gl.glClearColor(0, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		batch.draw(texture, 0, 0, 800, 480);

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

		layout.setText(font, game.currentTurnStr(), Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);
		lineHeight += 50;

		layout.setText(font, "Tap or press space to roll.", Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);

		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
			// Making a game move
			game.activate(this);
		}

		// Displays message about the details of the last special move
		displayGameMessage();
		batch.end();
	}

	public void setGameMessage(String message, int num) {
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
		} catch (Exception e) {
			// do something - File Reading Errors
		}
	}

	private void initialize() throws FileNotFoundException {
		// Currently Reading Config File for Dental Game
		FileHandle configText = Gdx.files.internal("dental.txt");
		Scanner config = new Scanner(configText.file());
		Scanner setUp = new Scanner(config.nextLine());
		int rowNum = setUp.nextInt();
		int colNum = setUp.nextInt();
		int endPosNum = setUp.nextInt();
		game = new GameEngine(rowNum, colNum, endPosNum);

		while (config.hasNextLine()) {
			String nextSqSettings = config.nextLine();
			setUpSquare(nextSqSettings);
		}
	}

	private void setUpSquare(String settings) {
		Scanner sqSettings = new Scanner(settings);
		int seqNum = sqSettings.nextInt();
		int xVal = sqSettings.nextInt();
		int yVal = sqSettings.nextInt();

		String[] attributes = new String[3];
		for (int i = 0; i < 3; i += 1) {
			attributes[i] = sqSettings.next();
			if (attributes[i].equals("*")) {
				attributes[i] = null;
			}
		}

		ArrayList<String> listOfActions = new ArrayList<>();
		while (sqSettings.hasNext()) {
			listOfActions.add(sqSettings.next());
		}

		game.addSquare(seqNum, xVal, yVal,
				attributes[0], attributes[1], attributes[2], listOfActions);
	}

	private void setPlayers() {
		for (int i = 1; i <= numOfPlayers; i += 1) {
			String name = "Player " + i;
			String image = "player" + i + ".png";
			game.addPlayer(name, image, i);
		}
		game.setNumOfPlayers(numOfPlayers);
	}

	private void setPlayer(String name, int num) {
		String image = "player" + num + ".png";
		game.addPlayer(name, image, num);
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
	public void dispose () {
		batch.dispose();
		font.dispose();
		texture.dispose();
	}
}
