package matriculation.client;

import java.util.ArrayList;
import java.util.Iterator;

/**
* Class for player piles. Mostly acts as a wrapper class for ArrayList<Card> currently.
*
* @author Emre Shively
* @version 1.0.0
**/
public class Pile {
    public ArrayList<Card> cards;
    
    protected Pile() {
        cards = new ArrayList<Card>();
    }
    
    public Iterator<Card> iterator() {
        return cards.iterator();
    }
    
    public int size() {
        return cards.size();
    }
    
    public void add(Card card) {
        cards.add(card);
    }
    
    public Card get(int index) {
        return cards.get(index);
    }
    
    public void remove(int index) {
        cards.remove(index);
    }
    
    /**
    * Print a human-readable list of cards in the pile to stdout.
    * @return String The list.
    **/
    public String listCards() {
        Iterator<Card> cardIter = iterator();
        StringBuilder output = new StringBuilder();
        while (cardIter.hasNext()) {
            output.append("[" + cardIter.next().toString() + "]");
            if (cardIter.hasNext()) output.append(", ");
        }
        if (output.length() == 0) output.append("None");
        return output.toString();
    }
}