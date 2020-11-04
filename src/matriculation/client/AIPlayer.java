package matriculation.client;

/**
* Implementation of an AI player. Determines what cards to play via computer logic.
*
* @author Emre Shively
* @version 1.0.0
**/
public class AIPlayer extends Player {
    protected AIPlayer(int startingCreditHours) {
        super(startingCreditHours);
        name = "AI";
        human = false;
    }
    
    /**
    * Currently plays the first valid card, or discards the first card if none are valid.
    * @param opponent The player's opponent.
    **/
    public void takeTurn(Player opponent) {
        int index;
        // Play the first valid card in the hand
        for (index = 0; index < hand.size(); index++) {
            // End loop if play is valid
            if (play(hand.get(index), opponent)) {
                //System.out.printf("AI played [%s]%n%n", hand.get(index).toString());
                break;
            }
        }
        // If condition is true, then no card could be played
        // So discard first card instead
        if (index == hand.size()) {
            //System.out.printf("AI discarded [%s]%n%n", hand.get(0).toString());
            hand.remove(0);
        }
        else hand.remove(index);
    }
}