package matriculation.client;

import java.util.NoSuchElementException;

/**
* Controller class for the game.
* This class handles actual game flow, making the players take their turns,
* printing relevant information, deciding when the game is over, and determining
* the winner.
*
* @author Emre Shively
* @version 1.0.0
**/
public class Controller {
    /** Determines whether the AI player's card should be printed. False by default **/
    public boolean showAICards;
    /** 
    * End the game after this many turns. 
    * If it is not a positive number, there is no limit
    * 0 by default
    **/
    public int endAfter;
    /** Number of credit-hours to start players with. 0 by default. **/
    public int startingCreditHours;
    
    
    /** The deck from which cards are drawn. **/
    private Deck stock;
    /** The human player. **/
    private HumanPlayer human;
    /** The AI player. **/
    private AIPlayer ai;
    /** Number of turns that have passed so far. **/
    private int turns;
    
    /** Constructs a new Matriculation controller ready to start a new game. **/
    protected Controller() {
        showAICards = false;
        endAfter = 0;
        startingCreditHours = 0;
        turns = 0;
    }
    
    /**
    * Starts a new game.
    * A new deck is shuffled, and new human and AI players are generated.
    * Cards are dealt to each player, the number of which is determined by
    * {@link Player#HANDSIZE HANDSIZE}.
    **/
    public void startGame() {
        stock = new Deck();
        human = new HumanPlayer(startingCreditHours);
        ai = new AIPlayer(startingCreditHours);
        
        for (int i = 0; i < Player.HANDSIZE; i++) {
            human.hand.add(stock.draw());
            ai.hand.add(stock.draw());
        }
    }
    
    /**
    * Plays one round of the game.
    * Human player plays first, followed by the AI.
    * After each turn, checks if a game-ending condition is met and ends the game if so.
    * @see HumanPlayer#takeTurn(Player)
    * @see AIPlayer#takeTurn(Player)
    **/
    public void playRound() {
        human.printState();
        human.listHand();
        // Make sure they have cards
        if (human.hand.size() > 0)
            human.takeTurn(ai);
        
        // Check for winner
        if (human.getCreditHours() >= Player.MAX_CREDITHOURS)
            endGame(human, true);
        
        // Draw if there are still cards in the deck
        try {
            human.hand.add(stock.draw());
        } catch (NoSuchElementException e) {}
        
        ai.printState();
        if (showAICards)
            ai.listHand();
        // Make sure they have cards
        if (ai.hand.size() > 0)
            ai.takeTurn(human);
        
        // Check for winner
        if (ai.getCreditHours() >= Player.MAX_CREDITHOURS)
            endGame(ai, true);
        
        // Draw if there are still cards in the deck
        try {
            ai.hand.add(stock.draw());
        } catch (NoSuchElementException e) {}
        
        // End game if max turns is reached or if there are no cards anywhere
        turns++;
        if (endAfter > 0 && turns == endAfter)
            endGame(human, false);
        if (stock.size() == 0 && ai.hand.size() == 0 && human.hand.size() == 0)
            endGame(human, false);
        printScores();
    }
    
    /** Prints the players' scores to stdin in a tabular format. **/
    public void printScores() {
        System.out.printf("SCORES: %5s %5s%n", human.getName(), ai.getName());
        System.out.printf("        %5d %5d%n%n%n", human.getScore(), ai.getScore());
    }
    
    /**
    * Ends the game and determines the winner.
    * Currently exits the program when complete.
    * @param ender The player who ended the game. Unused if neither player graduated.
    * @param graduated True if the game ended via a player graduating.
    **/
    public void endGame(Player ender, boolean graduated) {
        // Display who graduated
        if (graduated) {
            System.out.printf("%s has reached %d credit-hours and graduated. Game over!%n%n", ender.name, Player.MAX_CREDITHOURS);
            ender.addToScore(Player.GRADUATE_VALUE);
            if (ender.getTerms() <= Player.GRADUATE_BONUS_TERMS)
                ender.addToScore(Player.GRADUATE_BONUS);
        }
        // Otherwise no one graduated
        else 
            System.out.println("There are no more cards. Game over!\n");
        
        // Show scores and declare a winner
        printScores();
        if (human.getScore() > ai.getScore())
            System.out.println(human.getName() + " wins!");
        else if (human.getScore() < ai.getScore())
            System.out.println(ai.getName() + " wins!");
        else
            System.out.println("There was a tie! Everyone wins :)");
        System.exit(0);
    }
}