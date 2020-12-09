package matriculation.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import matriculation.shared.*;

/**
 * The client-side stub for the RPC service. Descriptions for these functions can be found in {@link matriculation.server.MatriculationServer}.
 */
@RemoteServiceRelativePath("m")
public interface MatriculationService extends RemoteService {
    HumanPlayer joinGame(HumanPlayer joining) throws GameStartException;
    HumanPlayer pollForOpponent();
    Boolean isMyTurn(String name);
    Integer getStockSize();
    Pile getHand(String name) throws IllegalArgumentException;
    void play(String name, int index) throws InvalidPlayException, IllegalArgumentException;
    void discard(String name, int index) throws IllegalArgumentException;
    Integer getLastPlayIndex();
    Card getLastDrawnCard();
    Boolean outOfCards();
    void reset();
    //boolean waitForTurn();
}
