package server.messages;


public class GameOverMessage extends MessageBean {

    public enum Result {
        YOU_WIN, TIED
    }

    private final String type = "game_over";
    private Result result;

    public GameOverMessage(Result r) {
        result = r;
    }

    public String getType() {
        return type;
    }

    public Result getResult() {
        return result;
    }
}
