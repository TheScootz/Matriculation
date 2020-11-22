package matriculation.client;

import Input.InputReader;

/**
* Implementation of a human player. Reads their input to determine what cards to play.
*
* @author Emre Shively
* @version 1.0.0
**/
public class HumanPlayer extends Player {
    /** Creates the player
    * @param startingCreditHours number of credit-hours to start with
    **/
    protected HumanPlayer(int startingCreditHours) {
        super(startingCreditHours);
        name = "Human";
        human = true;
    }
    
    /** Dummy function because the interface requires it, handled in {@link Matriculation#onClick} **/
    public int takeTurn(Player opponent) { return -1; }
}