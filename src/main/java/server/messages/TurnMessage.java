package server.messages;

/**
 * Created by Owner on 08.09.2017.
 */
public class TurnMessage extends MessageBean {

    public enum Turn {
        WAITING, YOUR_TURN
    }

    private final String type = "turn";
    private Turn turn;

    public TurnMessage(Turn t) {
        turn = t;
    }

    public String getType() {
        return type;
    }

    public Turn getTurn() {
        return turn;
    }
}
