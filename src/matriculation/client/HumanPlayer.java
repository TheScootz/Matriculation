package matriculation.client;

import Input.InputReader;

/**
* Implementation of a human player. Reads their input to determine what cards to play.
*
* @author Emre Shively
* @version 1.0.0
**/
public class HumanPlayer extends Player {    
    protected HumanPlayer(int startingCreditHours) {
        super(startingCreditHours);
        name = "Human";
        human = true;
    }
    
    public void takeTurn(Player opponent) {
        char action;
        int index;
        boolean result;
        
        // Get input
        while (true) { // Loop is broken if valid input is received
            try {
                System.out.print("Enter index of card: ");
                index = InputReader.readInt();
                System.out.print("Do you want to play (p) or discard (d) it? ");
                action = InputReader.readChar();
            } catch (java.io.IOException|NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }
                
            if (action != 'P' && action != 'D')
                System.out.println("Invalid action, must be p (play) or d (discard).");
            else if (index < 0 || index > hand.size() - 1)
                System.out.println("Invalid index! (Must be between 0 and " + (hand.size() - 1) + ")");
            else if (action == 'P') {
                if (play(hand.get(index), opponent)) {
                    System.out.printf("You played [%s]%n%n", hand.get(index).toString());
                    break;
                }
                else
                    System.out.println("Play something else.");
            }
            else if (action == 'D') {
                System.out.printf("You discarded [%s]%n%n", hand.get(index).toString());
                break;
            }
        }
        
        // At this point we've either played a card or we're discarding, so get rid of it
        hand.remove(index);
        
    }
}