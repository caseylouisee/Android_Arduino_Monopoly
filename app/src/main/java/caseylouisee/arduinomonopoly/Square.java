package caseylouisee.arduinomonopoly;

/**
 * Square abstract class
 * Created by Casey Denner on 26/10/2015.
 */
public abstract class Square {

    /**
     * String that holds the square's name
     */
    String m_name;

    /**
     * int that represents the square's position on the board
     */
    int m_place;

    /**
     * Square constructor
     * @param name The name of the square
     * @param place The position of the square on the board
     */
    public Square(String name, int place) {
        m_name = name;
        m_place = place;
    }

    /**
     * Accessor for the square name
     * @return m_name which is the name of the square
     */
    public String getName() {
        return m_name;
    }

    /**
     * Accessor for the square's location
     * @return m_place the location of the square on the board
     */
    public int getPlace() {
        return m_place;
    }

}
