package tictactoe;


import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class TicTacToeGame {

    private static int GAME_COUNT = 0;

    public enum Status {
        WAITING, IN_PROGRESS, WON, TIED
    }

    public enum PlayerLetter {
        X, O
    }

    private final int id;

    private Status status;

    private final TicTacToeBoard board;
    private Map<PlayerLetter, Player> players;
    private PlayerLetter winner;

    public TicTacToeGame() {
        this.id = GAME_COUNT++;
        this.board = new TicTacToeBoard();
        status = Status.WAITING;
        players = new EnumMap<PlayerLetter, Player>(PlayerLetter.class);
    }

    public int getId() {
        return id;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public Player getPlayer(PlayerLetter playerLetter) {
        return players.get(playerLetter);
    }

    public TicTacToeBoard getBoard() {
        return board;
    }

    public Status getStatus() {
        return status;
    }

    public PlayerLetter getWinner() {
        return winner;
    }

    public PlayerLetter addPlayer(Player p) {
        if (players.size() >= 2) {
            throw new RuntimeException("Too many players. Cannot add more than 1 player to a game.");
        }

        PlayerLetter playerLetter = (players.containsKey(PlayerLetter.X)) ? PlayerLetter.O : PlayerLetter.X;
        p.setLetter(playerLetter);
        players.put(playerLetter, p);

        if (players.size() == 2) {
            status = Status.IN_PROGRESS;
        }
        return playerLetter;
    }

    public void markCell(int gridId, PlayerLetter playerLetter) {
        board.markCell(gridId, playerLetter);
        setStatus(playerLetter);
    }

    private void setStatus(PlayerLetter playerLetter) {
        if (board.isWinner(playerLetter)) {
            status = Status.WON;

            if (playerLetter == PlayerLetter.X) {
                winner = PlayerLetter.X;
            } else {
                winner = PlayerLetter.O;
            }
        } else if (board.isTied()) {
            status = Status.TIED;
        }
    }

    public Player getOpponent(String currentPlayer) {
        PlayerLetter currentPlayerLetter = PlayerLetter.valueOf(currentPlayer);
        PlayerLetter opponentPlayerLetter = currentPlayerLetter.equals(PlayerLetter.X) ? PlayerLetter.O : PlayerLetter.X;
        return players.get(opponentPlayerLetter);
    }

    public boolean isPlayerWinner(PlayerLetter playerLetter) {
        return status == Status.WON && winner == playerLetter;
    }

    public boolean isTied() {
        return status == Status.TIED;
    }
}