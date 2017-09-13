package tictactoe;


import io.netty.channel.Channel;
import tictactoe.TicTacToeGame.PlayerLetter;

public class Player {

    private Channel channel;

    private PlayerLetter letter;

    public Player(Channel channel){
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public PlayerLetter getLetter() {
        return letter;
    }

    public void setLetter(PlayerLetter letter) {
        this.letter = letter;
    }
}
