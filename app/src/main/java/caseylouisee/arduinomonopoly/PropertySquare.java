package caseylouisee.arduinomonopoly;

/**
 * PropertySquare class extends Square
 * Created by Casey Denner on 26/10/2015.
 * @see Square
 */
public class PropertySquare extends Square {

    /**
     * String CONSTANT that holds the name of the property square
     */
    private final String NAME;

    /**
     * int CONSTANT that represents the position of the square on the board
     */
    private final int PLACE;

    /**
     * int CONSTANT that holds the price of the property
     */
    private final int PRICE;

    /**
     * String CONSTANT that represents the property colour/group
     */
    private final String GROUP;

    /**
     * Boolean shows whether the property is owned or not
     */
    private Boolean m_owned;

    /**
     * String of the player's name who owns the property
     */
    private String m_ownedBy;

    /**
     * int representing the number of houses on the property
     */
    private int m_numHouses;

    /**
     * Constructor for the PropertySquare
     * @param name name of the property
     * @param place position on the board
     * @param price price of the property
     * @param group colour/group of the property
     */
    public PropertySquare(String name, int place, int price, String group){
        super(name, place);
        NAME = name;
        PLACE = place;
        PRICE = price;
        GROUP = group;
        // By default on creation no properties are owned by any player.
        m_owned = false;
        // No properties have houses at the start of the game.
        m_numHouses = 0;
    }

    /**
     * Accessor for the property price
     * @return m_price
     */
    public int getPrice() {
        return PRICE;
    }

    /**
     * Accessor for the property group
     * @return M_GROUP
     */
    public String getGroup() {
        return GROUP;
    }

    /**
     * Accessor for the boolean of the property being owned
     * @return m_owned
     */
    public Boolean getOwned() {
        return m_owned;
    }

    /**
     * Accessor for the number of houses on the property
     * @return m_numHouses
     */
    public int getNumHouses() {
        return m_numHouses;
    }

    /**
     * Method to set the ownership of the property to a certain player
     * @param playerName owner of the property
     */
    public void setOwnedBy(String playerName) {
        m_ownedBy = playerName;
        m_owned = true;
    }

    /**
     * Accessor for the name of the player who owns the property
     * @return m_ownedBy
     */
    public String getOwnedBy() {
        return m_ownedBy;
    }


    /**
     * Method to add a house to the property
     */
    public void addHouse() {
        m_numHouses++;
    }



}
