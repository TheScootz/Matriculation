package matriculation.client;

/**
* Exception thrown when a player attempts to play a card that breaks the rules.
**/
class InvalidPlayException extends Exception {
    protected InvalidPlayException(String errorMessage) {
        super(errorMessage);
    }
}