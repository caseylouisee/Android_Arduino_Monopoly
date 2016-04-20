package caseylouisee.arduinomonopoly;

/**
 * Square abstract class
 * Created by Casey Denner on 26/10/2015.
 */
public abstract class Square {

    /**
     * CONSTANT String that holds the square's name
     */
    private final String NAME;

    /**
     * CONSTANT int that represents the square's position on the board
     */
    private final int PLACE;

    /**
     * Square constructor
     * @param name The name of the square
     * @param place The position of the square on the board
     */
    public Square(String name, int place) {
        NAME = name;
        PLACE = place;
    }

    /**
     * Accessor for the square name
     * @return m_name which is the name of the square
     */
    public String getName() {
        return NAME;
    }

    /**
     * Accessor for the square's location
     * @return m_place the location of the square on the board
     */
    public int getPlace() {
        return PLACE;
    }

}
