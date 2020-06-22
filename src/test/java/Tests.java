import logic.GameController;
import logic.Main;
import model.CheckerType;

import model.MoveResult;
import model.MoveType;
import org.junit.Test;
import view.Checker;
import view.Tile;

import static org.junit.Assert.*;

public class Tests {

    @Test
    public void test() {
        Main main = new Main();
        main.createScene();
        GameController game = main.getGame();
        Tile[][] board = main.getMainBoard();
        assertTrue(board[0][1].hasChecker());
        assertFalse(board[0][0].hasChecker());
        Checker checker1 = new Checker(CheckerType.BLACK, 2, 3);
        board[2][3].setChecker(checker1);
        Checker checker2 = new Checker(CheckerType.WHITE, 3, 4);
        board[3][4].setChecker(checker2);
        board[4][5].setChecker(null);
        MoveResult result;

        result = game.tryMove(checker1, 3, 4);
        assertEquals(MoveType.NONE, result.getType());
        result = game.tryMove(checker1, 4, 5);
        assertEquals(MoveType.NONE, result.getType());
        result = game.tryMove(checker2, 3, 4);
        assertEquals(MoveType.NONE, result.getType());
        result = game.tryMove(checker2, 4, 3);
        assertEquals(MoveType.NORMAL, result.getType());

        game.setTurn(false);

        result = game.tryMove(checker1, 3, 4);
        assertEquals(MoveType.NONE, result.getType());
        result = game.tryMove(checker1, 1, 4);
        assertEquals(MoveType.NONE, result.getType());
        result = game.tryMove(checker1, 4, 5);
        assertEquals(MoveType.KILL, result.getType());
    }
}
