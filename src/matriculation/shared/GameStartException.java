package matriculation.shared;

/**
* Exception thrown when a player attempts to join the online game but there was an issue.
**/
public class GameStartException extends Exception implements java.io.Serializable {
    public GameStartException(String errorMessage) {
        super(errorMessage);
    }
    
    /** Required for GWT serialization **/
    private GameStartException() {}
}