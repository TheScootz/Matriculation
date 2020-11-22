package matriculation.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
* Class for player piles. Mostly acts as a wrapper class for ArrayList&lt;Card&gt; currently.
*
* @author Emre Shively
* @version 1.0.0
**/
public class Pile {
    public ArrayList<Card> cards;
    
    /**
    * Constructs an empty Pile
    **/
    protected Pile() {
        cards = new ArrayList<Card>();
    }
    
    /**
    * Get an Iterator for the pile
    * @return the pile's Iterator object
    **/
    public Iterator<Card> iterator() {
        return cards.iterator();
    }
    
    /**
    * Get the number of cards in the pile
    * @return pile size
    **/
    public int size() {
        return cards.size();
    }
    
    /**
    * Get the index of the card in the pile, or -1 if it is not in the pile
    * @param card the card to search for
    * @return the index
    **/
    public int indexOf(Card card) {
        return cards.indexOf(card);
    }
    
    /**
    * Add a card to the pile
    * @param card the card to add
    **/
    public void add(Card card) {
        cards.add(card);
    }
    
    /**
    * Get a card by index
    * @param index the index
    * @return the card at that index
    **/
    public Card get(int index) {
        return cards.get(index);
    }
    
    /**
    * Remove a card from the pile
    * @param index the index of the card to remove
    **/
    public void remove(int index) {
        cards.remove(index);
    }
    
    /**
    * Get the top card (highest index)
    * @return the top card
    **/
    public Card top() {
        if (size() > 0)
            return cards.get(cards.size() - 1);
        else return null;
    }
    
    /**
    * Create an HTML &lt;ul&gt; list of cards in the pile, from top to bottom (i.e. reverse order).
    * @return String The HTML list
    **/
    public String listCardsHTML() {
        ListIterator<Card> cardIter = cards.listIterator(size());
        StringBuilder output = new StringBuilder("<ul>");
        while (cardIter.hasPrevious())
            output.append("<li>" + cardIter.previous().toString() + "</li>");
        if (output.length() == 0) output.append("None");
        output.append("</ul>");
        return output.toString();
    }
}