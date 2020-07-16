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

/**
 * BoardGameEngine is where the game configuration starts. An instance of this
 * class is created by the various launching classes - Desktop, Web, Android, etc.
 * Many of the user game specific parameters can be altered here:
 * the config file name, the board game image, name of the token images,
 * the order in which the csv columns are set. This class is in charge of
 * creating various screens to display and parsing the configuration
 * csv file.
 */
public class BoardGameEngine extends Game {
	// Game Specific Config Input
	private GameEngine game;
	public String victory = "victory.wav";
	public String winningPage = "congrats.jpg";
//	public String configFileName = "dentalActualGame.csv";
//	public String configFileName = "dentalActualGameChance.csv";
	public String configFileName = "dentalActualGameChanceAction.csv";
//	public String configFileName = "dentalActualGameAnimate.csv";
//	public String configFileName = "dentalActualGameWithName.csv";
//	public String configFileName = "dentalActualGameDefSound.csv";
//	public String configFileName = "dentalActualGameColSound.csv";
//	public String configFileName = "dentalQuickWin.csv";
//	public String configFileName = "dentalMultipleDet.csv";
//	public String configFileName = "dentalTestDet.csv,";
//	public String configFileName = "dentalWithDetermine.csv";
//	public String configFileName = "dentalColor.txt";
//	public String configFileName = "dentalColorG6.txt";
//	public String configFileName = "dentalWin.txt";
	public String configImage = "dentalColor.png";

//	public String configFileName = "dental.txt";
//	public String configImage = "rectangularBoard.png";

	// CSV File Reading Settings - specifies the order of the various columns
	private static String[] headerSetup =
			{"seqNum", "x", "y", "image", "sound", "text",
					"roll again", "move by", "move to", "skip",
					"roll to determine action", "conditions", "chance card"};
	private static int headersNum = 5; //instead of 4

	// Player Token Images Settings
	private static String[] tokenFiles = {"player1.png", "player2.png", "player3.png", "player4.png"};

	// Animated Token Images Settings
	private static String[][] tokenFilesList =
		{ 	{"player1.png"},
			{"player2.png"},
			{"player3.png"},
			{"player4.png"}
		};

	// Instructions / Game Name Settings
	private Screen curScreen;
	public String gameName;
	public String instructionsFile;
	public String welcomeScreen;
	private boolean beginning;

	// Splash Image
	public String splashImage = "splash.png";
	public float splashImageW = 456;
	public float splashImageH = 361;

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
	public float distBetwPlayers = 10;

	// Message Bar (FIXME - MESSAGE BAR)
	public int messageHeight = 150;
	public int messageAvgLen = 150;
	public int messagePad = 20;

	@Override
	public void create () {
		// Overall Shared GUI
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		layout = new GlyphLayout();

		// Set up Game By Parsing CSV File
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

		// Sets the Main Menu Screen with Stage & Buttons
		// this.setScreen(new MainMenuScreen(this));
		beginning = true;
		this.setScreen(new InstructionScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	/**
	 * Updates the camera based on the project matrix of board world to
	 * actual screen units. Called before any rendering or drawing occurs.
	 */
	public void cameraScreen() {
		camera.update();
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
	}

	/**
	 * Sets the number of players of current game to parameter NUM.
	 * Switches from the main menu screen to the game screen.
	 * @param num - number of players
	 */
	public void buttonSetGame(int num) {
		game.setNumOfPlayers(num);
		curScreen = new GameScreen(this, this.game);
		this.setScreen(curScreen);
	}

	/**
	 * Change from instruction screen to main menu screen.
	 */
	public void toNextScreen() {
		if (beginning) {
			curScreen = new MainMenuScreen(this, game);
			beginning = false;
		}

		if (curScreen instanceof MainMenuScreen) {
			((MainMenuScreen) curScreen).reactivate();
		}
		this.setScreen(curScreen);
	}

	public void toInstrScreen() {
		this.setScreen(new InstructionScreen(this));
	}

	/**
	 * Initializes the game by parsing and reading the configuration file.
	 * Uses helper methods specific for file type (CSV or txt).
	 */
	private void initialize() {
		// General Config File
		FileHandle configText = Gdx.files.internal(configFileName);

		// Regex approach - splits the file by each line
		String config = configText.readString();
		String[] lines = config.split("\n");
		for (String line: lines) {
			System.out.println(line);
		}

		// CSV File Approach
		initializeCSV(lines);

		// Txt File Approach
		// initializeText(lines);

	}

	/**
	 * Takes in an array of strings, each element being one line
	 * of the original configuration txt file. Sets up a Game
	 * object according to the xRange, yRange, and number of squares
	 * in the board game. Then, sets up all the special squares of the game.
	 * @param lines - array of Strings, each element is one line of txt file
	 */
	private void initializeText(String[] lines) {
		String setUp = lines[0];
		String[] setUpSettings = setUp.split(" ");

		float xNum = Float.valueOf(setUpSettings[0]);
		float yNum = Integer.valueOf(setUpSettings[1]);
		int endPosNum = Integer.valueOf(setUpSettings[2]);

		// Scaling Changes
		ratio = CONSTANTW / xNum;
		boardW = windWidth = xNum * ratio;
		boardH = windHeight = yNum * ratio;

		// Creating a Game Instance
		game = new GameEngine(xNum, yNum, endPosNum);

		// Setting up all the squares in this Game
		for (int i = 1; i < lines.length; i += 1) {
			setUpSquareText(lines[i]);
		}
	}

	/**
	 * Sets up a square of the Game, assuming that
	 * settings is the string for the Square's settings
	 * from a txt File. Format should be:
	 * seqNum xCoord yCoord pictureFile textFile soundFile seriesOfActions
	 * @param settings
	 */
	private void setUpSquareText(String settings) {
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

	/**
	 * Takes in an array of strings, each element being one line
	 * of the original configuration CSV file. Sets up a Game
	 * object according to the xRange, yRange, and number of squares
	 * in the board game. Then, sets up all the special squares of the game.
	 *
	 * Starts reading contents of the CSV file at row HEADERSNUM + 1 because
	 * the first HEADERSNUM rows are not related to actual game configuration.
	 *
	 * Each row after the first board representation row is set up for a square.
	 *
	 * @param config - array of String - each element is one row of the CSV file
	 */
	private void initializeCSV(String[] config) {
		// Row 1 will be game name and instructions
		// Row 2 Image Tokens reset
		// Ignore the header rows (row 3 - 4)
		// Row 5 designer specified column sounds
		// Row 6 will be # of squares, x position range, y position range
		// Row 7 onward are square IDs
		// For each square, index 0-2 set for sqNum, x coord, ycoord

		String gameData = config[0];
		String[] gameDataParsed = gameData.trim().split(",");
		gameName = gameDataParsed[0];
		instructionsFile = gameDataParsed[1];
		// welcomeScreen = gameDataParsed[2];

		String imageToken = config[1];
		String[] tokenRepl = imageToken.trim().split(",");
		for (int i = 0; i < tokenRepl.length; i += 1) {
			if (!tokenRepl[i].isEmpty()) {
				String[] animatedTokenImg = tokenRepl[i].split("\\s");
				tokenFilesList[i] = animatedTokenImg;
				// tokenFiles[i] = tokenRepl[i].trim();
			}
		}

		String boardRep = config[headersNum];
		String[] boardData = boardRep.trim().split(",");

		float xNum = Float.valueOf(boardData[1]);
		float yNum = Integer.valueOf(boardData[2]);
		int squareTotal = Integer.valueOf(boardData[0]);

		// Scaling Changes
		ratio = CONSTANTW / xNum;
		boardW = windWidth = xNum * ratio;
		boardH = windHeight = yNum * ratio;

		// Setting Up Game with Token Number and Player Number
		int tokensPerPlayer = 1; //DEFAULT
		int maxNumPlayer = 4; //DEFAULT
		if (boardData.length == 5) {
			if (!boardData[4].isEmpty()) {
				maxNumPlayer = Integer.valueOf(boardData[4]);
			}
		}
		if (boardData.length >= 4) {
			if (!boardData[3].isEmpty()) {
				tokensPerPlayer = Integer.valueOf(boardData[3]);
			}
			game = new GameEngine(xNum, yNum, squareTotal, tokensPerPlayer, maxNumPlayer);
		} else {
			game = new GameEngine(xNum, yNum, squareTotal);
		}

		// Setting up designer specified sounds per column
		String columnAction = config[headersNum - 1];
		String[] columnActionSounds = columnAction.trim().split(",");
		setUpSound(columnActionSounds);

		// Set up all the squares of the board (squareTotal rows starting from headersNum + 1 row)
		int i = headersNum + 1;
		for (int sqNum = 0; sqNum < squareTotal; sqNum += 1) {
			i = setUpSquareCSV(config[i], i, config);
		}

		while (i < config.length) {
			i = setUpChanceCSV(config[i], i, config);
		}
		game.shuffleAll();
	}

	/**
	 * Sets up a square of the Game, assuming that
	 * settings is the row for the Square's settings
	 * from a CSV File. Format should be based on the headers array,
	 * which specifies what each index means (xcoord, ycoord, etc.).
	 *
	 * Some squares read more than one row of the config csv file
	 * because their attributes are like: roll again to determine action
	 * where a different number corresponds to a different action.
	 *
	 * @param settings - the Square settings row
	 * @param row - which row number is being processed
	 * @param config - array of the entire configuration CSV file split by line
	 * @return the integer for the index of the next UNREAD / UNPROCESSED square row
	 */
	private int setUpSquareCSV(String settings, int row, String[] config) {
		// Assume sqData has same number of columns as headerSetup
		String[] sqData = settings.trim().split(",");

		// Count keeps track of the current row number and increments
		// to properly account for which row should be processed next
		int count = row;

		// DEBUGGING PRINT STATEMENTS
		for (String col : sqData) {
			System.out.print(col + " ");
		}
		System.out.println(sqData.length);

		// Defining Default Square Attribute Variables
		int seqNum = 0;
		float xVal = 0;
		float yVal = 0;
		String image = null;
		String sound = null;
		String text = null;
		ArrayList<String> listOfActions = new ArrayList<>();

		// For each column in this row (which is now the sqData array),
		// check to see which column type it belongs to and modify
		// the corresponding attribute if given value is valid (not null or empty string).
		for (int i = 0; i < sqData.length; i += 1) {
			String columnH = headerSetup[i];
			System.out.println(columnH);
			switch (columnH) {
				case "seqNum":
					seqNum = Integer.valueOf(sqData[i]);
					break;
				case "x":
					xVal = Float.valueOf(sqData[i]) * ratio;
					System.out.println(xVal);
					break;
				case "y":
					yVal = Float.valueOf(sqData[i]) * ratio;
					System.out.println(yVal);
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

	/**
	 * Takes in the VALUE of a box in a CSV file and matches it with the corresponding
	 * column heading to determine which type of action corresponds to the square
	 * being created. Properly formats a string based on the action type and the action amount
	 * (i.e - amount being 10 if action is move forward 10).
	 *
	 * @param value - the value in the CSV grid box at row, column
	 * @param column - Column number of this grid box
	 * @param row - Row that is currently being processed (row corresponding to the square that is being set up)
	 * @param config - array of the configuration CSV file separated by line
	 * @return a 2-element String array
	 * 	element at index 0: String representation for the action corresponding to the square
	 * 	element at index 1: String representation of the number of ADDITIONAL rows processed
	 * 	when trying to determine this square's action details
	 */
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
			case "chance card":
				action = "H" + value;
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

	/**
	 * Only called for the grid attribute that corresponds to this action:
	 * roll again to determine action.
	 *
	 * Square must read more rows to determine what action is associated with
	 * rolling a 1, 2, 3, 4, 5, or 6 on the following roll.
	 *
	 * Keeps reading rows until it reaches the next square row (which has
	 * a square id in the first column). For each row after the original ROW,
	 * it keeps tracks of the numbers and associated action. (i.e rolling a 1 means move 1 forward)
	 *   numbers: 1
	 *   action: move 1 forward
	 *
	 * Moves through the extra set up row. The CONDITIONS header determines this row is a
	 * condition for when user rolls the specific number. If it isn't in a CONDITIONS header,
	 * method uses actionDetails to determine the specific action string.
	 *
	 * Adding to the result string, which may look like G1A.2B10.
	 * Different conditions separated by a "." and the first "G" signifies it is is a
	 * roll to determine action type of square.
	 *
	 * @param row - Current Row Number of the Square being processed
	 * @param config - array representation of config CSV file separated by line
	 * @return a 2-element String array
	 *  element at index 0: action details in a String
	 *  element at index 1: number of additional rows used
	 */
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
				} else if (!parsedOptions[i].trim().isEmpty()) {
					String[] actionKeys = actionDetails(parsedOptions[i], i, rowTracker, config);
					nextAction = actionKeys[0];
					System.out.println(nextAction + " " + i);
					rowTracker += Integer.valueOf(actionKeys[1]); //increments row if needed
				}
			}
			result.append(numbers.trim());
			result.append(nextAction.trim());
			result.append(".");

			// Advancing to next row
			rowTracker += 1;
			if (rowTracker == config.length) {
				break;
			}
			options = config[rowTracker];
			parsedOptions = options.split(",");
		}
		System.out.println("Determined - ");
		System.out.println(result.charAt(0));
		System.out.println(result);
		return new String[] {result.toString(), "" + (rowTracker - row - 1)};
	}

	// Setting up Chance Cards
	private int setUpChanceCSV(String settings, int row, String[] config) {
		// Assume sqData has same number of columns as headerSetup
		String[] chanceData = settings.trim().split(",");

		// Count keeps track of the current row number and increments
		// to properly account for which row should be processed next
		int count = row;

		// Defining Default Square Attribute Variables
		String type = "";
		String image = null;
		String sound = null;
		String text = null;
		ArrayList<String> chanceActions = new ArrayList<>();

		// For each column in this row (which is now the chanceData array),
		// check to see which column type it belongs to and modify
		// the corresponding attribute if given value is valid (not null or empty string).
		for (int i = 0; i < chanceData.length; i += 1) {
			String columnH = headerSetup[i];
			switch (columnH) {
				case "seqNum":
					if (!chanceData[i].isEmpty()) {
						type = chanceData[i];
					}
					break;
				case "x":
				case "y":
					break;
				case "image":
					if (chanceData[i] != null && !chanceData[i].isEmpty()) {
						image = chanceData[i];
					}
					break;
				case "sound":
					if (chanceData[i] != null && !chanceData[i].isEmpty()) {
						sound = chanceData[i];
					}
					break;
				case "text":
					if (chanceData[i] != null && !chanceData[i].isEmpty()) {
						text = chanceData[i];
					}
					break;
				default:
					if (chanceData[i] != null && !chanceData[i].isEmpty()) {
						String[] results = actionDetails(chanceData[i], i, count, config);
						String action = results[0];
						if (action != null) {
							chanceActions.add(action);
						}
						if (action.startsWith("G")) {
							count += Integer.valueOf(results[1]);
						}
					}
					break;
			}
		}

		game.addChance(type, image, sound, text, chanceActions);

		// DEBUGGING
		System.out.println(type + image + sound + text);
		for (String act : chanceActions) {
			System.out.println(act);
		}

		count += 1;
		return count;
	}

	/**
	 * Sets up designer specified default sounds. Takes in an array
	 * of all the sound files specified. Matches them with a corresponding
	 * type of action and sets the default sound of that particular action
	 * to be given file name.
	 * @param columnActionSounds list of sound files
	 */
	private void setUpSound(String[] columnActionSounds) {
		for(int i = 0; i < columnActionSounds.length; i += 1) {
			if (columnActionSounds[i].isEmpty()) {
				continue;
			}
			String key;
			switch (headerSetup[i]) {
				case "roll again":
					key = "A";
					break;
				case "move by":
					key = "B";
					break;
				case "move to":
					key = "D";
					break;
				case "skip":
					key = "E";
					break;
				case "roll to determine action":
					key = "G";
					break;
				default:
					key = null;
			}
			game.setSoundInList(key, columnActionSounds[i]);

			// For moving forward and backward sounds
			if (key.equals("B")) {
				game.setSoundInList("C", columnActionSounds[i]);
			}
		}
	}

	/**
	 * Adds a player with the name NAME to the game.
	 * Assigns the player to the number NUM and selects corresponding
	 * image for player's tokens - currently uses default.
	 * @param name - name of player
	 * @param num - player number (i.e player 1, player 2, etc).
	 */
	public void setPlayer(String name, int num) {
		// Set Up Player with Static Image Files
		//String image = tokenFiles[num - 1];
		//game.addPlayer(name, image, num);

		// Set Up Player with Animation Files
		String[] images = tokenFilesList[num - 1];
		game.addPlayer(name, images, num);
	}

	/**
	 * Sets up an AI player with the name NAME and
	 * player number NUM. Currently uses default images.
	 * @param name - name of AI
	 * @param num - player number
	 */
	public void setAI(String name, int num) {
		// Set Up Player with Static Image Files
		// String image = tokenFiles[num - 1];
		// game.addAI(name, image, num);

		// Set Up Player with Animation Files
		String[] images = tokenFilesList[num - 1];
		game.addAI(name, images, num);
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
