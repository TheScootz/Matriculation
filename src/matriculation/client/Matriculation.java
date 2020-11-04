package matriculation.client;

import java.util.NoSuchElementException;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Matriculation implements EntryPoint, ClickHandler, KeyDownHandler {
    // Create the various panels and widgets we will be using.
    
    //// Main panel that will have the dynamic contents inside of it
    FlowPanel gameArea = new FlowPanel();
    
    //// Create the 3 play areas: player's, opponent's, and neutral
    FlowPanel playerArea = new FlowPanel();
    HorizontalPanel drawDiscard = new HorizontalPanel();
    FlowPanel opponentArea = new FlowPanel();
    
    //// Player and opponent areas and card slots
    HorizontalPanel playerCardArea = new HorizontalPanel();
    HorizontalPanel playerPileArea = new HorizontalPanel();
    HorizontalPanel opponentCardArea = new HorizontalPanel();
    HorizontalPanel opponentPileArea = new HorizontalPanel();
    FlowPanel[] playerCards = new FlowPanel[Player.HANDSIZE];
    FlowPanel[] opponentCards = new FlowPanel[Player.HANDSIZE];
    //// Player and opponent piles
    FlowPanel playerControlWrapper = new FlowPanel();
    FlowPanel playerControl = new FlowPanel();
    FlowPanel opponentControlWrapper = new FlowPanel();
    FlowPanel opponentControl = new FlowPanel();
    FlowPanel playerCreditHourWrapper = new FlowPanel();
    FlowPanel playerCreditHour = new FlowPanel();
    FlowPanel opponentCreditHourWrapper = new FlowPanel();
    FlowPanel opponentCreditHour = new FlowPanel();
    FlowPanel playerProbationWrapper = new FlowPanel();
    FlowPanel playerProbation = new FlowPanel();
    FlowPanel opponentProbationWrapper = new FlowPanel();
    FlowPanel opponentProbation = new FlowPanel();
    FlowPanel playerExceptionWrapper = new FlowPanel();
    FlowPanel playerException = new FlowPanel();
    FlowPanel opponentExceptionWrapper = new FlowPanel();
    FlowPanel opponentException = new FlowPanel();
    
    //// Deck and discard pile
    FlowPanel deckWrapper = new FlowPanel();
    FlowPanel deck = new FlowPanel();
    FlowPanel discard = new FlowPanel();
    Label countRemaining = new Label("Remaining: 0");
    
    //// Player info
    FlowPanel playerInfo = new FlowPanel();
    Label playerName = new Label("YOU");
    Label playerCHTotal = new Label("Credit-Hours: 0");
    Label playerScore = new Label("Score: 0");
    FlowPanel opponentInfo = new FlowPanel();
    Label opponentName = new Label("AI");
    Label opponentCHTotal = new Label("Credit-Hours: 0");
    Label opponentScore = new Label("Score: 0");
    
    FlowPanel status = new FlowPanel();
    Button gameStartButton = new Button("Start");
    
    
    // The game elements themselves
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
    public Deck stock;
    /** The human player. **/
    public HumanPlayer human;
    /** The AI player. **/
    public AIPlayer ai;
    /** Number of turns that have passed so far. **/
    public int turns;
    
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
        
        updateOpponentCardDisplay();
        updatePlayerCardDisplay();
        updateDeckCount();
        status.remove(gameStartButton);
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
        //System.out.printf("SCORES: %5s %5s%n", human.getName(), ai.getName());
        //System.out.printf("        %5d %5d%n%n%n", human.getScore(), ai.getScore());
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
            //System.out.printf("%s has reached %d credit-hours and graduated. Game over!%n%n", ender.name, Player.MAX_CREDITHOURS);
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
        //System.exit(0);
    }
    
    public void onModuleLoad() {
        showAICards = false;
        endAfter = 0;
        startingCreditHours = 0;
        turns = 0;        
        
        // Set up players' card and pile areas
        playerCardArea.addStyleName("gameRow");
        playerPileArea.addStyleName("gameRow");
        playerArea.add(playerPileArea);
        playerArea.add(playerCardArea);
        
        opponentCardArea.addStyleName("gameRow");
        opponentPileArea.addStyleName("gameRow");
        opponentArea.add(opponentCardArea);    // mirrored from player's perspective
        opponentArea.add(opponentPileArea);
        
        
        // Set up card slots
        int i;
        for (i = 0; i < Player.HANDSIZE; ++i) {
            playerCards[i] = new FlowPanel();
            playerCards[i].add(new HTML(""));   // needed to make the element show up
            playerCards[i].addStyleName("card");
            playerCards[i].addStyleName("emptyCard");
            playerCardArea.add(playerCards[i]);
        }
        
        for (i = 0; i < Player.HANDSIZE; ++i) {
            opponentCards[i] = new FlowPanel();
            opponentCards[i].add(new HTML(""));
            opponentCards[i].addStyleName("card");
            opponentCards[i].addStyleName("emptyCard");
            opponentCardArea.add(opponentCards[i]);
        }
        
        
        // Set up piles
        //// CONTROL
        playerControl.add(new HTML(""));
        playerControl.addStyleName("card emptyCard");
        playerControlWrapper.add(new Label("Control"));
        playerControlWrapper.add(playerControl);
        playerControlWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerControlWrapper);
        
        opponentControl.add(new HTML(""));
        opponentControl.addStyleName("card emptyCard");
        opponentControlWrapper.add(new Label("Control"));
        opponentControlWrapper.add(opponentControl);
        opponentControlWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentControlWrapper);
        
        //// CREDIT-HOURS
        playerCreditHour.add(new HTML(""));
        playerCreditHour.addStyleName("card emptyCard");
        playerCreditHourWrapper.add(new Label("Credit-Hours"));
        playerCreditHourWrapper.add(playerCreditHour);
        playerCreditHourWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerCreditHourWrapper);
        
        opponentCreditHour.add(new HTML(""));
        opponentCreditHour.addStyleName("card emptyCard");
        opponentCreditHourWrapper.add(new Label("Credit-Hours"));
        opponentCreditHourWrapper.add(opponentCreditHour);
        opponentCreditHourWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentCreditHourWrapper);
        
        //// PROBATION
        playerProbation.add(new HTML(""));
        playerProbation.addStyleName("card emptyCard");
        playerProbationWrapper.add(new Label("Probation"));
        playerProbationWrapper.add(playerProbation);
        playerProbationWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerProbationWrapper);
        
        opponentProbation.add(new HTML(""));
        opponentProbation.addStyleName("card emptyCard");
        opponentProbationWrapper.add(new Label("Probation"));
        opponentProbationWrapper.add(opponentProbation);
        opponentProbationWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentProbationWrapper);
        
        //// EXCEPTIONS
        playerException.add(new HTML(""));
        playerException.addStyleName("card emptyCard");
        playerExceptionWrapper.add(new Label("Exceptions"));
        playerExceptionWrapper.add(playerException);
        playerExceptionWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerExceptionWrapper);
        
        opponentException.add(new HTML(""));
        opponentException.addStyleName("card emptyCard");
        opponentExceptionWrapper.add(new Label("Exceptions"));
        opponentExceptionWrapper.add(opponentException);
        opponentExceptionWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentExceptionWrapper);
        
        
        // Set up draw and discard piles
        deck.addStyleName("card cardBack");
        deckWrapper.add(deck);
        deckWrapper.add(countRemaining);
        deckWrapper.addStyleName("cardWrapper");
        discard.addStyleName("card emptyCard");
        
        // Set up player info
        playerName.addStyleName("playerName");
        playerInfo.add(playerName);
        playerInfo.add(playerCHTotal);
        playerInfo.add(playerScore);
        playerInfo.addStyleName("playerInfo");
        opponentName.addStyleName("playerName");
        opponentInfo.add(opponentName);
        opponentInfo.add(opponentCHTotal);
        opponentInfo.add(opponentScore);
        opponentInfo.addStyleName("playerInfo");
        
        drawDiscard.add(playerInfo);
        drawDiscard.add(deckWrapper);
        drawDiscard.add(discard);
        drawDiscard.add(opponentInfo);
        drawDiscard.addStyleName("gameRow");
        
        // Set up the start button
        gameStartButton.addClickHandler(this);
        gameStartButton.addKeyDownHandler(this);
        
        status.add(gameStartButton);
        status.addStyleName("statusRow");
        
        // Add the panels to the page
        gameArea.add(opponentArea);
        gameArea.add(drawDiscard);
        gameArea.add(playerArea);
        gameArea.add(status);
        RootPanel.get().add(gameArea);
    }
    
    public void onClick(ClickEvent event) {
        startGame();
    }
    
    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            startGame();
        }
    }
    
    private void updateOpponentCardDisplay() {
        int handSize = ai.hand.size();
        for (int i = 0; i < Player.HANDSIZE; ++i) {
            if (i < handSize) {
                opponentCards[i].setStyleName("card cardBack");
            }
            else {
                opponentCards[i].setStyleName("card emptyCard");
            }
        }
    }
    
    private void updateDeckCount() {
        countRemaining.setText("Remaining: " + stock.size());
    }
    
    private void updatePlayerCardDisplay() {
        Pile hand = human.getHand();
        for (int i = 0; i < Player.HANDSIZE; ++i) {
            if (i < hand.size()) {
                playerCards[i].setStyleName("card");
                playerCards[i].add(new Label(hand.get(i).toString()));
            }
            else {
                playerCards[i].setStyleName("card emptyCard");
            }
        }
    }
}
