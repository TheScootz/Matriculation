package matriculation.server;

import matriculation.client.MatriculationService;
import matriculation.shared.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.NoSuchElementException;

/**
 * The server-side implementation of the RPC service.
 *
 * @author Emre Shively
 * @version 1.0.0
 */
public class MatriculationServer extends RemoteServiceServlet implements MatriculationService {
    /** The first player that joined the online game. **/
    private HumanPlayer player1 = null;
    /** The second player that joined the online game. **/
    private HumanPlayer player2 = null;
    /** The name of the player who is taking their turn. **/
    private String activePlayer;
    /** The deck from which cards are drawn. **/
    private Deck stock;
    /** The index of the last card that was played. Context determines whose card that was. **/
    private Integer lastPlayIndex;
    /** The card that was last drawn from the deck. Context determines who drew it. **/
    private Card lastDrawnCard;
    
    /**
    * Called by a player when they join the online game.
    * Will tell them who their opponent is if they have one.
    * Throws an exception if there is an issue.
    * @param joining the player joining
    * @return their opponent, or null if they were first to join the game
    * @throws GameStartException if there was a problem joining the game
    **/
    public HumanPlayer joinGame(HumanPlayer joining) throws GameStartException {
        if (!FieldVerifier.isValidName(joining.getName()))
            throw new GameStartException("Your name must be at least 1 character");
        
        if (player1 == null) {
            player1 = joining;
            return null;
        }
        else if (player2 == null) {
            if (joining.getName().equals(player1.getName()))
                throw new GameStartException("The other player is using that name.");
            player2 = joining;
            startGame();
            return player1;
        }
        else
            throw new GameStartException("The game is full!");
    }
    
    /**
    * Called repeatedly by the first player while they wait for the second one to join.
    * @return the second player, which will be <code>null</code> if one hasn't joined yet
    **/
    public HumanPlayer pollForOpponent() {
        return player2;
    }
    
    /**
    * Called to determine if it is a player's turn
    * @param name name of the player who is checking
    * @return true if it is their turn
    **/
    public Boolean isMyTurn(String name) {
        return activePlayer.equals(name);
    }
    
    /**
    * Gets the number of cards remaining in the deck.
    * @return remaining size of deck
    **/
    public Integer getStockSize() {
        return new Integer(stock.size());
    }
    
    /**
    * Retrieves a player's hand
    * @param name name of the player whose hand is being fetched
    * @return that player's hand
    * @throws IllegalArgumentException if there is no player with that name
    **/
    public Pile getHand(String name) throws IllegalArgumentException {
        if (name.equals(player1.getName()))
            return player1.hand;
        else if (name.equals(player2.getName()))
            return player2.hand;
        else
            throw new IllegalArgumentException("There is no player by that name.");
    }
    
    /**
    * Plays a player's card.
    * @param name name of the player who is playing
    * @param index index of the card in their hand
    * @throws InvalidPlayException if playing that card breaks the rules
    * @throws IllegalArgumentException if there is no player with that name
    **/
    public void play(String name, int index) throws InvalidPlayException, IllegalArgumentException {
        if (name.equals(player1.getName())) {
            player1.play(player1.hand.get(index), player2);
            lastPlayIndex = index;
            player1.hand.remove(index);
            // Draw if possible
            try {
                lastDrawnCard = stock.draw();
                player1.hand.add(lastDrawnCard);
            } catch (NoSuchElementException e) {
                lastDrawnCard = null;
            }
            activePlayer = player2.getName();
        }
        else if (name.equals(player2.getName())) {
            player2.play(player2.hand.get(index), player1);
            lastPlayIndex = index;
            player2.hand.remove(index);
            // Draw if possible
            try {
                lastDrawnCard = stock.draw();
                player2.hand.add(lastDrawnCard);
            } catch (NoSuchElementException e) {
                lastDrawnCard = null;
            }
            activePlayer = player1.getName();
        }
        else
            throw new IllegalArgumentException("There is no player by that name.");
        
        checkGameOver();
    }
    
    /**
    * Discards a player's card.
    * @param name name of the player who is discarding
    * @param index index of the card in their hand
    * @throws IllegalArgumentException if there is no player with that name
    **/
    public void discard(String name, int index) throws IllegalArgumentException {
        if (name.equals(player1.getName())) {
            player1.hand.remove(index);
            lastPlayIndex = -index - 1;
            // Draw if possible
            try {
                lastDrawnCard = stock.draw();
                player1.hand.add(lastDrawnCard);
            } catch (NoSuchElementException e) {
                lastDrawnCard = null;
            }
            activePlayer = player2.getName();
        }
        else if (name.equals(player2.getName())) {
            player2.hand.remove(index);
            lastPlayIndex = -index - 1;
            // Draw if possible
            try {
                lastDrawnCard = stock.draw();
                player2.hand.add(lastDrawnCard);
            } catch (NoSuchElementException e) {
                lastDrawnCard = null;
            }
            activePlayer = player1.getName();
        }
        else
            throw new IllegalArgumentException("There is no player by that name.");
    }
    
    /**
    * Getter for {@link #lastPlayIndex}
    * @return value of {@link #lastPlayIndex}
    **/
    public Integer getLastPlayIndex() {
        return lastPlayIndex;
    }
    
    /**
    * Getter for {@link #lastDrawnCard}
    * @return value of {@link #lastDrawnCard}
    **/
    public Card getLastDrawnCard() {
        return lastDrawnCard;
    }
    
    /**
    * Determines if there are no more playable cards in the game, in the deck or in players' hands.
    * @return true if there are no more playable cards
    **/
    public Boolean outOfCards() {
        if (stock.size() == 0 && player1.hand.size() == 0 && player2.hand.size() == 0) {
            reset();
            return true;
        }
        else return false;
    }
    
    /**
    * Resets the game state on the server by erasing the players.
    **/
    public void reset() {
        player1 = null;
        player2 = null;
    }
    
    /**
    * Starts a new game, shuffling the deck and dealing cards.
    **/
    private void startGame() {
        stock = new Deck();
        activePlayer = player1.getName();
        for (int i = 0; i < Player.HANDSIZE; i++) {
            player1.hand.add(stock.draw());
            player2.hand.add(stock.draw());
        }
    }
    
    /**
    * Checks if a player has passed {@link Player#MAX_CREDITHOURS} and resets the game if so.
    **/
    private void checkGameOver() {
        if (player1.getCreditHours() >= Player.MAX_CREDITHOURS || player2.getCreditHours() >= Player.MAX_CREDITHOURS)
            reset();
    }
}
