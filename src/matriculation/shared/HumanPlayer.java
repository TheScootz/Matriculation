package matriculation.shared;

import Input.InputReader;

/**
* Implementation of a human player. Reads their input to determine what cards to play.
*
* @author Emre Shively
* @version 1.0.0
**/
public class HumanPlayer extends Player {
    /** Required for GWT serialization **/
    protected HumanPlayer() {}
    
    /** Creates the player
    * @param n name of the player
    * @param startingCreditHours number of credit-hours to start with
    **/
    public HumanPlayer(String n, int startingCreditHours) {
        super(n, startingCreditHours);
        human = true;
    }
    
    /** Dummy function because the interface requires it, handled in {@link matriculation.client.MatriculationClient#onClick} **/
    public int takeTurn(Player opponent) { return -1; }
}