package matriculation.client;
import java.util.logging.*;

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
    public int takeTurn(Player opponent) {
        Logger logger = Logger.getLogger("");
        int index;
        // First, check if we can win
        for (index = 0; index < hand.size(); index++) {
            try {
                play(hand.get(index), opponent);
                logger.log(Level.SEVERE, "We got a card!");
                return index;
            } catch (InvalidPlayException e) {
                logger.log(Level.SEVERE, e.getMessage());
                continue;
            }
        }
        // If condition is true, then no card could be played
        // So discard first card instead
        logger.log(Level.SEVERE, "We discarded.");
        return -1;
    }
}