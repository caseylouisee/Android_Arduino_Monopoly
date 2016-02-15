package caseylouisee.arduinomonopoly;


public class SpecialSquare extends Square {

    private final String name;
    private final int place;

    public SpecialSquare (String name, int place) {
        super(name, place);
        this.name = name;
        this.place = place;
    }

}
