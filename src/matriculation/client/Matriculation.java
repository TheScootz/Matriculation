package matriculation.client;

import java.util.NoSuchElementException;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Matriculation implements EntryPoint, ClickHandler, KeyDownHandler, KeyUpHandler {
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
    ArrayList<FocusPanel> playerCards = new ArrayList<FocusPanel>(Player.HANDSIZE);
    ArrayList<Label> playerCardLabels = new ArrayList<Label>(Player.HANDSIZE);
    ArrayList<FocusPanel> opponentCards = new ArrayList<FocusPanel>(Player.HANDSIZE);
    ArrayList<Label> opponentCardLabels = new ArrayList<Label>(Player.HANDSIZE);
    //Button cardButton = new Button("", this);
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
    Label statusMessage = new Label();
    
    FlowPanel cheatPanel = new FlowPanel();
    CheckBox showOpponentCardsCheckBox = new CheckBox("Show opponent cards");
    HorizontalPanel endAfterPanel = new HorizontalPanel();
    TextBox endAfterTextBox = new TextBox();
    Label endAfterLabel = new Label("End after this many turns (takes effect at end of round)");
    
    // The game elements themselves
    /** Determines whether the other player's cards should be printed. False by default **/
    public boolean showOpponentCards;
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
    private boolean waitingForPlayer;
    private boolean gameActive;
    Logger logger = Logger.getLogger("");
    
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
        waitingForPlayer = false;
        gameActive = true;
        
        for (int i = 0; i < Player.HANDSIZE; i++) {
            human.hand.add(stock.draw());
            ai.hand.add(stock.draw());
        }
        
        updateDisplay();
        status.remove(gameStartButton);
        putStatus("Game start!", true);
        status.add(statusMessage);
        
        startRound();
    }
    
    private void startRound() {
        // Make sure player has cards
        if (human.hand.size() > 0) {
            waitingForPlayer = true;
            logger.log(Level.SEVERE, "We are waiting for the player!");
        }
    }
    
    /**
    * Plays one round of the game.
    * Human player plays first, followed by the AI.
    * After each turn, checks if a game-ending condition is met and ends the game if so.
    * @see HumanPlayer#takeTurn(Player)
    * @see AIPlayer#takeTurn(Player)
    **/
    public void endRound() {
        // Check for winner
        if (human.getCreditHours() >= Player.MAX_CREDITHOURS)
            endGame(human, true);
        
        // Draw if there are still cards in the deck
        try {
            human.hand.add(stock.draw());
        } catch (NoSuchElementException e) {}
        updateDisplay();
        
        // Make sure they have cards
        if (ai.hand.size() > 0) {
            int aiPlayed = ai.takeTurn(human);
            if (aiPlayed >= 0) {
                putStatus("AI played " + ai.hand.get(aiPlayed).toString(), true);
                ai.hand.remove(aiPlayed);
            }
            updateDisplay();
        }
        
        // Check for winner
        if (ai.getCreditHours() >= Player.MAX_CREDITHOURS)
            endGame(ai, true);
        
        // Draw if there are still cards in the deck
        try {
            ai.hand.add(stock.draw());
        } catch (NoSuchElementException e) {}
        updateDisplay();
        
        // End game if max turns is reached or if there are no cards anywhere
        turns++;
        if (endAfter > 0 && turns == endAfter)
            endGame(human, false);
        else if (stock.size() == 0 && ai.hand.size() == 0 && human.hand.size() == 0)
            endGame(human, false);
        else
            startRound();
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
            logger.log(Level.SEVERE, "There are no more cards. Game over!\n");
        
        // Declare a winner
        if (human.getScore() > ai.getScore())
            logger.log(Level.SEVERE, human.getName() + " wins!");
        else if (human.getScore() < ai.getScore())
            logger.log(Level.SEVERE, ai.getName() + " wins!");
        else
            logger.log(Level.SEVERE, "There was a tie! Everyone wins :)");
    }
    
    public void onClick(ClickEvent event) {
        if (event.getSource() == gameStartButton)
            startGame();
        else if (event.getSource() == showOpponentCardsCheckBox) {
            showOpponentCards = showOpponentCardsCheckBox.getValue();
            updateDisplay();
        }
        else if (playerCards.contains(event.getSource())
            && !((FocusPanel)event.getSource()).getStyleName().contains("emptyCard")
            && waitingForPlayer) {   // if it's a player card, make sure it's not empty
            int i = playerCards.indexOf(event.getSource());
            try {
                human.play(human.hand.get(i), ai);
                putStatus("You played " + human.hand.get(i).toString(), true);
                human.hand.remove(i);
                waitingForPlayer = false;
                endRound();
            } catch (InvalidPlayException e) {
                putStatus(e.getMessage(), false);
            }
        }
    }
    
    public void onKeyDown(KeyDownEvent event) {
        if (event.getSource() == gameStartButton && event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            startGame();
        else if (event.getSource() == opponentCards.get(4) && event.getNativeKeyCode() == KeyCodes.KEY_C)
            RootPanel.get().add(cheatPanel);
        else if (playerCards.contains(event.getSource())
            && !((FocusPanel)event.getSource()).getStyleName().contains("emptyCard")
            && waitingForPlayer
            && event.getNativeKeyCode() == KeyCodes.KEY_D) {   // if it's a player card, make sure it's not empty
            human.hand.remove(playerCards.indexOf(event.getSource()));
            waitingForPlayer = false;
            endRound();
        }
    }
    
    public void onKeyUp(KeyUpEvent event) {
        if (event.getSource() == endAfterTextBox) {
            // update as textbox is updated
            try {
                endAfter = Integer.parseInt(endAfterTextBox.getValue());
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }
    
    private void updateDisplay() {
        // PLAYER CARDS
        Pile hand = human.getHand();
        for (int i = 0; i < Player.HANDSIZE; ++i) {
            if (i < hand.size()) {
                playerCardLabels.get(i).setText(hand.get(i).toString());
                playerCards.get(i).setStyleName("card");
            }
            else {
                playerCardLabels.get(i).setText("");
                playerCards.get(i).setStyleName("card emptyCard");
            }
        }
        
        // OPPONENT CARDS
        hand = ai.getHand();
        for (int i = 0; i < Player.HANDSIZE; ++i) {
            if (i < hand.size()) {
                if (showOpponentCards) {
                    opponentCardLabels.get(i).setText(hand.get(i).toString());
                    opponentCards.get(i).setStyleName("card");
                }
                else {
                    opponentCardLabels.get(i).setText("");
                    opponentCards.get(i).setStyleName("card cardBack");
                }
            }
            else {
                opponentCards.get(i).setStyleName("card emptyCard");
            }
        }
        
        // DECK
        countRemaining.setText("Remaining: " + stock.size());
        
        // SCORES
        playerCHTotal.setText("Credit-Hours: " + human.getCreditHours());
        playerScore.setText("Score: " + human.getScore());
        opponentCHTotal.setText("Credit-Hours: " + ai.getCreditHours());
        opponentScore.setText("Score: " + ai.getScore());
    }
    
    private void putStatus(String message, boolean good) {
        statusMessage.setText(message);
        if (good) statusMessage.setStyleName("goodMessage");
        else statusMessage.setStyleName("badMessage");
    }
    
    public void onModuleLoad() {
        showOpponentCards = false;
        endAfter = 0;
        startingCreditHours = 0;
        turns = 0;   
        gameActive = false;
        
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
            playerCards.add(new FocusPanel());
            playerCardLabels.add(new Label());
            playerCards.get(i).add(playerCardLabels.get(i));
            playerCards.get(i).addStyleName("card emptyCard");
            playerCardArea.add(playerCards.get(i));
            playerCards.get(i).addClickHandler(this);
            playerCards.get(i).addKeyDownHandler(this);
        }
        //cardButton.addStyleName("cardButton");
        
        for (i = 0; i < Player.HANDSIZE; ++i) {
            opponentCards.add(new FocusPanel());
            opponentCardLabels.add(new Label());
            opponentCards.get(i).add(opponentCardLabels.get(i));
            opponentCards.get(i).addStyleName("card emptyCard");
            opponentCardArea.add(opponentCards.get(i));
            // super secret cheat menu accessed by pressing c on the opponent's 5th card
            if (i == 4)
                opponentCards.get(i).addKeyDownHandler(this);
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
        
        // Set up cheat panel
        cheatPanel.add(showOpponentCardsCheckBox);
        cheatPanel.addStyleName("statusRow");
        showOpponentCardsCheckBox.addClickHandler(this);
        endAfterPanel.add(endAfterTextBox);
        endAfterPanel.add(endAfterLabel);
        endAfterTextBox.addKeyUpHandler(this);
        endAfterTextBox.setVisibleLength(2);
        cheatPanel.add(endAfterPanel);
    }
}
