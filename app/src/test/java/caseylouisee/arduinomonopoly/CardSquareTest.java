package caseylouisee.arduinomonopoly;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by Casey Denner on 17/4/2016.
 */
public class CardSquareTest {

    CardSquare cardSquare = new CardSquare("Card", 1);

    /**
     * This method tests the getName method, ensuring that when the card was constructed the name
     * was set correctly.
     * @throws Exception
     */
    @Test
    public void getNameTest() throws Exception {
        assertTrue(cardSquare.getName().equals("Card"));
    }

    /**
     * This method tests the getPlace method, ensuring that when the card was constructed the
     * place was set correctly.
     * @throws Exception
     */
    @Test
    public void getPlaceTest() throws Exception {
        assertTrue(cardSquare.getPlace()==1);
    }

}