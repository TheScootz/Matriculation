package matriculation.client;

import matriculation.shared.*;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
/**
* Controller and UI class for the game.
* This class handles actual game flow, making the players take their turns,
* creating and updating the UI, deciding when the game is over, and determining
* the winner.
* <p>NB: I tried to make the controller and the UI separate classes and got an error I couldn't trace because the browser stack traces are useless</p>
*
* @author Emre Shively
* @version 1.1.0
**/
public class MatriculationClient implements EntryPoint, ClickHandler, KeyDownHandler, KeyUpHandler, ValueChangeHandler<Boolean> {
    // Create the various panels and widgets we will be using.
    
    /** Main panel that will have the dynamic contents inside of it **/
    FlowPanel gameArea = new FlowPanel();
    
    
    //// Create the 3 play areas: player's, opponent's, and neutral
    /** Player's play area **/
    FlowPanel playerArea = new FlowPanel();
    /** Deck, discard pile, and players' information **/
    HorizontalPanel drawDiscard = new HorizontalPanel();
    /** Opponent's play area **/
    FlowPanel opponentArea = new FlowPanel();
    
    
    //// Player and opponent areas and card slots
    /** Panel for player's hand **/
    HorizontalPanel playerCardArea = new HorizontalPanel();
    /** Panel for player's piles **/
    HorizontalPanel playerPileArea = new HorizontalPanel();
    /** Panel for opponent's hand **/
    HorizontalPanel opponentCardArea = new HorizontalPanel();
    /** Panel for opponent's piles **/
    HorizontalPanel opponentPileArea = new HorizontalPanel();
    /** Individual player card panels **/
    ArrayList<FocusPanel> playerCards = new ArrayList<FocusPanel>(Player.HANDSIZE);
    /** Labels for player cards **/
    ArrayList<Label> playerCardLabels = new ArrayList<Label>(Player.HANDSIZE);
    /** Individual opponent card panels **/
    ArrayList<FocusPanel> opponentCards = new ArrayList<FocusPanel>(Player.HANDSIZE);
    /** Labels for opponent cards **/
    ArrayList<Label> opponentCardLabels = new ArrayList<Label>(Player.HANDSIZE);
    
    
    //// Player and opponent piles
    /** Wrapper panel for player control pile **/
    FlowPanel playerControlWrapper = new FlowPanel();
    /** Panel for player control pile **/
    FocusPanel playerControl = new FocusPanel();
    /** Label for player control pile **/
    Label playerControlLabel = new Label();
    /** Wrapper panel for opponent control pile **/
    FlowPanel opponentControlWrapper = new FlowPanel();
    /** Panel for opponent control pile **/
    FocusPanel opponentControl = new FocusPanel();
    /** Label for opponent control pile **/
    Label opponentControlLabel = new Label();
    
    /** Wrapper panel for player credit-hour pile **/
    FlowPanel playerCreditHourWrapper = new FlowPanel();
    /** Panel for player credit-hour pile **/
    FocusPanel playerCreditHour = new FocusPanel();
    /** Label for player credit-hour pile **/
    Label playerCreditHourLabel = new Label();
    /** Wrapper panel for opponent credit-hour pile **/
    FlowPanel opponentCreditHourWrapper = new FlowPanel();
    /** Panel for opponent credit-hour pile **/
    FocusPanel opponentCreditHour = new FocusPanel();
    /** Label for opponent credit-hour pile **/
    Label opponentCreditHourLabel = new Label();
    
    /** Wrapper panel for player probation pile **/
    FlowPanel playerProbationWrapper = new FlowPanel();
    /** Panel for player probation pile **/
    FocusPanel playerProbation = new FocusPanel();
    /** Label for player probation pile **/
    Label playerProbationLabel = new Label();
    /** Wrapper panel for opponent probation pile **/
    FlowPanel opponentProbationWrapper = new FlowPanel();
    /** Panel for opponent probation pile **/
    FocusPanel opponentProbation = new FocusPanel();
    /** Label for opponent probation pile **/
    Label opponentProbationLabel = new Label();
    
    /** Wrapper panel for player exception pile **/
    FlowPanel playerExceptionWrapper = new FlowPanel();
    /** Panel for player exception pile **/
    FocusPanel playerException = new FocusPanel();
    /** Label for player exception pile **/
    Label playerExceptionLabel = new Label();
    /** Wrapper panel for opponent exception pile **/
    FlowPanel opponentExceptionWrapper = new FlowPanel();
    /** Panel for opponent exception pile **/
    FocusPanel opponentException = new FocusPanel();
    /** Label for opponent exception pile **/
    Label opponentExceptionLabel = new Label();
    
    /** Dialog box for displaying the contents of piles **/
    DialogBox pileDialog = new DialogBox(true);
    /** The HTML contents of {@link #pileDialog} **/
    HTML pileDialogContents = new HTML();
    
    //// Deck and discard pile
    /** Wrapper panel for the deck **/
    FlowPanel deckWrapper = new FlowPanel();
    /** Panel for the deck card **/
    FlowPanel deck = new FlowPanel();
    /** Panel for the discard pile **/
    FlowPanel discardPanel = new FlowPanel();
    /** Label for the discard pile **/
    Label discardLabel = new Label();
    /** Label showing how many cards are remainign in the deck **/
    Label countRemaining = new Label("Remaining: 0");
    
    //// Player info
    /** Panel containing the player's info **/
    FlowPanel playerInfo = new FlowPanel();
    /** Label containing the player's name **/
    Label playerName = new Label("YOU");
    /** Label containing the player's total credit-hours **/
    Label playerCHTotal = new Label("Credit-Hours: 0");
    /** Label containing the player's score **/
    Label playerScore = new Label("Score: 0");
    /** Label containing player's probation status **/
    Label playerProbationStatus = new Label("Probation: NO");
    /** Label containing player's ogre prof status **/
    Label playerOgreProfStatus = new Label("Ogre Prof: NO");
    /** Panel containing the opponent's info  **/
    FlowPanel opponentInfo = new FlowPanel();
    /** Label containing the opponent's name **/
    Label opponentName = new Label("OPPONENT");
    /** Label containing the opponent's credit-hours **/
    Label opponentCHTotal = new Label("Credit-Hours: 0");
    /** Label containing the opponent's score **/
    Label opponentScore = new Label("Score: 0");
    /** Label containing opponent's probation status **/
    Label opponentProbationStatus = new Label("Probation: NO");
    /** Label containing opponent's ogre prof status **/
    Label opponentOgreProfStatus = new Label("Ogre Prof: NO");
    
    /** Panel containing status messages **/
    FlowPanel status = new FlowPanel();
    /** Button to start the game **/
    Button gameStartButton = new Button("Start New Game");
    /** Text box for entering player name **/
    TextBox nameEntryTextBox = new TextBox();
    /** Label for {@link #nameEntryTextBox} **/
    Label nameEntryLabel = new Label("Your Name");
    /** Radio button indicating a game against an AI **/
    RadioButton vsAIButton = new RadioButton("playMode", "Play vs. AI");
    /** Radio button indicating a game against a human over the internet **/
    RadioButton vsHumanButton = new RadioButton("playMode", "Play vs. Human");
    /** Label used when waiting for opponent **/
    Label waitingForOpponent = new Label("Waiting for opponent to join...");
    /** Label used when waiting for opponent **/
    Label waitingForOpponentPlay = new Label("Waiting for your opponent to play...");
    /** Label containing the most recent status message **/
    Label errorMessage = new Label();
    /** Text area containing actions taken **/
    TextArea narrative = new TextArea();
    
    /** Panel for cheat options **/
    FlowPanel cheatPanel = new FlowPanel();
    /** Check box for showing/hiding opponent cards **/
    CheckBox showOpponentCardsCheckBox = new CheckBox("Show opponent cards");
    /** Panel containing end after X turns cheat **/
    HorizontalPanel endAfterPanel = new HorizontalPanel();
    /** Text box for entering how many turns to end after **/
    TextBox endAfterTextBox = new TextBox();
    /** Label for {@link #endAfterTextBox} **/
    Label endAfterLabel = new Label("End after this many turns (takes effect at end of round)");
    /** Panel containing starting credit-hours cheat cheat **/
    HorizontalPanel startingCreditHoursPanel = new HorizontalPanel();
    /** Text box for entering how many credit-hours to start with **/
    TextBox startingCreditHoursTextBox = new TextBox();
    /** Label for {@link #startingCreditHoursTextBox} **/
    Label startingCreditHoursLabel = new Label("Start with this many credit hours (takes effect at game start)");
    /** Button for resetting the online game **/
    Button resetButton = new Button("Reset Online Game");
    
    //// RPC members
    /** RPC Service for internet play **/
    private MatriculationServiceAsync mService = GWT.create(MatriculationService.class);
    /** Timer used to poll the server **/
    private Timer pollTimer = null;
    /** Notes if we are currently awaiting a callback. **/
    // private boolean callbackPending = false;
    /** Notes if the most recent async callback failed. **/
    // private boolean callbackFailed = false;

    
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
    /** True if playing over the internet, false if the opponent is the AI **/
    private boolean internetPlay;
    /** The deck from which cards are drawn. **/
    private Deck stock;
    /** The most recently discarded card. **/
    private Card discard;
    /** The human player. **/
    private HumanPlayer human;
    /** The human player's name. **/
    private String name = "";
    /** The opponent player. **/
    private Player opponent;
    /** Whether this instance is the first player or not (relevant for internet play) **/
    private boolean iAmPlayer1;
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
    public void initializePlayers() {
        status.remove(gameStartButton);
        status.remove(nameEntryTextBox);
        status.remove(nameEntryLabel);
        status.remove(vsAIButton);
        status.remove(vsHumanButton);
        
        human = new HumanPlayer(name, startingCreditHours);
        
        // Get the opponent player via RPC or generating an AI
        if (internetPlay) {
            
            status.add(waitingForOpponent);
            // callback used when polling for a second player, if we don't get one initially
            final AsyncCallback<HumanPlayer> pollCallback = new AsyncCallback<HumanPlayer>() {
                public void onFailure(Throwable caught) {
                    status.remove(waitingForOpponent);
                    status.add(gameStartButton);
                    status.add(nameEntryTextBox);
                    status.add(nameEntryLabel);
                    status.add(vsAIButton);
                    status.add(vsHumanButton);
                    logger.log(Level.SEVERE, "initializePlayers() pollCallback failed");
                    putError("RPC error: " + caught.getMessage());
                }
                public void onSuccess(HumanPlayer o) {
                    if (o != null) {
                        status.remove(waitingForOpponent);
                        opponent = o;
                        logger.log(Level.SEVERE, "Player 2 found!");
                        startGame();
                    }
                }
            };
              
            // callback used when first joining the game
            final AsyncCallback<HumanPlayer> callback = new AsyncCallback<HumanPlayer>() {
                public void onFailure(Throwable caught) {
                    status.remove(waitingForOpponent);
                    status.add(gameStartButton);
                    status.add(nameEntryTextBox);
                    status.add(nameEntryLabel);
                    status.add(vsAIButton);
                    status.add(vsHumanButton);
                    try {
                        throw caught;
                    } catch (GameStartException e) {
                        putError("Error starting game: " + e.getMessage());
                    } catch (Throwable e) {
                        putError("RPC error: " + e.getMessage());
                    }
                }
                public void onSuccess(HumanPlayer o) {
                    putError("");
                    if (o != null) {
                        status.remove(waitingForOpponent);
                        opponent = o;
                        logger.log(Level.SEVERE, "Player 2 found!");
                        startGame();
                    }
                    else {
                        // If we've reached this point then we are waiting for another player in the lobby
                        logger.log(Level.SEVERE, "Polling for a second player...");
                        pollTimer = new Timer() {
                            public void run() {
                                mService.pollForOpponent(pollCallback);
                            }
                        };
                        // Poll for an opponent every second
                        pollTimer.scheduleRepeating(1000);
                    }
                }
            };
            
            mService.joinGame(human, callback);
        }
        else {
            opponent = new AIPlayer("AI", startingCreditHours);
            startGame();
        }
    }
    
    /**
    * Cancels the poll timer after first checking if it's null.
    * This avoids errors.
    **/
    private void cancelPollTimer() {
        if (pollTimer != null) {
            pollTimer.cancel();
            pollTimer = null;
        }
    }
    
    public void startGame() {
        cancelPollTimer();
        opponentName.setText(opponent.getName());
        
        if (internetPlay) {
            // Get game information from the server
            
            stock = null;   // Server will provide necessary information about the deck
            
            // Am I player 1 or 2?
            mService.isMyTurn(human.getName(), new AsyncCallback<Boolean>() {
                public void onFailure(Throwable caught) {
                    putError("RPC error: " + caught.getMessage());
                    logger.log(Level.SEVERE, caught.getMessage());
                }
                public void onSuccess(Boolean b) {
                    iAmPlayer1 = b.booleanValue();
                    waitingForPlayer = iAmPlayer1;

            
                    // Get player cards
                    mService.getHand(human.getName(), new AsyncCallback<Pile>() {
                        public void onFailure(Throwable caught) {
                            putError("RPC error: " + caught.getMessage());
                            logger.log(Level.SEVERE, caught.getMessage());
                        }
                        public void onSuccess(Pile cards) {
                            human.hand = cards;

                            // Get opponent cards
                            mService.getHand(opponent.getName(), new AsyncCallback<Pile>() {
                                public void onFailure(Throwable caught) {
                                    putError("RPC error: " + caught.getMessage());
                                    logger.log(Level.SEVERE, caught.getMessage());
                                }
                                public void onSuccess(Pile cards) {
                                    opponent.hand = cards;
                                    updateDisplay();
                                }
                            });
                            gameActive = true;
                            discard = null;
                            
                            status.remove(waitingForOpponentPlay);
                            updateDisplay();
                            narrative.setText("Game start!");
                            status.add(narrative);
                            
                            startRound();
                        }
                    });

                }
            });
        }
        
        else {
            stock = new Deck();
            waitingForPlayer = false;
            for (int i = 0; i < Player.HANDSIZE; i++) {
                human.hand.add(stock.draw());
                opponent.hand.add(stock.draw());
            }
            
            gameActive = true;
            discard = null;
            iAmPlayer1 = true;
            
            status.remove(waitingForOpponentPlay);
            updateDisplay();
            narrative.setText("Game start!");
            status.add(narrative);
            
            startRound();
        }
    }
    
    /**
    * Starts the next round by waiting for the user to make their turn.
    **/
    private void startRound() {
        if (iAmPlayer1) {
            // Make sure player has cards
            if (human.hand.size() > 0) {
                waitingForPlayer = true;
                logger.log(Level.SEVERE, "We are waiting for the player!");
            }
        }
        else {  // This will only happen during internet play
            if (opponent.hand.size() > 0) {
                status.add(waitingForOpponentPlay);
                
                // callback used when polling for the opponent's play.
                final AsyncCallback<Boolean> pollCallback = new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        putError("RPC error: " + caught.getMessage());
                    }
                    public void onSuccess(Boolean isMyTurn) {
                        if (isMyTurn) {
                            logger.log(Level.SEVERE, "My turn now!");
                            status.remove(waitingForOpponentPlay);
                            
                            // Get the index of the last played card, which will be the opponent's played card
                            mService.getLastPlayIndex(new AsyncCallback<Integer>() {
                                public void onFailure(Throwable caught) {
                                    putError("RPC error: " + caught.getMessage());
                                }
                                public void onSuccess(Integer index) {
                                    int opponentPlayed = index.intValue();

                                    if (opponentPlayed >= 0) {
                                        try {
                                            opponent.play(opponent.hand.get(opponentPlayed), human);
                                        } catch (InvalidPlayException e) {} // server already checked this
                                        putNarrative(opponent.getName() + " played " + opponent.hand.get(opponentPlayed).toString());
                                        addToPile(opponent.hand.get(opponentPlayed), opponent, human);
                                        opponent.hand.remove(opponentPlayed);
                                    }
                                    else {
                                        int discardedIndex = -opponentPlayed - 1;
                                        putNarrative(opponent.getName() + " discarded " + opponent.hand.get(discardedIndex).toString());
                                        discard = opponent.hand.get(discardedIndex);
                                        opponent.hand.remove(discardedIndex);
                                    }
                                    updateDisplay();
                                    startSecondTurn();
                                }
                            });
                        }
                        else logger.log(Level.SEVERE, "Not my turn yet...");
                    }
                };
                
                pollTimer = new Timer() {
                    public void run() {
                        mService.isMyTurn(human.getName(), pollCallback);
                    }
                };
                
                // Poll for opponent play once per second
                pollTimer.scheduleRepeating(1000);
            }
        }
    }
    
    /**
    * Finishes processing the first turn and starts the second turn.
    * After each turn, checks if a game-ending condition is met and ends the game if so.
    * @see AIPlayer#takeTurn(Player)
    **/
    public void startSecondTurn() {
        cancelPollTimer();
        
        // Check for winner
        if (human.getCreditHours() >= Player.MAX_CREDITHOURS) {
            endGame(human, true);
            updateDisplay();
            return;
        }
        if (opponent.getCreditHours() >= Player.MAX_CREDITHOURS) {
            endGame(opponent, true);
            updateDisplay();
            return;
        }
        
        // Get next drawn card
        if (internetPlay) {
            mService.getLastDrawnCard(new AsyncCallback<Card>() {
                public void onFailure(Throwable caught) {
                    putError("RPC error: " + caught.getMessage());
                }
                public void onSuccess(Card card) {
                    if (card != null) {
                        if (iAmPlayer1)
                            human.hand.add(card);
                        else
                            opponent.hand.add(card);
                    }
                    updateDisplay();
                }
            });
        }
        else {
            // Draw if there are still cards in the deck
            try {
                human.hand.add(stock.draw());
            } catch (NoSuchElementException e) {}
            updateDisplay();
        }
        
        // Start the second turn
        if (!iAmPlayer1) {
            // Make sure player has cards
            if (human.hand.size() > 0) {
                waitingForPlayer = true;
                logger.log(Level.SEVERE, "We are waiting for the player!");
            }
        }
        else {
            if (internetPlay) {
                if (opponent.hand.size() > 0) {
                    status.add(waitingForOpponentPlay);
                    
                    // callback used when polling for the opponent's play.
                    final AsyncCallback<Boolean> pollCallback = new AsyncCallback<Boolean>() {
                        public void onFailure(Throwable caught) {
                            putError("RPC error: " + caught.getMessage());
                        }
                        public void onSuccess(Boolean isMyTurn) {
                            if (isMyTurn) {
                                logger.log(Level.SEVERE, "My turn now!");
                                status.remove(waitingForOpponentPlay);
                                
                                // Get the index of the last played card, which will be the opponent's played card
                                mService.getLastPlayIndex(new AsyncCallback<Integer>() {
                                    public void onFailure(Throwable caught) {
                                        putError("RPC error: " + caught.getMessage());
                                    }
                                    public void onSuccess(Integer index) {
                                        int opponentPlayed = index.intValue();
                                    
                                        if (opponentPlayed >= 0) {
                                            try {
                                                opponent.play(opponent.hand.get(opponentPlayed), human);
                                            } catch (InvalidPlayException e) {} // server already checked this
                                            putNarrative(opponent.getName() + " played " + opponent.hand.get(opponentPlayed).toString());
                                            addToPile(opponent.hand.get(opponentPlayed), opponent, human);
                                            opponent.hand.remove(opponentPlayed);
                                        }
                                        else {
                                            int discardedIndex = -opponentPlayed - 1;
                                            putNarrative(opponent.getName() + " discarded " + opponent.hand.get(discardedIndex).toString());
                                            discard = opponent.hand.get(discardedIndex);
                                            opponent.hand.remove(discardedIndex);
                                        }
                                        updateDisplay();
                                        endRound();
                                    }
                                });
                            }
                            else logger.log(Level.SEVERE, "Not my turn yet...");
                        }
                    };
                    
                    pollTimer = new Timer() {
                        public void run() {
                            mService.isMyTurn(human.getName(), pollCallback);
                        }
                    };
                    
                    // Poll for opponent play once per second
                    pollTimer.scheduleRepeating(1000);
                }
            }
            else {
                if (opponent.hand.size() > 0) {
                    int aiPlayed = opponent.takeTurn(human);
                    if (aiPlayed >= 0) {
                        putNarrative("Opponent played " + opponent.hand.get(aiPlayed).toString());
                        addToPile(opponent.hand.get(aiPlayed), opponent, human);
                        opponent.hand.remove(aiPlayed);
                    }
                    else {
                        int discardedIndex = -aiPlayed - 1;
                        putNarrative("Opponent discarded " + opponent.hand.get(-aiPlayed - 1).toString());
                        discard = opponent.hand.get(discardedIndex);
                        opponent.hand.remove(discardedIndex);
                    }
                    updateDisplay();
                }
                endRound();
            }
        }
    }
    
    /**
    * Finishes processing the second turn and ends the round.
    * After each turn, checks if a game-ending condition is met and ends the game if so.
    **/
    public void endRound() {
        cancelPollTimer();
        
        // Check for winner
        if (human.getCreditHours() >= Player.MAX_CREDITHOURS) {
            endGame(human, true);
            updateDisplay();
            return;
        }
        if (opponent.getCreditHours() >= Player.MAX_CREDITHOURS) {
            endGame(opponent, true);
            updateDisplay();
            return;
        }
        
        // Get next drawn card
        if (internetPlay) {
            mService.getLastDrawnCard(new AsyncCallback<Card>() {
                public void onFailure(Throwable caught) {
                    putError("RPC error: " + caught.getMessage());
                }
                public void onSuccess(Card card) {
                    if (card != null) {
                        if (!iAmPlayer1)
                            human.hand.add(card);
                        else
                            opponent.hand.add(card);
                    }
                    updateDisplay();
                }
            });
        }
        else {
            // Draw if there are still cards in the deck
            try {
                opponent.hand.add(stock.draw());
            } catch (NoSuchElementException e) {}
            updateDisplay();
        }
        
        // End game if max turns is reached or if there are no cards anywhere
        turns++;
        if (endAfter > 0 && turns == endAfter)
            endGame(human, false);
        
        if (internetPlay) {
            mService.outOfCards(new AsyncCallback<Boolean>() {
                public void onFailure(Throwable caught) {
                    putError("RPC error: " + caught.getMessage());
                }
                public void onSuccess(Boolean outOfCards) {
                    if (outOfCards)
                        endGame(human, false);
                }
            });
        }
        else if (stock.size() == 0 && opponent.hand.size() == 0 && human.hand.size() == 0)
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
            putNarrative("There are no more cards. Game over!");
        
        // Declare a winner
        if (human.getScore() > opponent.getScore())
            putNarrative(human.getName() + " wins!");
        else if (human.getScore() < opponent.getScore())
            putNarrative(opponent.getName() + " wins!");
        else
            putNarrative("There was a tie! Everyone wins :)");
        
        status.remove(narrative);
        status.add(gameStartButton);
        status.add(nameEntryTextBox);
        status.add(nameEntryLabel);
        status.add(vsAIButton);
        status.add(vsHumanButton);
        status.add(narrative);
    }
    
    /** 
    * Adds a card to the appropriate pile.
    * @param card the card to add
    * @param player the player playing the card
    * @param opponent the opponent of player, for the case of setbacks
    **/
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
    
    /**
    * Handles various click events
    * @param event the triggering event
    **/
    public void onClick(ClickEvent event) {
        if (event.getSource() == gameStartButton)
            initializePlayers();
        else if (event.getSource() == resetButton) {
            mService.reset(new AsyncCallback<Void>() {
                public void onFailure(Throwable caught) {
                    putError("RPC error: " + caught.getMessage());
                }
                public void onSuccess(Void result) {
                    com.google.gwt.user.client.Window.Location.reload();
                }
            });
        }
        // if they clicked on a pile
        else if (event.getSource() == playerControl)
            showPileContents(human.controlPile, human, "Control");
        else if (event.getSource() == opponentControl)
            showPileContents(opponent.controlPile, opponent, "Control");
        else if (event.getSource() == playerCreditHour)
            showPileContents(human.creditHourPile, human, "Credit-Hour");
        else if (event.getSource() == opponentCreditHour)
            showPileContents(opponent.creditHourPile, opponent, "Credit-Hour");
        else if (event.getSource() == playerProbation)
            showPileContents(human.probationPile, human, "Probation");
        else if (event.getSource() == opponentProbation)
            showPileContents(opponent.probationPile, opponent, "Probation");
        else if (event.getSource() == playerException)
            showPileContents(human.exceptionPile, human, "Exception");
        else if (event.getSource() == opponentException)
            showPileContents(opponent.exceptionPile, opponent, "Exception");
        
        // if they clicked one of their cards
        else if (playerCards.contains(event.getSource())
            && !((FocusPanel)event.getSource()).getStyleName().contains("emptyCard")
            && waitingForPlayer) {   // if it's a player card, make sure it's not empty
            final int i = playerCards.indexOf(event.getSource());
            
            // discard if ctrl is pressed
            if (event.isControlKeyDown()) { 
                if (internetPlay) {
                    mService.discard(human.getName(), i, new AsyncCallback<Void>() {
                        public void onFailure(Throwable caught) {
                            putError("RPC error: " + caught.getMessage());
                        }
                        public void onSuccess(Void result) {
                            putNarrative("You discarded " + human.hand.get(i).toString());
                            putError("");
                            discard = human.hand.get(i);
                            human.hand.remove(i);
                            waitingForPlayer = false;
                            if (iAmPlayer1) startSecondTurn();
                            else endRound();
                        }
                    });
                }
                else {
                    putNarrative("You discarded " + human.hand.get(i).toString());
                    putError("");
                    discard = human.hand.get(i);
                    human.hand.remove(i);
                    waitingForPlayer = false;
                    if (iAmPlayer1) startSecondTurn();
                    else endRound();
                }
            }
            else {
                try {
                    // Try to play client side
                    human.play(human.hand.get(i), opponent);
                    
                    if (internetPlay) {
                        // Try to play server side
                        mService.play(human.getName(), i, new AsyncCallback<Void>() {
                            public void onFailure(Throwable caught) {
                                try {
                                    throw caught;
                                } catch (InvalidPlayException e) {
                                    logger.log(Level.SEVERE, "Play error happened server-side");
                                    putError(e.getMessage());
                                } catch (Throwable e) {
                                    putError("RPC error: " + e.getMessage());
                                }
                            }
                            public void onSuccess(Void result) {
                                putNarrative("You played " + human.hand.get(i).toString());
                                putError("");
                                addToPile(human.hand.get(i), human, opponent);
                                human.hand.remove(i);
                                waitingForPlayer = false;
                                if (iAmPlayer1) startSecondTurn();
                                else endRound();
                            }
                        });
                    }
                    
                    else {
                        putNarrative("You played " + human.hand.get(i).toString());
                        putError("");
                        addToPile(human.hand.get(i), human, opponent);
                        human.hand.remove(i);
                        waitingForPlayer = false;
                        if (iAmPlayer1) startSecondTurn();
                        else endRound();
                    }
                } catch (InvalidPlayException e) {
                    putError(e.getMessage());
                }
            }
        }
    }
    
    /**
    * Handles various key down events
    * @param event the triggering event
    **/
    public void onKeyDown(KeyDownEvent event) {
        if (event.getSource() == gameStartButton && event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            initializePlayers();
        else if (event.getSource() == opponentCards.get(4) && event.getNativeKeyCode() == KeyCodes.KEY_C)
            RootPanel.get().add(cheatPanel);
        /*else if (playerCards.contains(event.getSource())
            && !((FocusPanel)event.getSource()).getStyleName().contains("emptyCard")
            && waitingForPlayer
            && event.getNativeKeyCode() == KeyCodes.KEY_D) {   // if it's a player card, make sure it's not empty
            int i = playerCards.indexOf(event.getSource());
            putNarrative("You discarded.");
            discard = human.hand.get(i);
            human.hand.remove(i);
            waitingForPlayer = false;
            endRound();
        }*/
    }
    
    /**
    * Handles various key up events
    * @param event the triggering event
    **/
    public void onKeyUp(KeyUpEvent event) {
        if (event.getSource() == nameEntryTextBox) {
            name = nameEntryTextBox.getValue();
            playerName.setText(name);
        }
        else if (event.getSource() == endAfterTextBox) {
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
    
    /**
    * Handles checkbox/radio button value change events.
    * Note that this will only trigger when the value is set to true.
    * @param event the triggering event
    **/
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getSource() == showOpponentCardsCheckBox) {
            showOpponentCards = showOpponentCardsCheckBox.getValue();
            updateDisplay();
        }
        else if (event.getSource() == vsAIButton)
            internetPlay = false;
        else if (event.getSource() == vsHumanButton)
            internetPlay = true;
    }
    
    /**
    * Updates the UI to match the details of the game
    * This includes cards, piles, player info, etc.
    **/
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
        hand = opponent.getHand();
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
        updatePile(opponent.controlPile, opponentControl, opponentControlLabel);
        updatePile(opponent.creditHourPile, opponentCreditHour, opponentCreditHourLabel);
        updatePile(opponent.probationPile, opponentProbation, opponentProbationLabel);
        updatePile(opponent.exceptionPile, opponentException, opponentExceptionLabel);
        
        // DECK
        if (internetPlay) {
            final int stockSize;
            mService.getStockSize(new AsyncCallback<Integer>() {
                public void onFailure(Throwable caught) {
                    putError("RPC error: " + caught.getMessage());
                }
                public void onSuccess(Integer size) {
                    countRemaining.setText("Remaining: " + size.intValue());
                }
            });
        }
        else
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
        playerProbationStatus.setText("Probation: " + (human.probation ? "YES" : "NO"));
        playerProbationStatus.setStyleName("gwt-Label " + (human.probation ? "badMessage" : "goodMessage"));
        playerOgreProfStatus.setText("Ogre Prof: " + (human.ogreProf ? "YES" : "NO"));
        playerOgreProfStatus.setStyleName("gwt-Label " + (human.ogreProf ? "badMessage" : "goodMessage"));
        opponentCHTotal.setText("Credit-Hours: " + opponent.getCreditHours());
        opponentProbationStatus.setText("Probation: " + (opponent.probation ? "YES" : "NO"));
        opponentProbationStatus.setStyleName("gwt-Label " + (opponent.probation ? "badMessage" : "goodMessage"));
        opponentOgreProfStatus.setText("Ogre Prof: " + (opponent.ogreProf ? "YES" : "NO"));
        opponentOgreProfStatus.setStyleName("gwt-Label " + (opponent.ogreProf ? "badMessage" : "goodMessage"));
        opponentScore.setText("Score: " + opponent.getScore());
    }
    
    /**
    * Updates the top card of a pile in the UI
    * @param pile the actual pile object
    * @param pilePanel the panel containing the pile's UI
    * @param pileLabel the label for the pile's UI
    **/
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
    
    /**
    * Displays the contents of a clicked pile in a dialog box
    * @param pile the actual pile object
    * @param owner the player who owns the pile
    * @param name the name of the pile to write
    **/
    public void showPileContents(Pile pile, Player owner, String name) {
        if (pile.size() == 0) return;
        
        pileDialog.setText("Contents of " + owner.getName() + "'s " + name + " Pile");
        pileDialogContents.setHTML(pile.listCardsHTML());
        pileDialog.show();
    }
    
    /**
    * Puts a message in the status line
    * @param message the message to write
    * @param good true to make the message green, false for red
    **/    
    private void putError(String message) {
        errorMessage.setText(message);
        errorMessage.setStyleName("badMessage");
    }
    
    /**
    * Appends a message to the narrative box
    * @param message the message to write
    **/    
    private void putNarrative(String message) {
        narrative.setText(narrative.getText() + "\n" + message);
    }
    
    
    /**
    * Code run when the page is first loaded
    **/  
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
            opponentCardLabels.get(i).addStyleName("card-text");
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
        playerProbationStatus.addStyleName("goodMessage");
        playerInfo.add(playerProbationStatus);
        playerOgreProfStatus.addStyleName("goodMessage");
        playerInfo.add(playerOgreProfStatus);
        playerInfo.addStyleName("playerInfo");
        opponentName.addStyleName("playerName");
        opponentInfo.add(opponentName);
        opponentInfo.add(opponentCHTotal);
        opponentInfo.add(opponentScore);
        opponentProbationStatus.addStyleName("goodMessage");
        opponentInfo.add(opponentProbationStatus);
        opponentOgreProfStatus.addStyleName("goodMessage");
        opponentInfo.add(opponentOgreProfStatus);
        opponentInfo.addStyleName("playerInfo");
        
        drawDiscard.add(playerInfo);
        drawDiscard.add(deckWrapper);
        drawDiscard.add(discardPanel);
        drawDiscard.add(opponentInfo);
        drawDiscard.addStyleName("gameRow");
        
        // Set up the start button and options
        status.add(errorMessage);
        gameStartButton.addClickHandler(this);
        gameStartButton.addKeyDownHandler(this);
        nameEntryTextBox.addKeyUpHandler(this);
        vsAIButton.addValueChangeHandler(this);
        vsAIButton.setValue(true);
        vsHumanButton.addValueChangeHandler(this);
        status.add(gameStartButton);
        status.add(nameEntryTextBox);
        status.add(nameEntryLabel);
        status.add(vsAIButton);
        status.add(vsHumanButton);
        status.addStyleName("statusRow");
        narrative.setVisibleLines(3);
        narrative.setCharacterWidth(50);
        narrative.setReadOnly(true);
        
        // Set up dialog box showing pile contents
        pileDialog.add(pileDialogContents);
        
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
        showOpponentCardsCheckBox.addValueChangeHandler(this);
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
        resetButton.addClickHandler(this);
        cheatPanel.add(resetButton);
        
        pileDialog.show();  // this makes it not show at the beginning for some reason?
    }
}
