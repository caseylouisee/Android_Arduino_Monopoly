package caseylouisee.arduinomonopoly;

/**
 * Player class that manages the player
 * Created by Casey Denner on 26/10/2015.
 */
public class Player {

    /**
     * String CONSTANT representing the player's name
     */
    private final String NAME;

    /**
     * int holding the player's current position
     */
    private int m_currentPosition;

    /**
     * int holding the player's current funds
     */
    private int m_money;

    /**
     * Boolean representing if a player is in jail(TRUE) or not (FALSE)
     */
    private Boolean m_jail = false;

    /**
     * Constructor for a new Player
     * Sets the name, position(0) and funds(1500).
     * @param name name of the player
     */
    public Player (String name){
        NAME = name;
        // When a new player is created they start at go(0).
        m_currentPosition = 0;
        m_money = 1500;
    }

    /**
     * Accessor for the player's name
     * @return NAME the player's name
     */
    public String getName() {
        return NAME;
    }

    /**
     * Method to set the position of a player
     * @param currentPosition the position you wish to set
     */
    public void setCurrentPosition(int currentPosition) {
        m_currentPosition = currentPosition;
    }

    /**
     * Accessor for the player's current position
     * @return m_currentPosition the player's current position
     */
    public int getCurrentPosition() {
        return m_currentPosition;
    }

    /**
     * Accessor for the player's funds
     * @return m_money the player's funds
     */
    public int getMoney() {
        return m_money;
    }

    /**
     * Method to set jail to true or false
     * @param bool True = player is in jail, False = player is not in jail
     */
    public void setJail(Boolean bool){
        m_jail = bool;
    }

    /**
     * Accessor to find whether a player is in jail or not
     * @return m_jail True = player is in jail, False = player is not in jail
     */
    public Boolean getJail() {
        return m_jail;
    }

    /**
     * Method to subtract money from a player
     * @param value the value to subtract
     */
    public void subtractMoney(int value){
        m_money = m_money-value;
    }

    /**
     * Method to add money to a player's funds
     * @param value the value to add
     */
    public void addMoney(int value){
        m_money = m_money + value;
    }

}
