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
import java.util.List;

public class BoardGameEngine extends Game {
	// Game Specific Config Input
	private GameEngine game;
	public String victory = "victory.wav";
	public String configFileName = "dentalActualGame.csv";
	//	public String configFileName = "dentalMultipleDet.csv";
//	public String configFileName = "dentalTestDet.csv";
//	public String configFileName = "dentalWithDetermine.csv";
//	public String configFileName = "dentalColor.txt";
//	public String configFileName = "dentalColorG6.txt";
//	public String configFileName = "dentalWin.txt";
	public String configImage = "dentalColor.png";

//	public String configFileName = "dental.txt";
//	public String configImage = "rectangularBoard.png";

	// CSV File Reading Settings
	private static String[] headerSetup =
			{"seqNum", "x", "y", "image", "sound", "text",
					"roll again", "move by", "move to", "skip",
					"roll to determine action", "conditions"};
	private static int headersNum = 2;

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
		for (String line: lines) {
			System.out.println(line);
		}

		// CSV File Approach
		initializeCSV(lines);

	/*
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
		System.out.println(board W);
		System.out.println(boardH);

		game = new GameEngine(rowNum, colNum, endPosNum);

		for (int i = 1; i < lines.length; i += 1) {
			setUpSquare(lines[i]);
		}
	*/

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

	private void initializeCSV(String[] config) {
		// Ignore the header rows (row 1 - 2)
		// Row 3 will be # of squares, x position range, y position range
		// Row 4 onward are square IDs
		// For each square, index 0-2 set for sqNum, x coord, ycoord

		//List<String> config is a list or array of strings separated by \n char
		String boardRep = config[headersNum];
		String[] boardData = boardRep.trim().split(",");

		float rowNum = Float.valueOf(boardData[1]);
		float colNum = Integer.valueOf(boardData[2]);
		int squareTotal = Integer.valueOf(boardData[0]);

		// Scaling Changes
		ratio = CONSTANTW / rowNum;
		boardW = windWidth = rowNum * ratio;
		boardH = windHeight = colNum * ratio;

		game = new GameEngine(rowNum, colNum, squareTotal);

		for (int i = headersNum + 1; i < config.length; ) {
			i = setUpSquareCSV(config[i], i, config);
		}
	}

	private int setUpSquareCSV(String settings, int row, String[] config) {
		// Assume sqData has same number of columns as headerSetup
		String[] sqData = settings.trim().split(",");
		for (String col : sqData) {
			System.out.print(col + " ");
		}
		System.out.println(sqData.length);
		int count = row;

		// Defining Variables
		int seqNum = 0;
		float xVal = 0;
		float yVal = 0;
		String image = null;
		String sound = null;
		String text = null;
		ArrayList<String> listOfActions = new ArrayList<>();

		for (int i = 0; i < sqData.length; i += 1) {
			String columnH = headerSetup[i];
			System.out.println(columnH);
			switch (columnH) {
				case "seqNum":
					seqNum = Integer.valueOf(sqData[i]);
					break;
				case "x":
					xVal = Float.valueOf(sqData[i]) * ratio;
					break;
				case "y":
					yVal = Float.valueOf(sqData[i]) * ratio;
					break;
				case "image":
					if (sqData[i] != null && !sqData[i].isEmpty()) {
						image = sqData[i];
					}
					break;
				case "sound":
					if (sqData[i] != null && !sqData[i].isEmpty()) {
						sound = sqData[i];
					}
					break;
				case "text":
					if (sqData[i] != null && !sqData[i].isEmpty()) {
						text = sqData[i];
					}
					break;
				default:
					if (sqData[i] != null && !sqData[i].isEmpty()) {
						String[] results = actionDetails(sqData[i], i, count, config);
						String action = results[0];
						if (action != null) {
							listOfActions.add(action);
						}
						if (action.startsWith("G")) {
							count += Integer.valueOf(results[1]);
						}
						System.out.println(action);
					}
					break;
			}
		}

		game.addSquare(seqNum, xVal, yVal, image, text, sound, listOfActions);
		System.out.println("added square " + seqNum);
		System.out.println();
		count += 1;
		return count;
	}

	private String[] actionDetails(String value, int column, int row, String[] config) {
		String colType = headerSetup[column];
		String action;
		String additional = "0";
		switch(colType) {
			case "roll again":
				action = "A";
				break;
			case "move by":
				int amount = Integer.valueOf(value);
				if (amount >= 0) {
					action = "B" + amount;
				} else {
					amount *= -1;
					action = "C" + amount;
				}
				break;
			case "move to":
				action = "D" + Integer.valueOf(value);
				break;
			case "skip":
				action = "E";
				break;
			case "roll to determine action":
				String[] determinedAction = determine(row, config);
				action = determinedAction[0];
				additional = determinedAction[1];
				break;
			default:
				System.out.println(colType);
				System.out.println(column);
				System.out.println(row);
				System.out.println(value);
				action = null;
				System.out.println("Invalid action");
		}
		return new String[]{action, additional};
	}

	private String[] determine(int row, String[] config) {
		StringBuilder result = new StringBuilder();
		result.append("G");

		int rowTracker = row + 1;
		String options = config[rowTracker];
		String[] parsedOptions = options.split(",");
		while(parsedOptions[0].isEmpty()) {
			// Processing this row
			String numbers = "";
			String nextAction = "";
			for (int i = 0; i < parsedOptions.length; i += 1) {
				String value = parsedOptions[i];
				if (headerSetup[i].equals("conditions")) {
					numbers += value;
				} else if (!parsedOptions[i].isEmpty()) {
					String[] actionKeys = actionDetails(parsedOptions[i], i, rowTracker, config);
					nextAction = actionKeys[0];
					row += Integer.valueOf(actionKeys[1]); // Increments row if needed
				}
			}
			result.append(numbers.trim());
			result.append(nextAction.trim());
			result.append(".");

			// Advacing to next row
			rowTracker += 1;
			if (rowTracker == config.length) {
				break;
			}
			options = config[rowTracker];
			parsedOptions = options.split(",");
		}
		System.out.println("Determined - ");
		System.out.println(result.charAt(0));
//		result = "G6D0-";
		return new String[] {result.toString(), "" + (rowTracker - row - 1)};
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
