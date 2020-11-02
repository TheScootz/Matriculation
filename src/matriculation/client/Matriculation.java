package matriculation.client;

import matriculation.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Matriculation implements EntryPoint {
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
    
    public void onModuleLoad() {
        
        
        
        // Set up players' card and pile areas
        playerCardArea.addStyleName("center");
        playerPileArea.addStyleName("center");
        playerArea.add(playerPileArea);
        playerArea.add(playerCardArea);
        
        opponentCardArea.addStyleName("center");
        opponentPileArea.addStyleName("center");
        opponentArea.add(opponentCardArea);    // mirrored from player's perspective
        opponentArea.add(opponentPileArea);
        
        
        // Create card slots
        int i;
        for (i = 0; i < Player.HANDSIZE; ++i) {
            playerCards[i] = new FlowPanel();
            playerCards[i].add(new HTML(""));
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
        
        
        // Create piles
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
        
        
        // Create draw and discard piles
        deck.addStyleName("card cardBack");
        deckWrapper.add(deck);
        deckWrapper.add(countRemaining);
        deckWrapper.addStyleName("center");
        drawDiscard.add(deckWrapper);
        discard.addStyleName("card emptyCard");
        drawDiscard.add(discard);
        drawDiscard.addStyleName("center");
        
        // Add the panels to the page
        gameArea.add(opponentArea);
        gameArea.add(drawDiscard);
        gameArea.add(playerArea);
        RootPanel.get().add(gameArea);
    }
}
