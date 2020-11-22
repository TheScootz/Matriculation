package matriculation.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

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
    
    public int indexOf(Card card) {
        return cards.indexOf(card);
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
    
    public Card top() {
        if (size() > 0)
            return cards.get(cards.size() - 1);
        else return null;
    }
    
    /**
    * Create an HTML <ul> list of cards in the pile, from top to bottom (i.e. reverse order).
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