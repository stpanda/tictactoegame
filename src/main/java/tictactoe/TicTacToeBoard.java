package tictactoe;

import tictactoe.TicTacToeGame.PlayerLetter;

public class TicTacToeBoard {

    public static final int[][] WINNING = { {1,2,3}, {4,5,6}, {7,8,9}, {1,4,7}, {2,5,8}, {3,6,9}, {1,5,9}, {3,5,7} };

    PlayerLetter[] cells = new PlayerLetter[9];

    protected void markCell(int gridId, PlayerLetter player) {
        cells[gridId-1] = player;
    }

    public boolean isWinner(PlayerLetter player) {
        for (int i = 0; i < WINNING.length; i++) {
            int[] possibleWinningCombo = WINNING[i];
            if (cells[possibleWinningCombo[0]-1] == player && cells[possibleWinningCombo[1]-1] == player && cells[possibleWinningCombo[2]-1] == player) {
                return true;
            }
        }
        return false;
    }

    public boolean isTied() {
        boolean boardFull = true;
        boolean tied = false;

        for (int i = 0; i < 9; i++) {
            PlayerLetter letter = cells[i];
            if (letter == null) {
                boardFull = false;
            }
        }

        if (boardFull && (!isWinner(PlayerLetter.X) || !isWinner(PlayerLetter.O))) {
            tied = true;
        }

        return tied;
    }
}
