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
			// mainMenu();

			// Switch from Main to Game
			// old conditional statement
			// Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)

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
			gameScreen();
//			if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
//				Player p = game.currentPlayer();
//				p.guiMove(1);
//				batch.begin();
//				p.draw(this);
//				batch.end();
////				game.activate();
//			}

			// checking for wins
			if (game.gameOver()) {
				gameNotOver = false;
				winner = game.winner();
			}
		} else {
			winningScreen();
		}

//		batch.draw(img, 0, 0);
//		batch.end();
	}

	private void setMainMenu() {
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		final TextButton singlePlay = new TextButton("1 Player", skin);
		singlePlay.setWidth(150);
		singlePlay.setHeight(100);
		singlePlay.setPosition(windWidth / 8, 150, Align.center);
		singlePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				singlePlay.remove();
				Label caption = new Label("Game Begins!", skin);
				caption.setX(windWidth / 2, Align.center);
				stage.addActor(caption);
			}
		});

		final TextButton doublePlay = new TextButton("2 Player", skin);
		doublePlay.setWidth(150);
		doublePlay.setHeight(100);
		doublePlay.setPosition(3 * windWidth / 8, 150, Align.center);
		doublePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				doublePlay.remove();
				Label caption = new Label("Game Begins (2)!", skin);
				caption.setX(windWidth / 2, Align.center);
				stage.addActor(caption);
			}
		});

		final TextButton triplePlay = new TextButton("3 Player", skin);
		triplePlay.setWidth(150);
		triplePlay.setHeight(100);
		triplePlay.setPosition(5 * windWidth / 8, 150, Align.center);
		triplePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				triplePlay.remove();
				Label caption = new Label("Game Begins (3)!", skin);
				caption.setX(windWidth / 2, Align.center);
				stage.addActor(caption);
			}
		});

		final TextButton quadPlay = new TextButton("4 Player", skin);
		quadPlay.setWidth(150);
		quadPlay.setHeight(100);
		quadPlay.setPosition(7 * windWidth / 8, 150, Align.center);
		quadPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				quadPlay.remove();
				Label caption = new Label("Game Begins (4)!", skin);
				caption.setX(windWidth / 2, Align.center);
				stage.addActor(caption);
			}
		});

		Label name = new Label("Hygiene Heroes", skin);
		name.setColor(Color.BLACK);
		name.setX(windWidth / 2, Align.center);
		name.setY(windHeight - windHeight / 12);

		Label gameName = new Label("Dental Hygiene Game", skin);
		gameName.setColor(Color.BLACK);
		gameName.setX(windWidth / 2, Align.center);
		gameName.setY(windHeight - 2 * windHeight / 12);

		Label instruction = new Label("Choose number of players to start game!", skin);
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
//			int playerNum = 1;
			for (Player p : game.getPlayersList()) {
				layout.setText(font, p.getName(), Color.BLACK, width, Align.center, true);
				font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);
				p.draw(this);
//				font.draw(batch, p.getName() + " previous roll: " + p.getPrevRoll(), 0, 440 - 20 * playerNum);

//				playerNum += 1;
				lineHeight += 50;
			}
		}

		layout.setText(font, game.currentTurnStr(), Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);
		lineHeight += 50;

		layout.setText(font, "Tap or press space to roll.", Color.BLACK, width, Align.center, true);
		font.draw(batch, layout, 0, height / 2 + layout.height / 2 - lineHeight);

//		int counter = 0;
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
//			Player p = game.currentPlayer();
//			p.guiMove(1);
//			p.draw(this);
//			System.out.println(counter);
			game.activate(this);
//			System.out.println("counter: " + counter);
//			counter += 1;
		}

//		if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
//			game.activate();
//		}
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
			// do something
		}
	}

	private void initialize() throws FileNotFoundException {
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
