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
import com.google.gwt.user.client.ui.DialogBox;
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
    FocusPanel playerControl = new FocusPanel();
    Label playerControlLabel = new Label();
    FlowPanel opponentControlWrapper = new FlowPanel();
    FocusPanel opponentControl = new FocusPanel();
    Label opponentControlLabel = new Label();
    FlowPanel playerCreditHourWrapper = new FlowPanel();
    FocusPanel playerCreditHour = new FocusPanel();
    Label playerCreditHourLabel = new Label();
    FlowPanel opponentCreditHourWrapper = new FlowPanel();
    FocusPanel opponentCreditHour = new FocusPanel();
    Label opponentCreditHourLabel = new Label();
    FlowPanel playerProbationWrapper = new FlowPanel();
    FocusPanel playerProbation = new FocusPanel();
    Label playerProbationLabel = new Label();
    FlowPanel opponentProbationWrapper = new FlowPanel();
    FocusPanel opponentProbation = new FocusPanel();
    Label opponentProbationLabel = new Label();
    FlowPanel playerExceptionWrapper = new FlowPanel();
    FocusPanel playerException = new FocusPanel();
    Label playerExceptionLabel = new Label();
    FlowPanel opponentExceptionWrapper = new FlowPanel();
    FocusPanel opponentException = new FocusPanel();
    Label opponentExceptionLabel = new Label();
    DialogBox pileDialog = new DialogBox(true);
    HTML pileDialogContents = new HTML();
    
    //// Deck and discard pile
    FlowPanel deckWrapper = new FlowPanel();
    FlowPanel deck = new FlowPanel();
    FlowPanel discardPanel = new FlowPanel();
    Label discardLabel = new Label();
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
    Button gameStartButton = new Button("Start New Game");
    Label statusMessage = new Label();
    
    FlowPanel cheatPanel = new FlowPanel();
    CheckBox showOpponentCardsCheckBox = new CheckBox("Show opponent cards");
    HorizontalPanel endAfterPanel = new HorizontalPanel();
    TextBox endAfterTextBox = new TextBox();
    Label endAfterLabel = new Label("End after this many turns (takes effect at end of round)");
    HorizontalPanel startingCreditHoursPanel = new HorizontalPanel();
    TextBox startingCreditHoursTextBox = new TextBox();
    Label startingCreditHoursLabel = new Label("Start with this many credit hours (takes effect at game start)");
    
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
    
    
    /** True when there is currently a game being played. **/
    private boolean gameActive;
    /** The deck from which cards are drawn. **/
    private Deck stock;
    /** The most recently discarded card. **/
    private Card discard;
    /** The human player. **/
    private HumanPlayer human;
    /** The AI player. **/
    private AIPlayer ai;
    /** Number of turns that have passed so far. **/
    private int turns;
    /** True when the game is waiting for the local player to take an action. **/
    private boolean waitingForPlayer;
    /** Logger to write to the JavaScript console. **/
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
        discard = null;
        
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
        if (human.getCreditHours() >= Player.MAX_CREDITHOURS) {
            endGame(human, true);
            updateDisplay();
            return;
        }
        
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
                addToPile(ai.hand.get(aiPlayed), ai, human);
                ai.hand.remove(aiPlayed);
            }
            else {
                int discardedIndex = -aiPlayed - 1;
                putStatus("AI discarded.", true);
                discard = ai.hand.get(discardedIndex);
                ai.hand.remove(discardedIndex);
            }
            updateDisplay();
        }
        
        // Check for winner
        if (ai.getCreditHours() >= Player.MAX_CREDITHOURS) {
            endGame(ai, true);
            updateDisplay();
            return;
        }
        
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
        
        if (gameActive)
            startRound();
    }
    
    /**
    * Ends the game and determines the winner.
    * Currently exits the program when complete.
    * @param ender The player who ended the game. Unused if neither player graduated.
    * @param graduated True if the game ended via a player graduating.
    **/
    public void endGame(Player ender, boolean graduated) {
        gameActive = false;
        
        // Display who graduated
        if (graduated) {
            //System.out.printf("%s has reached %d credit-hours and graduated. Game over!%n%n", ender.name, Player.MAX_CREDITHOURS);
            ender.addToScore(Player.GRADUATE_VALUE);
            if (ender.getTerms() <= Player.GRADUATE_BONUS_TERMS)
                ender.addToScore(Player.GRADUATE_BONUS);
        }
        // Otherwise no one graduated
        else 
            putStatus("There are no more cards. Game over!", true);
        
        // Declare a winner
        if (human.getScore() > ai.getScore())
            putStatus(human.getName() + " wins!", true);
        else if (human.getScore() < ai.getScore())
            putStatus(ai.getName() + " wins!", false);
        else
            putStatus("There was a tie! Everyone wins :)", true);
        
        status.add(gameStartButton);
    }
    
    public void addToPile(Card card, Player player, Player opponent) {
        if (card.type == Card.Type.TERM)
            player.creditHourPile.add(card);
        else if (card.type == Card.Type.SETBACK)
            if (card.attribute == Card.OGREPROF)
                opponent.creditHourPile.add(card);
            else if (card.attribute == Card.PROBATION)
                opponent.probationPile.add(card);
            else
                opponent.controlPile.add(card);
        else if (card.type == Card.Type.FIX)
            if (card.attribute == Card.OGREPROF)
                player.creditHourPile.add(card);
            else if (card.attribute == Card.PROBATION)
                player.probationPile.add(card);
            else
                player.controlPile.add(card);
        else if (card.type == Card.Type.EXCEPTION)
            player.exceptionPile.add(card);
    }
    
    
    public void onClick(ClickEvent event) {
        if (event.getSource() == gameStartButton)
            startGame();
        else if (event.getSource() == showOpponentCardsCheckBox) {
            showOpponentCards = showOpponentCardsCheckBox.getValue();
            updateDisplay();
        }
        // if they clicked on a pile
        else if (event.getSource() == playerControl)
            showPileContents(human.controlPile, human, "Control");
        else if (event.getSource() == opponentControl)
            showPileContents(ai.controlPile, ai, "Control");
        else if (event.getSource() == playerCreditHour)
            showPileContents(human.creditHourPile, human, "Credit-Hour");
        else if (event.getSource() == opponentCreditHour)
            showPileContents(ai.creditHourPile, ai, "Credit-Hour");
        else if (event.getSource() == playerProbation)
            showPileContents(human.probationPile, human, "Probation");
        else if (event.getSource() == opponentProbation)
            showPileContents(ai.probationPile, ai, "Probation");
        else if (event.getSource() == playerException)
            showPileContents(human.exceptionPile, human, "Exception");
        else if (event.getSource() == opponentException)
            showPileContents(ai.exceptionPile, ai, "Exception");
        else if (playerCards.contains(event.getSource())
            && !((FocusPanel)event.getSource()).getStyleName().contains("emptyCard")
            && waitingForPlayer) {   // if it's a player card, make sure it's not empty
            int i = playerCards.indexOf(event.getSource());
            try {
                human.play(human.hand.get(i), ai);
                putStatus("You played " + human.hand.get(i).toString(), true);
                addToPile(human.hand.get(i), human, ai);
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
            int i = playerCards.indexOf(event.getSource());
            putStatus("You discarded.", true);
            discard = human.hand.get(i);
            human.hand.remove(i);
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
        else if (event.getSource() == startingCreditHoursTextBox) {
            // update as textbox is updated
            try {
                startingCreditHours = Integer.parseInt(startingCreditHoursTextBox.getValue());
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
                playerCards.get(i).addStyleName(hand.get(i).style());
            }
            else {
                playerCardLabels.get(i).setText("");
                playerCards.get(i).setStyleName("card emptyCard");
            }
        }
        
        // PLAYER PILES
        updatePile(human.controlPile, playerControl, playerControlLabel);
        updatePile(human.creditHourPile, playerCreditHour, playerCreditHourLabel);
        updatePile(human.probationPile, playerProbation, playerProbationLabel);
        updatePile(human.exceptionPile, playerException, playerExceptionLabel);
        
        // OPPONENT CARDS
        hand = ai.getHand();
        for (int i = 0; i < Player.HANDSIZE; ++i) {
            if (i < hand.size()) {
                if (showOpponentCards) {
                    opponentCardLabels.get(i).setText(hand.get(i).toString());
                    opponentCards.get(i).setStyleName("card");
                    opponentCards.get(i).addStyleName(hand.get(i).style());
                }
                else {
                    opponentCardLabels.get(i).setText("");
                    opponentCards.get(i).setStyleName("card cardBack");
                }
            }
            else {
                opponentCardLabels.get(i).setText("");
                opponentCards.get(i).setStyleName("card emptyCard");
            }
        }
        
        // OPPONENT PILES
        updatePile(ai.controlPile, opponentControl, opponentControlLabel);
        updatePile(ai.creditHourPile, opponentCreditHour, opponentCreditHourLabel);
        updatePile(ai.probationPile, opponentProbation, opponentProbationLabel);
        updatePile(ai.exceptionPile, opponentException, opponentExceptionLabel);
        
        // DECK
        countRemaining.setText("Remaining: " + stock.size());
        
        // DISCARD
        if (discard != null) {
            discardLabel.setText(discard.toString());
            discardPanel.setStyleName("card");
            discardPanel.addStyleName(discard.style());
        }
        else {
            discardLabel.setText("");
            discardPanel.setStyleName("card emptyCard");
        }
        
        // SCORES
        playerCHTotal.setText("Credit-Hours: " + human.getCreditHours());
        playerScore.setText("Score: " + human.getScore());
        opponentCHTotal.setText("Credit-Hours: " + ai.getCreditHours());
        opponentScore.setText("Score: " + ai.getScore());
    }
    
    public void updatePile(Pile pile, FocusPanel pilePanel, Label pileLabel) {
        Card top = pile.top();
        if (top == null) {
            pileLabel.setText("");
            pilePanel.setStyleName("card emptyCard");
        }
        else {
            pileLabel.setText(top.toString());
            pilePanel.setStyleName("card");
            pilePanel.addStyleName(top.style());
        }
    }
    
    public void showPileContents(Pile pile, Player owner, String name) {
        if (pile.size() == 0) return;
        
        pileDialog.setText("Contents of " + owner.getName() + "'s " + name + " Pile");
        pileDialogContents.setHTML(pile.listCardsHTML());
        pileDialog.show();
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
        playerControl.add(playerControlLabel);
        playerControl.addStyleName("card emptyCard");
        playerControl.addClickHandler(this);
        playerControlWrapper.add(new Label("Control"));
        playerControlWrapper.add(playerControl);
        playerControlWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerControlWrapper);
        
        opponentControl.add(opponentControlLabel);
        opponentControl.addStyleName("card emptyCard");
        opponentControl.addClickHandler(this);
        opponentControlWrapper.add(new Label("Control"));
        opponentControlWrapper.add(opponentControl);
        opponentControlWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentControlWrapper);
        
        //// CREDIT-HOURS
        playerCreditHour.add(playerCreditHourLabel);
        playerCreditHour.addStyleName("card emptyCard");
        playerCreditHour.addClickHandler(this);
        playerCreditHourWrapper.add(new Label("Credit-Hours"));
        playerCreditHourWrapper.add(playerCreditHour);
        playerCreditHourWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerCreditHourWrapper);
        
        opponentCreditHour.add(opponentCreditHourLabel);
        opponentCreditHour.addStyleName("card emptyCard");
        opponentCreditHour.addClickHandler(this);
        opponentCreditHourWrapper.add(new Label("Credit-Hours"));
        opponentCreditHourWrapper.add(opponentCreditHour);
        opponentCreditHourWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentCreditHourWrapper);
        
        //// PROBATION
        playerProbation.add(playerProbationLabel);
        playerProbation.addStyleName("card emptyCard");
        playerProbation.addClickHandler(this);
        playerProbationWrapper.add(new Label("Probation"));
        playerProbationWrapper.add(playerProbation);
        playerProbationWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerProbationWrapper);
        
        opponentProbation.add(opponentProbationLabel);
        opponentProbation.addStyleName("card emptyCard");
        opponentProbation.addClickHandler(this);
        opponentProbationWrapper.add(new Label("Probation"));
        opponentProbationWrapper.add(opponentProbation);
        opponentProbationWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentProbationWrapper);
        
        //// EXCEPTIONS
        playerException.add(playerExceptionLabel);
        playerException.addStyleName("card emptyCard");
        playerException.addClickHandler(this);
        playerExceptionWrapper.add(new Label("Exceptions"));
        playerExceptionWrapper.add(playerException);
        playerExceptionWrapper.addStyleName("cardWrapper");
        playerPileArea.add(playerExceptionWrapper);
        
        opponentException.add(opponentExceptionLabel);
        opponentException.addStyleName("card emptyCard");
        opponentException.addClickHandler(this);
        opponentExceptionWrapper.add(new Label("Exceptions"));
        opponentExceptionWrapper.add(opponentException);
        opponentExceptionWrapper.addStyleName("cardWrapper");
        opponentPileArea.add(opponentExceptionWrapper);
        
        
        // Set up draw and discardPanel piles
        deck.addStyleName("card cardBack");
        deckWrapper.add(deck);
        deckWrapper.add(countRemaining);
        deckWrapper.addStyleName("cardWrapper");
        discardPanel.add(discardLabel);
        discardPanel.addStyleName("card emptyCard");
        
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
        drawDiscard.add(discardPanel);
        drawDiscard.add(opponentInfo);
        drawDiscard.addStyleName("gameRow");
        
        // Set up the start button
        gameStartButton.addClickHandler(this);
        gameStartButton.addKeyDownHandler(this);
        status.add(gameStartButton);
        status.addStyleName("statusRow");
        
        // Set up dialog box showing pile contents
        pileDialog.add(pileDialogContents);
        pileDialog.hide(true);
        
        // Add the panels to the page
        gameArea.add(opponentArea);
        gameArea.add(drawDiscard);
        gameArea.add(playerArea);
        gameArea.add(status);
        gameArea.add(pileDialog);
        RootPanel.get().add(gameArea);
        
        
        // Set up cheat panel
        cheatPanel.add(showOpponentCardsCheckBox);
        cheatPanel.addStyleName("statusRow");
        showOpponentCardsCheckBox.addClickHandler(this);
        endAfterTextBox.setValue("0");
        endAfterTextBox.addKeyUpHandler(this);
        endAfterTextBox.setVisibleLength(2);
        endAfterPanel.add(endAfterTextBox);
        endAfterPanel.add(endAfterLabel);
        endAfterPanel.addStyleName("center");
        cheatPanel.add(endAfterPanel);
        startingCreditHoursTextBox.setValue("0");
        startingCreditHoursTextBox.addKeyUpHandler(this);
        startingCreditHoursTextBox.setVisibleLength(3);
        startingCreditHoursPanel.add(startingCreditHoursTextBox);
        startingCreditHoursPanel.add(startingCreditHoursLabel);
        startingCreditHoursPanel.addStyleName("center");
        cheatPanel.add(startingCreditHoursPanel);
    }
}
