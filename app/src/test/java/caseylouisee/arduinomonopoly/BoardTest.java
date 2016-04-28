package caseylouisee.arduinomonopoly;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Casey Denner on 17/4/2016.
 */
public class BoardTest {

    /**
     * This test checks that when a Board object is initialized, the squares on the board
     * are also initializes and set to the correct positions and names. Square 0 on the board
     * should therefore be the square "Go".
     * @throws Exception
     */
    @Test
    public void board_gets_correct_square_pos() throws Exception {
        Board board = new Board();
        assertTrue(board.getSquare(0).getName().equals("Go"));
    }

    /**
     * This test checks that the board "getSquares" method doesn't return null. This means that the
     * squares arrayList has therefore been initialized.
     * @throws Exception
     */
    @Test
    public void get_squares_test() throws Exception {
        Board board = new Board();
        assertNotNull(board.getSquares());
    }

}