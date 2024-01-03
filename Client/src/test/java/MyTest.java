import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {
	
	int x = 7;
	int y = 6;
	int a = 65;
	
	static GameLogic addBoard = new GameLogic();
	static GameLogic horizontal = new GameLogic();
	static GameLogic vertical = new GameLogic();
	static GameLogic diagonalT = new GameLogic();
	static GameLogic diagonalB = new GameLogic();
	static GameLogic tieGame = new GameLogic();
	
	@BeforeAll
	static void setup() {
		horizontal.add(1, 5, 'R');
		horizontal.add(2, 5, 'R');
		horizontal.add(3, 5, 'R');
		horizontal.add(4, 5, 'R');
		
		vertical.add(1, 5, 'R');
		vertical.add(1, 4, 'R');
		vertical.add(1, 3, 'R');
		vertical.add(1, 2, 'R');
		
		diagonalT.add(1, 5, 'R');
		diagonalT.add(2, 4, 'R');
		diagonalT.add(3, 3, 'R');
		diagonalT.add(4, 2, 'R');
		
		diagonalB.add(1, 2, 'R');
		diagonalB.add(2, 3, 'R');
		diagonalB.add(3, 4, 'R');
		diagonalB.add(4, 5, 'R');
		
		
	}
	
	@Test
	void constructTest() {
		assertEquals('E', addBoard.board[6][5], "Construct Failed");
	}
	
	
	@Test
	void addingTest() {
		addBoard.add(2, 3, 'R');
		assertEquals('R', addBoard.board[2][3], "Adding Failed");
	}
	@Test
	void addingTest2() {
		addBoard.add(2, 3, 'Y');			// Changing value
		assertEquals('Y', addBoard.board[2][3], "Adding Failed");
	}
	
	//Winning
	@Test
	void noWinTest() {
		assertEquals("N", addBoard.evaluate(), "No Win Failed");
	}
	
	@Test
	void horizontalTest() {
//		horizontal.print();
//		System.out.print("horizontal\n");
		assertEquals('H', horizontal.evaluate().charAt(0), "Horizontal Failed");
	}
	
	@Test
	void verticalTest() {
//		vertical.print();
//		System.out.print("vertical\n");
		assertEquals('V', vertical.evaluate().charAt(0), "Vertical Failed");
	}
	
	@Test
	void DiagonalTTest() {
//		diagonalT.print();
//		System.out.print("diagonalT\n");
		assertEquals('T', diagonalT.evaluate().charAt(0), "Diagonal Top Failed");
	}
	
	@Test
	void DiagonaBTest() {
//		diagonalB.print();
//		System.out.print("diagonalB\n");
		assertEquals('B', diagonalB.evaluate().charAt(0), "Diagonal Bottom Failed");
	}
	
	
	@Test
	void tieGameTest() {
//		tieGame.print();
//		System.out.print("tieGame\n");
		for (int i=0; i<y; i++) {
			for (int j=0; j<x; j++) {
				tieGame.add(j, i, (char)(a));
				a++;
			}
		}
//		tieGame.print();
//		System.out.print("tieGameAfter\n");
		assertEquals("O", tieGame.evaluate(), "Tie Game Failed");
	}

}
