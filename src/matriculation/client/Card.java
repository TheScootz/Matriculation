package matriculation.client;

/**
* An object representing a physical card in the game.
* Each card is of a category from {@link #Type} and contains additional information
* depending on the type.
*
* @author Emre Shively
* @version 1.0.0
**/
public class Card {
    /** Categories of cards. Determines the meaning of {@link #attribute}. **/
    public enum Type {
        /** Term card: <code>attribute</code> specifies number of credit-hours. **/
        TERM,
        /** Setback card: <code>attribute</code> determines which setback to impose. **/
        SETBACK,
        /** Fix card: <code>attribute</code> determines which setback to fix. **/
        FIX,
        /** Exception card: <code>attribute</code> determines which setback the player is immune to. **/
        EXCEPTION
    }
    
    /** For all card types except TERM, value of <code>attribute</code> for cards related to the "Caught Cheating" setback. **/
    public static final int CHEATING = 1;
    /** For all card types except TERM, value of <code>attribute</code> for cards related to the "Car Not Working" setback. **/
    public static final int NOCAR = 2;
    /** For all card types except TERM, value of <code>attribute</code> for cards related to the "Alarm Clock Broken" setback. **/
    public static final int NOALARM = 3;
    /** For all card types except TERM, value of <code>attribute</code> for cards related to the "Probation" setback. **/
    public static final int PROBATION = 4;
    /** For all card types except TERM, value of <code>attribute</code> for cards related to the "Ogre Prof" setback. **/
    public static final int OGREPROF = 5;
    
    /** An array that matches each setback value to its printable string. **/
    public static final String[] SETBACK_NAME = {"None", "Caught Cheating",
    "Car Not Working", "Alarm Clock Broken", "Probation", "Ogre Prof"};
    
    /** The type of card. **/
    public final Type type;
    /** The card's attribute, whose meaning is determined by {@link #type}. **/
    public final int attribute;
    
    /**
    * Generates a new card. Will print a warning if it is not a valid card
    * but will not throw an exception.
    * @param t Value for {@link #type}
    * @param a Value for {@link #attribute}
    **/
    protected Card(Type t, int a) {
        // Warn if card is invalid according to the rules
        if ((t == Type.TERM && (a < 3 || a > 21 || a % 3 != 0)) || (t != Type.TERM && (a < CHEATING || a > OGREPROF))) {
            System.out.println("WARNING: An invalid card has been generated (" + t + "," + a + ")");
        }
        type = t;
        attribute = a;
    }
    
    /**
    * Generates a printable string representation of the card.
    * @return String representation of the card.
    **/
    public String toString() {
        if (type == Type.TERM)
            return Integer.toString(attribute) + " Credit-Hours";
        else {
            StringBuilder output = new StringBuilder();
            
            switch (type) {
                case SETBACK: output.append("Setback: "); break;
                case FIX: output.append("Fix: "); break;
                case EXCEPTION: output.append("Exception: "); break;
            }
            output.append(SETBACK_NAME[attribute]);
            
            return output.toString();
        }
    }
}