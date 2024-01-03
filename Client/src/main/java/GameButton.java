import javafx.scene.control.Button;

public class GameButton extends Button {
	int x;
	int y;
	char value;						// This will be used to show who 
	
	GameButton(int x, int y) {
		// Changing button appearance
		this.setPrefSize(50, 50);
		this.setStyle("-fx-background-color: lightgrey");
		
		this.x = x;
		this.y = y;
		this.value = 'E';				// E means Empty.
	}
	
}