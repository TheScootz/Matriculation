package matriculation.client;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;

/**
* Implementation of an AI player. Determines what cards to play via computer logic.
*
* @author Emre Shively
* @version 1.0.0
**/
public class AIPlayer extends Player {
    /** Creates the player
    * @param startingCreditHours number of credit-hours to start with
    **/
    protected AIPlayer(int startingCreditHours) {
        super(startingCreditHours);
        name = "AI";
        human = false;
    }
    
    /**
    * Algorithmically decide which card to play. Tries to play cards in the following priority:
    * <ol>
    * <li>Term card that will win the game</li>
    * <li>Opponent setback</li>
    * <li>Fix active setback with exception</li>
    * <li>Fix active setback with fix</li>
    * <li>Exception</li>
    * <li>Highest term card</li>
    * </ol>
    * If there are no valid cards, will discard a card with the following priority:
    * <ol>
    * <li>Fix for active exception</li>
    * <li>Setback for active opponent exception</li>
    * <li>Lowest term card</li>
    * <li>First card in hand</li>
    * </ol>
    * @param opponent The player's opponent.
    **/
    public int takeTurn(Player opponent) {
        Logger logger = Logger.getLogger("");
        int index;
        Iterator<Card> handIter = hand.iterator();
        Card next;
        
        // First, check if we can win
        logger.log(Level.SEVERE, "Trying to win...");
        if (activeSetback != SB_NONE) {
            while (handIter.hasNext()) {
                next = handIter.next();
                if (next.type == Card.Type.TERM && getCreditHours() + next.attribute >= MAX_CREDITHOURS) {
                    try {
                        play(next, opponent);
                        return hand.indexOf(next);
                    } catch (InvalidPlayException e) {}
                }
            }
        }
        
        // Then, check if we can set back the opponent
        // Potential improvement: prioritize hard setbacks
        logger.log(Level.SEVERE, "Trying to set you back...");
        handIter = hand.iterator();
        while (handIter.hasNext()) {
            next = handIter.next();
            if (next.type == Card.Type.SETBACK) {
                try {
                    play(next, opponent);
                    return hand.indexOf(next);
                } catch (InvalidPlayException e) {}
            }
        }
        
        // Then, check if we can fix our own setback with an exception
        // Needs to be expanded when coup fourré is added
        // Potential improvement: prioritize hard setbacks
        logger.log(Level.SEVERE, "Trying to fix my setback permanently...");
        handIter = hand.iterator();
        while (handIter.hasNext()) {
            next = handIter.next();
            if (next.type == Card.Type.EXCEPTION && (next.attribute == activeSetback
                || (next.attribute == Card.PROBATION && probation)
                || (next.attribute == Card.OGREPROF && ogreProf))) {
                try {
                    play(next, opponent);
                    return hand.indexOf(next);
                } catch (InvalidPlayException e) {}
            }
        }
        
        // Then, check if we can fix our own setback with a fix
        // Potential improvement: prioritize hard setbacks
        logger.log(Level.SEVERE, "Trying to fix my setback temporarily...");
        handIter = hand.iterator();
        while (handIter.hasNext()) {
            next = handIter.next();
            if (next.type == Card.Type.FIX) { // play() determines whether the fix is valid
                try {
                    play(next, opponent);
                    return hand.indexOf(next);
                } catch (InvalidPlayException e) {}
            }
        }
        
        // Then, check if we can play an exception
        // Potential improvement: prioritize hard setbacks
        logger.log(Level.SEVERE, "Trying to play an exception...");
        handIter = hand.iterator();
        while (handIter.hasNext()) {
            next = handIter.next();
            if (next.type == Card.Type.EXCEPTION) {
                try {
                    play(next, opponent);
                    return hand.indexOf(next);
                } catch (InvalidPlayException e) {}
            }
        }
        
        // Finally, play the highest term card
        // Potential improvement: prioritize hard setbacks
        logger.log(Level.SEVERE, "Trying to get some credit-hours...");
        if (activeSetback == SB_NONE) {
            handIter = hand.iterator();
            int highestTermIndex = -1;
            int highestTermValue = 0;
            while (handIter.hasNext()) {
                next = handIter.next();
                if (next.type == Card.Type.TERM && next.attribute > highestTermValue) {
                    highestTermIndex = hand.indexOf(next);
                    highestTermValue = next.attribute;
                }
            }
            if (highestTermIndex >= 0) {
                try {
                    play(hand.get(highestTermIndex), opponent);
                    return highestTermIndex;
                } catch (InvalidPlayException e) {}
            }
        }
        
        
        // If we got here, we must discard :(
        // First, discard a fix for an exception we have
        logger.log(Level.SEVERE, "Trying to discard an unneeded fix...");
        handIter = hand.iterator();
        while (handIter.hasNext()) {
            next = handIter.next();
            if (next.type == Card.Type.FIX && hasException(next.attribute)) {
                return -hand.indexOf(next) - 1;   // how to calculate discard returns
            }
        }
        
        // Then, discard a setback for an exception they have
        logger.log(Level.SEVERE, "Trying to discard an unneeded setback...");
        handIter = hand.iterator();
        while (handIter.hasNext()) {
            next = handIter.next();
            if (next.type == Card.Type.SETBACK && opponent.hasException(next.attribute)) {
                return -hand.indexOf(next) - 1;
            }
        }
        
        // Potential improvement: discard duplicate setbacks/fixes here
        // Too much work for this project: discard fixes for exhausted setbacks
        
        // Then, discard the lowest term card
        logger.log(Level.SEVERE, "Discarding my lowest term card...");
        handIter = hand.iterator();
        int lowestTermIndex = -1;
        int lowestTermValue = 22;
        while (handIter.hasNext()) {
            next = handIter.next();
            if (next.type == Card.Type.TERM && next.attribute < lowestTermValue) {
                lowestTermIndex = hand.indexOf(next);
                lowestTermValue = next.attribute;
            }
        }
        if (lowestTermIndex >= 0)
            return -lowestTermIndex - 1;
        
        // Failsafe: discard first card
        return -1;
    }
}