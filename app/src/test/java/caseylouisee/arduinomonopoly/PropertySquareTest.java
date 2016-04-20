package caseylouisee.arduinomonopoly;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by caseylouisee on 20/04/2016.
 */
public class PropertySquareTest {

    /**
     * This method tests the getName method in the Square class, ensuring the correct square
     * name is returned after it is constructed. This calls the Square's method even though
     * it is being called on a PropertySquare object as the method is inherited.
     * @throws Exception
     */
    @Test
    public void getNameTest()throws Exception{
        PropertySquare propertySquare = new PropertySquare("NameTest", 0, 100, "Red");
        assertTrue(propertySquare.getName().equals("NameTest"));
    }

    /**
     * This method tests the getPlace method in the Square class. The square class is an abstract
     * class and therefore Property square has inherited its method.
     * @throws Exception
     */
    @Test
    public void getPlaceTest() throws Exception{
        PropertySquare propertySquare = new PropertySquare("PlaceTest", 0, 100, "Red");
        assertTrue(propertySquare.getPlace()==0);
    }

    /**
     * This method tests the getPrice method in PropertySquare, ensuring the correct price
     * of the square is returned after being constructed.
     * @throws Exception
     */
    @Test
    public void getPriceTest() throws Exception{
        PropertySquare propertySquare = new PropertySquare("PriceTest", 0, 100, "Red");
        assertTrue(propertySquare.getPrice()==100);
    }

    /**
     * This method tests the getGroup method in the PropertySquare class, ensuring the correct
     * group of the square is returned after being constructed.
     * @throws Exception
     */
    @Test
    public void getGroupTest() throws Exception{
        PropertySquare propertySquare = new PropertySquare("PlaceTest", 0, 100, "Red");
        assertTrue(propertySquare.getGroup().equals("Red"));
    }

    /**
     * This method tests the setOwnedBy and getOwnedBy method as well as the getOwned method
     * in the PropertySquare class, ensuring the correct owner name is returned after being set and
     * also ensuring the owned boolean is changed.
     * @throws Exception
     */
    @Test
    public void getSetOwnedByTest() throws Exception{
        PropertySquare propertySquare = new PropertySquare("OwnedTest", 0, 100, "Red");
        assertFalse(propertySquare.getOwned());
        propertySquare.setOwnedBy("Owner's Name");
        assertTrue(propertySquare.getOwned());
        assertTrue(propertySquare.getOwnedBy().equals("Owner's Name"));
    }

    /**
     * This method tests the getNumHouses and the addHouse method in the PropertySquare class,
     * ensuring the house count for that property is increased when needed.
     * @throws Exception
     */
    @Test
    public void getNumAddHousesTest() throws Exception{
        PropertySquare propertySquare = new PropertySquare("HousesTest", 0, 100, "Red");
        assertTrue(propertySquare.getNumHouses()==0);
        propertySquare.addHouse();
        assertTrue(propertySquare.getNumHouses()==1);
    }

}
