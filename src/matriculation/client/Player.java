package matriculation.client;

import java.util.ArrayList;
import java.util.Iterator;

/**
* An abstract class representing a generic player of the game. This class
* handles everything that doesn't vary between human and AI logic, which
* currently is everything except {@link #takeTurn(Player) taking turns}.
*
* @author Emre Shively
* @version 1.0.0
**/
public abstract class Player {
    /** Constant representing how many cards are in a player's hand (while cards remain in the deck). **/
    public static final int HANDSIZE = 7;
    /** Credit-hours required to graduate (and thus end the game). **/
    public static final int MAX_CREDITHOURS = 120;
    /** Maximum number of credit-hours that can be played while on probation. **/
    public static final int PROBATION_LIMIT = 9;
    /** Credit-hours subtracted from term cards while the Ogre Prof setback is active **/
    public static final int OGREPROF_PENALTY = 6;
    /** Number of points for each credit-hour obtained. **/
    public static final int CREDITHOUR_VALUE = 5;
    /** Number of points for graduating. **/
    public static final int GRADUATE_VALUE = 100;
    /** Number of points for graduating in less than {@link #GRADUATE_BONUS_TERMS} terms. **/
    public static final int GRADUATE_BONUS = 200;
    /** Maximum number of terms to get the graduation bonus. **/
    public static final int GRADUATE_BONUS_TERMS = 10;
    /** Number of points for each exception played **/
    public static final int EXCEPTION_VALUE = 100;
    /** Number of points for playing all exceptions **/
    public static final int EXCEPTION_BONUS = 200;
    
    /** Bitmask flag of {@link #exceptions} for the Caught Cheating exception. **/
    public static final int EX_CHEATING = 0x01;
    /** Bitmask flag of {@link #exceptions} for the Car Not Working exception. **/
    public static final int EX_NOCAR = 0x02;
    /** Bitmask flag of {@link #exceptions} for the Alarm Clock Broken exception. **/
    public static final int EX_NOALARM = 0x04;
    /** Bitmask flag of {@link #exceptions} for the Probation exception. **/
    public static final int EX_PROBATION = 0x08;
    /** Bitmask flag of {@link #exceptions} for the Ogre Prof exception. **/
    public static final int EX_OGREPROF = 0x10;
    /** Bitmask flag of {@link #exceptions} for all exceptions. **/
    public static final int EX_ALL = 0x1F;
    /** Array that associates setbacks with their respective exceptions. **/
    public static final int[] EX_ARRAY = {0x00, EX_CHEATING, EX_NOCAR, EX_NOALARM, EX_PROBATION, EX_OGREPROF};
    
    /** Value of {@link #activeSetback} when no control setbacks are active. **/
    public static final int SB_NONE = 0;
    
    /** Name of the player **/
    protected String name;
    /** Number of credit-hours attained by the player. **/
    protected int creditHours;
    /** The player's score **/
    protected int score;
    /** The player's active control setback (i.e. does not include probation and ogre prof). **/
    protected int activeSetback;
    /** Whether the ogre prof setback is active. **/
    protected boolean ogreProf;
    /** Whether the probation setback is active. **/
    protected boolean probation;
    /** Bitmask representing which exceptions the player has played. **/
    protected int exceptions;   // this is a bitmask, see above
    /** Number of credit-hour cards the player has played. **/
    protected int terms;
    /** True if the player is a human player. **/
    protected boolean human;
    /** The player's control pile. **/
    public Pile controlPile;
    /** The player's probation pile. **/
    public Pile probationPile;
    /** The player's credit-hour pile. **/
    public Pile creditHourPile;
    /** The player's exception pile. **/
    public Pile exceptionPile;
    /** The player's currently held cards. **/
    public Pile hand;
    
    /**
    * Generic constructor which instantiates non-subclass specific values.
    * @param startingCreditHours Number of credit hours to start with.
    **/
    protected Player(int startingCreditHours) {
        creditHours = startingCreditHours;
        score = startingCreditHours * CREDITHOUR_VALUE;
        activeSetback = SB_NONE;
        ogreProf = false;
        probation = false;
        exceptions = 0x00;
        terms = 0;
        controlPile = new Pile();
        probationPile = new Pile();
        creditHourPile = new Pile();
        exceptionPile = new Pile();
        hand = new Pile();
    }
    
    /**
    * Returns the player's name.
    * @return The player's name.
    **/
    public String getName() {
        return name;
    }
    
    /**
    * Returns the player's credit-hours.
    * @return The player's credit-hours.
    **/
    public int getCreditHours() {
        return creditHours;
    }
    
    /**
    * Returns the player's score.
    * @return The player's score.
    **/
    public int getScore() {
        return score;
    }
    
    /**
    * Adds points to the player's score
    * @param add Number of points to add.
    **/
    public void addToScore(int add) {
        score += add;
    }

    /**
    * Returns the player's number of terms (number of credit-hour cards played).
    * @return The player's number of terms.
    **/
    public int getTerms() {
        return terms;
    }
    
    /**
    * Returns whether the player is human.
    * @return true if the player is human, false if AI.
    **/
    public boolean isHuman() {
        return human;
    }
    
    /**
    * Prints the player's hand to stdout with index values to reference.
    **/
    public void listHand() {
        System.out.println("Your hand:");
        Iterator<Card> cardIter = hand.iterator();
        for (int i = 0; cardIter.hasNext(); i++) {
            //System.out.printf("%d: [%s]%n", i, cardIter.next().toString());
        }
        System.out.println();
    }
    
    /**
    * Returns the player's hand.
    * @return The player's hand.
    **/    
    public Pile getHand() {
        return hand;
    }
    
    /**
    * Returns whether the player has the given exception.
    * @param setback exception to check for, given as a constant from {@link Card}
    * @return true if the player has the exception for the setback value given
    **/
    public boolean hasException(int setback) {
        return (exceptions & EX_ARRAY[setback]) != 0x00;
    }
    
    /**
    * Prints the player's state to stdout.
    * This includes name, credit-hours, active setbacks, and their played
    * exceptions.
    **/
    public void printState() {
        //System.out.printf("Name: %s, Credit-Hours: %d, Setback: %s%n", name, creditHours, Card.SETBACK_NAME[activeSetback]);
        //System.out.printf("Probation: %b, Ogre Prof: %b%n", probation, ogreProf);
        //System.out.printf("Exceptions: %s%n",  exceptionPile.listCards());
    }
    
    /**
    * Attempts to play the given card and determines if that play is valid.
    * @param card The card being played.
    * @param opponent The card player's opponent.
    * @return True if the play is valid.
    * @throws InvalidPlayException if the player can't play the card for any reason
    **/
    public boolean play(Card card, Player opponent) throws InvalidPlayException {
        //try {
            switch (card.type) {
            case TERM: 
                playTerm(card.attribute);
                break;
            case SETBACK:
                playSetback(card.attribute, opponent);
                break;
            case FIX:
                playFix(card.attribute);
                break;
            case EXCEPTION:
                playException(card.attribute);
                break;
            }
        /*} catch (InvalidPlayException e) {
            if (isHuman())
                System.out.println(e.getMessage());
            return false;
        }*/

        
        return true;
    }
     
    /**
    * Play a term card
    * @param creditHourChange Number of credit-hours to add.
    * @throws InvalidPlayException if the player can't play the card for any reason
    **/
    private void playTerm(int creditHourChange) throws InvalidPlayException {
        if (ogreProf) creditHourChange -= OGREPROF_PENALTY;
        
        if (activeSetback == Card.CHEATING || activeSetback == Card.NOCAR || activeSetback == Card.NOALARM)
            throw new InvalidPlayException("You have a setback preventing you from increasing your credit-hours.");
        if (creditHourChange <= 0)
            throw new InvalidPlayException("The Ogre Prof has prevented you from getting any credit-hours!");
        if (probation && creditHourChange > PROBATION_LIMIT)
            throw new InvalidPlayException("You are on academic probation! Take fewer credit-hours.");
        
        creditHours += creditHourChange;
        addToScore(creditHourChange * CREDITHOUR_VALUE);
        terms++;
        if (isHuman())
            System.out.println("You now have " + creditHours + " credit-hours.");
    }
    
    /**
    * Play a setback card
    * @param setback Integer representation of the setback to incur.
    * @param target The target of the setback.
    * @throws InvalidPlayException if the player can't play the card for any reason
    **/
    private void playSetback(int setback, Player target) throws InvalidPlayException {
        // Check if the target has the relevant exception
        if (target.hasException(setback))
            throw new InvalidPlayException("Your opponent has an exception for that setback.");
        // Cannot play the setback if the target has an active control setback
        // Or for probation and ogre prof, if they are already active
        if (setback != Card.PROBATION && setback != Card.OGREPROF && target.activeSetback != SB_NONE
         || setback == Card.PROBATION && target.probation
         || setback == Card.OGREPROF && target.ogreProf)
            throw new InvalidPlayException("Your opponent already has an active setback.");
        
        if (setback == Card.PROBATION)
            target.probation = true;
        else if (setback == Card.OGREPROF)
            target.ogreProf = true;
        else
            target.activeSetback = setback;
        //System.out.println("Your opponent now has setback " + target.activeSetback + ".");
    }

    /**
    * Play a fix card
    * @param setback Integer representation of the setback to fix.
    * @throws InvalidPlayException if the player can't play the card for any reason
    **/
    private void playFix(int setback) throws InvalidPlayException {
        if (setback == Card.CHEATING && activeSetback != Card.CHEATING
         || setback == Card.NOCAR && activeSetback != Card.NOCAR
         || setback == Card.NOALARM && activeSetback != Card.NOALARM
         || setback == Card.PROBATION && !probation
         || setback == Card.OGREPROF && !ogreProf)
            throw new InvalidPlayException("You cannot fix what is not broken.");
        
        if (setback == Card.PROBATION)
            probation = false;
        else if (setback == Card.OGREPROF)
            ogreProf = false;
        else
            activeSetback = SB_NONE;
        System.out.println("The setback has been fixed.");
    }
    
    /**
    * Play an exception card
    * @param setback Integer representation of the setback to be excepted from.
    **/
    private void playException(int setback) {
        switch (setback) {
            case Card.CHEATING:
                exceptions = exceptions | EX_CHEATING;
                if (activeSetback == Card.CHEATING)
                    activeSetback = SB_NONE;
                break;
            case Card.NOCAR:
                exceptions = exceptions | EX_NOCAR;
                if (activeSetback == Card.NOCAR)
                    activeSetback = SB_NONE;
                break;
            case Card.NOALARM:
                exceptions = exceptions | EX_NOALARM;
                if (activeSetback == Card.NOALARM)
                    activeSetback = SB_NONE;
                break;
            case Card.PROBATION:
                exceptions = exceptions | EX_PROBATION;
                probation = false;  // no need to check if active
                break;
            case Card.OGREPROF:
                exceptions = exceptions | EX_OGREPROF;
                ogreProf = false;   // no need to check if active
                break;
        }
        
        addToScore(EXCEPTION_VALUE);
        if (exceptions == 0x1F) addToScore(EXCEPTION_BONUS);
    }
    
    public abstract int takeTurn(Player opponent);
}
