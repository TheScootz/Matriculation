package matriculation.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import matriculation.shared.*;

/**
 * The async counterpart of <code>MatriculationService</code>. Descriptions for these functions can be found in {@link matriculation.server.MatriculationServer}.
 */
public interface MatriculationServiceAsync {
    void joinGame(HumanPlayer joining, AsyncCallback<HumanPlayer> callback);
    void pollForOpponent(AsyncCallback<HumanPlayer> callback);
    void getStockSize(AsyncCallback<Integer> callback);
    void isMyTurn(String name, AsyncCallback<Boolean> callback);
    void getHand(String name, AsyncCallback<Pile> callback);
    void play(String name, int index, AsyncCallback<Void> callback);
    void discard(String name, int index, AsyncCallback<Void> callback);
    void getLastPlayIndex(AsyncCallback<Integer> callback);
    void getLastDrawnCard(AsyncCallback<Card> callback);
    void outOfCards(AsyncCallback<Boolean> callback);
    void reset(AsyncCallback<Void> callback);
}
