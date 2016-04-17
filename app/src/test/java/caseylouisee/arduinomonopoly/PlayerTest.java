package caseylouisee.arduinomonopoly;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by caseylouisee on 17/04/2016.
 */
public class PlayerTest {

    /**
     * This method tests the getName() method in the player class, as well as ensuring the
     * constructor for creating a new player works.
     * @throws Exception
     */
    @Test
    public void getNameTest() throws Exception {
        Player player = new Player("player1");
        assertTrue(player.getName().equals("player1"));
    }

    /**
     * The Player class constructor sets the player's position to 0 when a new player is
     * initialised. This method therefore tests the constructor and the getPlace() method.
     * @throws Exception
     */
    @Test
    public void getPlaceTest() throws Exception {
        Player player = new Player("player1");
        assertTrue(player.getCurrentPosition()==0);
        player.setCurrentPosition(10);
        assertTrue(player.getCurrentPosition()==10);
    }


}
