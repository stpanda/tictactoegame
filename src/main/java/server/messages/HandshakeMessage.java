package server.messages;


public class HandshakeMessage extends MessageBean {

    private final String type = "handshake";
    private int gameId;
    private String player;

    public HandshakeMessage(int gameId, String player) {
        this.gameId = gameId;
        this.player = player;
    }

    public String getType() {
        return type;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
