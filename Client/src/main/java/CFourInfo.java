import java.io.Serializable;

public class CFourInfo implements Serializable {
    Boolean have2Players;
    Boolean gameOver;
    int playerNum;
    int turn;
    String result;
    
    int x;					// used to get coords of players moves to update board.
    int y;
    String move;
    CFourInfo () {
        have2Players = false;
        gameOver = false;
        playerNum = 0;
        x = -1;
        y = -1;
        move = "";
        result = ""; 				// to be used for end game and showing winning move
        turn = 1;
    }
}
