import javafx.animation.PauseTransition;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;


//
// Michael Jedziniak part of Project.
//
// Created the Board and Game Logic for the Project.
// This board will automatically have all coins go to bottom of board
// and there will be no need to tell the user that they need to do a valid move
// because only valid moves are accepted to move forward with the game.
// The gameLogic can be found in the GameLogic.java file that mimics
// the Board in the GUI and creates a char array of it that will be used to
// evaluate if there is a win.
//

public class ClientGUI extends Application {
	CFourInfo info = new CFourInfo();
	Client clientConnection;
	Stage primaryStageNonLocal;			// Used for End Game Scene so can access this.
	
	// Display elements
	Label turn = new Label("Player " + info.turn + "'s Turn!");
	Label moveInfo = new Label();
	
	// Player ID
	static int playerNumber = 0;
	static char playerToken;
	static String playerColor;
	
	// Board GUI
	private GridPane board = new GridPane();
	private GameLogic charBoard = new GameLogic();

	public static void main(String[] args) {
		launch(args);
	}

	public void endGame(Stage primaryStage) {
		Label prompt = new Label(info.move); // TODO final message should say who won/tie
		prompt.setTranslateX(250); // probably need to update
		prompt.setTranslateY(175);
		prompt.setFont(new Font(25));
		
		// create button to play again
		Button playAgain = new Button("Play Again");
		playAgain.setPrefWidth(150);
		playAgain.setPrefHeight(50);
		playAgain.setTranslateX(275);
		playAgain.setTranslateY(360);

		playAgain.setOnAction(action -> {
			try {
				info.move = "Playing Again";
				info.gameOver = false;
				info.have2Players = false;
				info.x = -1;			// Used to reset values
				info.y = -1;
				info.result = "";
				info.turn = 1;
				info.playerNum = 0;
				
				board = new GridPane();			// reset boards
				charBoard = new GameLogic();
				
				clientConnection.send(info);
				waitingForPlayer(primaryStage);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		// create button to exit program
		Button exit = new Button("Exit");
		exit.setPrefWidth(150);
		exit.setPrefHeight(50);
		exit.setTranslateX(275);
		exit.setTranslateY(425);

		exit.setOnAction(action -> System.exit(0));

		// create scene
		Group root = new Group(prompt, playAgain, exit);

		Scene scene = new Scene(root, 700,700, Color.PALEVIOLETRED);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public void startGame(Stage primaryStage) {
		// TODO game play logic
		// IP: 127.0.0.1
		createGameBoard();					// Creating Board
		board.setHgap(5);
		board.setVgap(5);
		
		// Setting playerID and Color according to which Client they are
		if (playerNumber == 0) {
			playerNumber = 2;				// In case of being Client 2
			playerColor = "Yellow";			// Setting colors to be used for chips
			playerToken = playerColor.charAt(0);
			disableBoard();					// They are P2, so disableBoard
		} else {
			playerColor = "Red";
			playerToken = playerColor.charAt(0);
		}
		// END OF SETTING PLAYER IDs

		moveInfo.setText("You are Player " + playerNumber);
					
		// Update CFourInfo info
		//clientConnection.send(info); // send move to server

		Group root = new Group(new VBox(turn, moveInfo, board) );

		Scene scene = new Scene(root, 500,500, Color.LIGHTBLUE);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStageNonLocal = primaryStage;
		// This variable will be used for when the game is over and to switch to the end Scene
		// this is due to that primaryStage is a local variable.
	}
	
	// BOARD LOGIC HERE
	
	private void createGameBoard() {
		//board = new GridPane();
		for (int i=0; i < 6; i++) {
			for(int j=0; j < 7; j++) {
				GameButton btn = new GameButton(j, i);
				btn.setOnAction(e -> GameBtnClicked(btn));
				board.add(btn, j, i);
			}
		}
	}
	
	// EVENT HANDLER
	
	public void GameBtnClicked(GameButton btn) {
		// When clicked Validate if it is a good move
		int col = btn.x;
		for(int i=0; i < 6; i++) {
			GameButton btnCheck = this.get(col, i);
			// NOTE
			// This method will always place a token at the bottom of the column no matter what.
			// Therefore, no need of validation string to tell user.
			if (btnCheck.y == 5 && btnCheck.value == 'E') {
				btnCheck.setStyle("-fx-background-color: " + playerColor);
				btnCheck.value = playerToken;
				
				updateInfo(btnCheck.x, btnCheck.y);
				break;												// Column empty, fill bottom
			} 
			else if ((btnCheck.value == 'R' || btnCheck.value == 'Y') && i != 0) {
				GameButton btnCheck2 = this.get(col, i - 1);
				btnCheck2.setStyle("-fx-background-color: " + playerColor);
				btnCheck2.value = playerToken;
				
				updateInfo(btnCheck2.x, btnCheck2.y);
				break;												// btn is used, go to one above if not top column
			}
			
		}
		
	}
	// END OF EVENT HANDLER
	
	// UPDATING INFO
	public void updateInfo(int x, int y) {
		info.x = x;
		info.y = y;
		info.move = "Player " + playerNumber + " moved to " + info.x + ", " + info.y;
		
		charBoard.add(x, y, playerToken);
		//charBoard.print();
		// UPDATE TURN which will swap values.
		if (info.turn == 1) {
			info.turn = 2;
		} else if (info.turn == 2) {
			info.turn = 1;
		}
		
		// CHECKING FOR WIN
		String result = charBoard.evaluate();
		if (result != "N") {
			info.gameOver = true;
			//System.out.println("Player " + playerNumber + " WON!!!!");
			info.move = "Player " + playerNumber + " WON! Move was "  + info.x + ", " + info.y;
			info.result = result;
			
			moveInfo.setText(info.move);			// update moveInfo
			disableBoard();
			clientConnection.send(info);
			gameIsOver(result);
		} else {									// Game not over; continue
			moveInfo.setText(info.move);			// update moveInfo
			turn.setText("Player " + info.turn + "'s Turn!");
			// Disable Board now
			disableBoard();
			clientConnection.send(info);
		}
	}
	
	// This will handle the highlighting and end game logic before end scene
	public void gameIsOver(String result) {
		disableBoard();
		if (result.charAt(0) == 'H') {
			int x = (int)result.charAt(1)-'0';					// These are used to get cord
			int y = (int)result.charAt(2)-'0';
			//System.out.println(result + ": " + x + " " + y);
			GameButton button = get(x, y);
			button.setText("X");
			button = get(x + 1, y);
			button.setText("X");
			button = get(x + 2, y);
			button.setText("X");
			button = get(x + 3, y);
			button.setText("X");
		} else if (result.charAt(0) == 'V') {
			int x = (int)result.charAt(1)-'0';
			int y = (int)result.charAt(2)-'0';
			GameButton button = get(x, y);
			button.setText("X");
			button = get(x, y + 1);
			button.setText("X");
			button = get(x, y + 2);
			button.setText("X");
			button = get(x, y + 3);
			button.setText("X");
		} else if (result.charAt(0) == 'B') {
			int x = (int)result.charAt(1)-'0';
			int y = (int)result.charAt(2)-'0';
			GameButton button = get(x, y);
			button.setText("X");
			button = get(x + 1, y + 1);
			button.setText("X");
			button = get(x + 2, y + 2);
			button.setText("X");
			button = get(x + 3, y + 3);
			button.setText("X");
		} else if (result.charAt(0) == 'T') {
			int x = (int)result.charAt(1)-'0';
			int y = (int)result.charAt(2)-'0';
			GameButton button = get(x, y);
			button.setText("X");
			button = get(x + 1, y - 1);
			button.setText("X");
			button = get(x + 2, y - 2);
			button.setText("X");
			button = get(x + 3, y - 3);
			button.setText("X");
		} else {
			info.move = "IT IS A TIE!!";  // The Game Board is filled
		}
		
		// HERE IS TO PAUSE AND MOVE TO END SCENE
		PauseTransition pause = new PauseTransition(Duration.seconds(3));
		
		pause.setOnFinished((e) -> {
			if (info.gameOver) endGame(primaryStageNonLocal);
		});
		
		pause.play();
	}
	
	// UPDATING BOARD FROM OTHER TURN
	public void updateBoard(int x, int y) {
		for (Node node: board.getChildren()){
			GameButton b = (GameButton) node;
			if (b.x == x && b.y == y) {
				if (playerToken == 'R') {
					b.setStyle("-fx-background-color: Yellow");
					charBoard.add(x, y, 'Y');
					//charBoard.print();
					b.value = 'Y';
				} else {			// Token is Y
					b.setStyle("-fx-background-color: Red");
					charBoard.add(x, y, 'R');
					//charBoard.print();
					b.value = 'R';
				}
			}
		}
	}	
	
	public GameButton get(int x , int y) {
		// Loop through the children/GameButtons of the Board
		for (Node node: board.getChildren()){
			GameButton b = (GameButton) node;
			if (b.x == x && b.y == y) {
				return b;					// Found button return it
			}
		}
		return null;						// Not found return NULL
	}
	
	public void disableBoard() {
		for (Node node: board.getChildren()){
			GameButton b = (GameButton) node;
			b.setDisable(true);
		}
	}
	public void enableBoard() {
		for (Node node: board.getChildren()){
			GameButton b = (GameButton) node;
			b.setDisable(false);
		}
	}

	
	// END OF BOARD LOGIC STUFF
	
	
	public void waitingForPlayer(Stage primaryStage) {
		Label label = new Label("Waiting for Player 2...");
		label.setTranslateX(150);
		label.setTranslateY(200);
		label.setFont(new Font(25));
		
		playerNumber = info.playerNum;				// Store player number id: this client will be Player 1.

		Group root = new Group(label);

		Scene scene = new Scene(root, 500,500, Color.LIGHTBLUE);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Welcome to the Connect 4 Game");
		AtomicInteger callCount = new AtomicInteger();

		// create prompts to enter ip address and port #
		TextField ip = new TextField("Enter ip address"); // correct ip: 127.0.0.1
		ip.setPrefWidth(200);
		ip.setPrefHeight(50);
		ip.setTranslateX(100);
		ip.setTranslateY(400);

		TextField port = new TextField("Enter Port #");
		port.setPrefWidth(200);
		port.setPrefHeight(50);
		port.setTranslateX(400);
		port.setTranslateY(400);

		// create button to start client thread
		Button clientChoice = new Button("Client");
		clientChoice.setPrefWidth(150);
		clientChoice.setPrefHeight(50);
		clientChoice.setTranslateX(275);
		clientChoice.setTranslateY(600);

		clientChoice.setOnAction(e-> {
			primaryStage.setTitle("Connected to Server");
			clientConnection = new Client(data-> Platform.runLater(()->{
				info = (CFourInfo) data;
				if (info.have2Players) callCount.getAndIncrement();
				else waitingForPlayer(primaryStage);
				if (callCount.get() == 1) startGame(primaryStage);
				
				// This part will contain all game information and updating 
				if(info.x != -1 || info.y != -1) {
					updateBoard(info.x, info.y);		// update Board
					moveInfo.setText(info.move);		// Change moveInfo to be latest move
					turn.setText("Player " + info.turn + "'s Turn!");
					
					// Turn Logic
					if (info.turn == playerNumber) {
						enableBoard();
					}
					
					if (info.gameOver) {				// If called here, the other player lost
						gameIsOver(info.result);
					}
				}
				
			}), ip.getText(), Integer.parseInt(port.getText()));

			clientConnection.start();
		});

		// option to exit program
		primaryStage.setOnCloseRequest(t -> {
			Platform.exit();
			System.exit(0);
		});

		// create new scene
		BorderPane root = new BorderPane();
		root.setPrefWidth(700);
		root.setPrefHeight(350);
		Group group = new Group(root, clientChoice, ip, port);

		// set background image
		BackgroundImage bi = new BackgroundImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNt6dFwQFiVlmCs2Z1GpQFvOZ6AIEkX5OW8A&usqp=CAU", 700, 350, false, true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		Background bg = new Background(bi);
		root.setBackground(bg);

		Scene scene = new Scene(group, 700,700, Color.LIGHTBLUE);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
