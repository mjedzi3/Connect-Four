import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;


//
// IMPORTANT NOTE
//
// THIS FILE IS NOT BEING USED ANYMORE DUE TO FACT THAT
// I CANNOT ACCESS CFOURINFO TO UPDATE EACH CLIENT
// SO I MOVED THE LOGIC HERE OF THE BOARD TO BE FOUND
// IN THE CLIENTGUI.
// GAME LOGIC WILL THEN BE DONE IN A CHAR ARRAY IN THE 
// GAMELOGIC CLASS WITH WILL MIMIC THE ACTUAL GAME BOARD
//
public class GameBoard extends GridPane {

	private String playerColor;				// This will be uses as argument for constructor to set chip colors
	private char playerToken;				// Will be 'Y' or 'R' and be used to change value in GameButton
		
	int placedX;							// These will be set when the user places a token and will
	int placedY;							// be used to be sent to CFourInfo.
	
	// Constructor will create the Board
	GameBoard(String color) {
		// Appearance
		this.setHgap(5);
		this.setVgap(5);
		// Variables
		this.playerColor = color;
		this.playerToken = this.playerColor.charAt(0);
		
		for (int i=0; i < 6; i++) {
			for(int j=0; j < 7; j++) {
				GameButton btn = new GameButton(j, i);
				btn.setOnAction(e -> GameBtnClicked(btn));
				this.add(btn, j, i);
			}
		}
	}
	
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
				btnCheck.value = this.playerToken;
				
				placedX = btnCheck.x;
				placedY = btnCheck.y;
				break;												// Column empty, fill bottom
			} 
			else if ((btnCheck.value == 'R' || btnCheck.value == 'Y') && i != 0) {
				GameButton btnCheck2 = this.get(col, i - 1);
				btnCheck2.setStyle("-fx-background-color: " + playerColor);
				btnCheck2.value = this.playerToken;
				
				placedX = btnCheck2.x;
				placedY = btnCheck2.y;
				break;												// btn is used, go to one above if not top column
			}
		}
	}
	
	public void updateBoard(int x, int y) {
		for (Node node: this.getChildren()){
			GameButton b = (GameButton) node;

			if (b.x == x && b.y == y) {
				if (playerToken == 'R') {
					b.setStyle("-fx-background-color: Yellow");
					b.value = 'Y';
				} else {			// Token is Y
					b.setStyle("-fx-background-color: Red");
					b.value = 'R';
				}
			}
		}
	}	
	
	public GameButton get(int x , int y) {
		// Loop through the children/GameButtons of the Board
		for (Node node: this.getChildren()){
			GameButton b = (GameButton) node;

			if (b.x == x && b.y == y) {
				return b;					// Found button return it
			}
		}
		return null;						// Not found return NULL
	}
}