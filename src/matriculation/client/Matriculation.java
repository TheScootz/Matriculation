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
    public void onModuleLoad() {
        FlowPanel gameArea = new FlowPanel();
        
        // Create the 3 play areas: player's, opponent's, and neutral
        FlowPanel playerArea = new FlowPanel();
        FlowPanel drawDiscard = new FlowPanel();
        FlowPanel opponentArea = new FlowPanel();
        
        
        // Set up players' card and pile areas
        HorizontalPanel playerCardArea = new HorizontalPanel();
        HorizontalPanel playerPileArea = new HorizontalPanel();
        playerCardArea.addStyleName("center");
        playerPileArea.addStyleName("center");
        playerArea.add(playerPileArea);
        playerArea.add(playerCardArea);
        
        HorizontalPanel opponentCardArea = new HorizontalPanel();
        HorizontalPanel opponentPileArea = new HorizontalPanel();
        opponentCardArea.addStyleName("center");
        opponentPileArea.addStyleName("center");
        opponentArea.add(opponentCardArea);    // mirrored from player's perspective
        opponentArea.add(opponentPileArea);
        
        
        // Create card slots
        FlowPanel[] playerCards = new FlowPanel[Player.HANDSIZE];
        int i;
        for (i = 0; i < Player.HANDSIZE; ++i) {
            playerCards[i] = new FlowPanel();
            playerCards[i].add(new HTML(""));
            playerCards[i].addStyleName("card");
            playerCards[i].addStyleName("emptyCard");
            playerCardArea.add(playerCards[i]);
        }
        
        FlowPanel[] opponentCards = new FlowPanel[Player.HANDSIZE];
        for (i = 0; i < Player.HANDSIZE; ++i) {
            opponentCards[i] = new FlowPanel();
            opponentCards[i].add(new HTML(""));
            opponentCards[i].addStyleName("card");
            opponentCards[i].addStyleName("emptyCard");
            opponentCardArea.add(opponentCards[i]);
        }
        
        
        // Create piles
        //// CONTROL
        FlowPanel playerControlWrapper = new FlowPanel();
        FlowPanel playerControl = new FlowPanel();
        playerControl.add(new HTML(""));
        playerControl.addStyleName("card emptyCard");
        playerControlWrapper.add(playerControl);
        playerPileArea.add(playerControlWrapper);
        
        FlowPanel opponentControlWrapper = new FlowPanel();
        FlowPanel opponentControl = new FlowPanel();
        opponentControl.add(new HTML(""));
        opponentControl.addStyleName("card emptyCard");
        opponentControlWrapper.add(opponentControl);
        opponentPileArea.add(opponentControlWrapper);
        
        //// CREDIT-HOURS
        FlowPanel playerCreditHourWrapper = new FlowPanel();
        FlowPanel playerCreditHour = new FlowPanel();
        playerCreditHour.add(new HTML(""));
        playerCreditHour.addStyleName("card emptyCard");
        playerCreditHourWrapper.add(playerCreditHour);
        playerPileArea.add(playerCreditHourWrapper);
        
        FlowPanel opponentCreditHourWrapper = new FlowPanel();
        FlowPanel opponentCreditHour = new FlowPanel();
        opponentCreditHour.add(new HTML(""));
        opponentCreditHour.addStyleName("card emptyCard");
        opponentCreditHourWrapper.add(opponentCreditHour);
        opponentPileArea.add(opponentCreditHourWrapper);
        
        //// PROBATION
        FlowPanel playerProbationWrapper = new FlowPanel();
        FlowPanel playerProbation = new FlowPanel();
        playerProbation.add(new HTML(""));
        playerProbation.addStyleName("card emptyCard");
        playerProbationWrapper.add(playerProbation);
        playerPileArea.add(playerProbationWrapper);
        
        FlowPanel opponentProbationWrapper = new FlowPanel();
        FlowPanel opponentProbation = new FlowPanel();
        opponentProbation.add(new HTML(""));
        opponentProbation.addStyleName("card emptyCard");
        opponentProbationWrapper.add(opponentProbation);
        opponentPileArea.add(opponentProbationWrapper);
        
        //// EXCEPTIONS
        FlowPanel playerExceptionWrapper = new FlowPanel();
        FlowPanel playerException = new FlowPanel();
        playerException.add(new HTML(""));
        playerException.addStyleName("card emptyCard");
        playerExceptionWrapper.add(playerException);
        playerPileArea.add(playerExceptionWrapper);
        
        FlowPanel opponentExceptionWrapper = new FlowPanel();
        FlowPanel opponentException = new FlowPanel();
        opponentException.add(new HTML(""));
        opponentException.addStyleName("card emptyCard");
        opponentExceptionWrapper.add(opponentException);
        opponentPileArea.add(opponentExceptionWrapper);


        gameArea.add(opponentArea);
        gameArea.add(drawDiscard);
        gameArea.add(playerArea);
        RootPanel.get().add(gameArea);
    }
}
