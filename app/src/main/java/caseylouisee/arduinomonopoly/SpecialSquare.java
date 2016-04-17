package caseylouisee.arduinomonopoly;

/**
 * SpecialSquare class extends Square
 * Created by Casey Denner
 * @see Square
 */
public class SpecialSquare extends Square {

    /**
     * String CONSTANT representing the SpecialSquare's name
     */
    private final String NAME;

    /**
     * int CONSTANT representing the position of the square on the board
     */
    private final int PLACE;

    /**
     * Constructor for a new Special Square
     * @param name the name of the square
     * @param place the position of the square on the board
     */
    public SpecialSquare (String name, int place) {
        super(name, place);
        NAME = name;
        PLACE = place;
    }

}
