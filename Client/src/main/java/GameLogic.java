//
// The internal GameLogic that will be used for Test Cases
//
// The ClientGUI will handle valid moves and will transfer
// that move to this class to be added. So no second
// validation will be needed.
//

public class GameLogic {
	
	char[][] board = new char[7][6];
	int totalMoves = 0;
	
	GameLogic() {
		for (int i=0; i<6; i++) {
			for (int j=0; j<7; j++) {
				board[j][i] = 'E';
			}
		}
	}
	
	// Will be called each time the user clicks button to fill this verison of the board.
	public void add(int x, int y, char value) {
		for (int i=0; i<6; i++) {
			for (int j=0; j<7; j++) {
				
				if(j == x && i == y) {
					board[j][i] = value;
					totalMoves++;
				}
			}
		}
	}
	
	// Just will be used for visual testing, no need for Unit Tests
	public void print() {
		for (int i=0; i<6; i++) {
			for (int j=0; j<7; j++) {
				System.out.print(board[j][i]);
			}
			System.out.print('\n');
		}
	}
	
	// Change return value to be a boolean or an ArrayList of int pairs
	public String evaluate() {
		for (int i=0; i<6; i++) {
			for (int j=0; j<7; j++) {
				
				if (board[j][i] != 'E') {
					// Horizontal
					if (j+3 < 7) {
						if (board[j][i] == board[j+1][i] &&
							board[j][i] == board[j+2][i]  &&
							board[j][i]== board[j+3][i]) {
								return "H" + j + i; // Horizontal win
						}
					}
					// Vertical
					if (i+3 < 6) {
						if (board[j][i] == board[j][i+1] &&
							board[j][i] == board[j][i+2]  &&
							board[j][i]== board[j][i+3]) {
								return "V" + j + i; // Vertical win
						}
					}
					// Diagonal 1
					if (i+3 < 6 && j+3 < 7) {
						if (board[j][i] == board[j+1][i+1] &&
							board[j][i] == board[j+2][i+2]  &&
							board[j][i]== board[j+3][i+3]) {
								return "B" + j + i; // Diagonal Bottom wing '\' something like that symbol
						}
					}
					// Diagonal 2
					if (i > 2 && j+3 < 7) {
						if (board[j][i] == board[j+1][i-1] &&
							board[j][i] == board[j+2][i-2]  &&
							board[j][i]== board[j+3][i-3]) {
								return "T" + j + i; // Diagonal Top wing '/' something like that symbol
						}
					}
				}
				
				
			}
		}
		
		if (totalMoves == (7 * 6)) {
			return "O";			// The board has been filled game is over and is tie
		}
		
		
		return "N"; 		// N means no winner but still playable
	}
}