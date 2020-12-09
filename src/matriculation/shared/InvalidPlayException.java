package matriculation.shared;

/**
* Exception thrown when a player attempts to play a card that breaks the rules.
**/
public class InvalidPlayException extends Exception implements java.io.Serializable {
    public InvalidPlayException(String errorMessage) {
        super(errorMessage);
    }
    
    /** Required for GWT serialization **/
    protected InvalidPlayException() {}
}