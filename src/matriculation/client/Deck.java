package matriculation.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
* An object representing the physical deck from which cards are drawn.
* Specifies how many copies of each card are in the game.
*
* @author Emre Shively
* @version 1.0.0
**/
public class Deck {
    
    /** Shuffled list of cards in the deck, used as a stack **/
    private ArrayDeque<Card> cards;
    /** Determines what cards and how many of each to shuffle into the deck **/
    private HashMap<Card, Integer> cardQuantities = new HashMap<Card, Integer>(21);
    
    /** Generates and shuffles a new deck. **/
    protected Deck() {
        cardQuantities.put(new Card(Card.Type.TERM, 6), 10);
        cardQuantities.put(new Card(Card.Type.TERM, 9), 10);
        cardQuantities.put(new Card(Card.Type.TERM, 12), 8);
        cardQuantities.put(new Card(Card.Type.TERM, 15), 15);
        cardQuantities.put(new Card(Card.Type.TERM, 18), 5);
        cardQuantities.put(new Card(Card.Type.TERM, 21), 3);
        cardQuantities.put(new Card(Card.Type.SETBACK, Card.CHEATING), 3);
        cardQuantities.put(new Card(Card.Type.SETBACK, Card.NOCAR), 3);
        cardQuantities.put(new Card(Card.Type.SETBACK, Card.NOALARM), 3);
        cardQuantities.put(new Card(Card.Type.SETBACK, Card.PROBATION), 4);
        cardQuantities.put(new Card(Card.Type.SETBACK, Card.OGREPROF), 3);
        cardQuantities.put(new Card(Card.Type.FIX, Card.CHEATING), 6);
        cardQuantities.put(new Card(Card.Type.FIX, Card.NOCAR), 6);
        cardQuantities.put(new Card(Card.Type.FIX, Card.NOALARM), 6);
        cardQuantities.put(new Card(Card.Type.FIX, Card.PROBATION), 6);
        cardQuantities.put(new Card(Card.Type.FIX, Card.OGREPROF), 6);
        cardQuantities.put(new Card(Card.Type.EXCEPTION, Card.CHEATING), 1);
        cardQuantities.put(new Card(Card.Type.EXCEPTION, Card.NOCAR), 1);
        cardQuantities.put(new Card(Card.Type.EXCEPTION, Card.NOALARM), 1);
        cardQuantities.put(new Card(Card.Type.EXCEPTION, Card.PROBATION), 1);
        cardQuantities.put(new Card(Card.Type.EXCEPTION, Card.OGREPROF), 1);
        shuffle();
    }
    
    /**
    * Shuffles the cards, using {@link #cardQuantities} to randomly sort the
    * cards into {@link #cards}.
    **/
    public void shuffle() {
        // Container for every individual card that will be in the deck
        ArrayList<Card> unshuffledCards = new ArrayList<Card>();
        
        // Turn card quantities into individual cards
        // This will iterate over each key (card) in the cardQuantities map
        // And create the number of copies of that card equal to its value in the map
        Iterator<Card> cardIter = cardQuantities.keySet().iterator();
        int i, quantity;
        Card currentCard;
        while (cardIter.hasNext()) {
            currentCard = cardIter.next();
            quantity = cardQuantities.get(currentCard);
            for (i = 0; i < quantity; i++) {
                unshuffledCards.add(currentCard);
                //System.out.print(currentCard.type);
                //System.out.println(currentCard.attribute);
            }
        }
        
        // Then randomly select cards from unshuffledCards until it is empty
        cards = new ArrayDeque<Card>();
        int index;
        Random rng = new Random();
        while (unshuffledCards.size() > 0) {
            index = rng.nextInt(unshuffledCards.size());
            currentCard = unshuffledCards.get(index);
            //System.out.print(currentCard.type);
            //System.out.println(currentCard.attribute);
            cards.add(currentCard);
            unshuffledCards.remove(index);
        }
    }
    
    /**
    * Draw the top card from the deck, removing it from the deck.
    * @return The top card.
    **/
    public Card draw() {
        return cards.removeFirst();
    }
    
    /**
    * Returns the number of cards in the deck.
    * @return the number of cards remaining in the deck.
    **/
    public int size() {
        return cards.size();
    }
}