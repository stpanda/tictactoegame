package server;

import static javax.ws.rs.core.HttpHeaders.HOST;
import static server.messages.GameOverMessage.Result.TIED;
import static server.messages.GameOverMessage.Result.YOU_WIN;
import static server.messages.TurnMessage.Turn.WAITING;
import static server.messages.TurnMessage.Turn.YOUR_TURN;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import server.messages.*;
import tictactoe.Player;
import tictactoe.TicTacToeGame;
import tictactoe.TicTacToeGame.PlayerLetter;

import java.util.HashMap;
import java.util.Map;

public class TicTacToeServerHandler extends SimpleChannelInboundHandler<Object> {

    private static Map<Integer, TicTacToeGame> games = new HashMap<>();

    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {

        if (req.method() != HttpMethod.GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(this.getWebSocketLocation(req), null, false);
        this.handshaker = wsFactory.newHandshaker(req);
        if (this.handshaker == null) {
            wsFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {

            this.handshaker.handshake(ctx.channel(), req);
            initGame(ctx);
        }
    }

    private void initGame(ChannelHandlerContext ctx) {
        TicTacToeGame game = findGame();
        Player player = new Player(ctx.channel());
        TicTacToeGame.PlayerLetter letter = game.addPlayer(player);
        games.put(game.getId(), game);
        ctx.channel().write(new TextWebSocketFrame(new HandshakeMessage(game.getId(), letter.toString()).toJson()));
        if (game.getStatus() == TicTacToeGame.Status.IN_PROGRESS) {
            game.getPlayer(PlayerLetter.X).getChannel().write(new TextWebSocketFrame(new TurnMessage(YOUR_TURN).toJson()));
            game.getPlayer(PlayerLetter.O).getChannel().write(new TextWebSocketFrame(new TurnMessage(WAITING).toJson()));
        }
    }

    private TicTacToeGame findGame() {
        for (TicTacToeGame g : games.values()) {
            if (g.getStatus().equals(TicTacToeGame.Status.WAITING)) {
                return g;
            }
        }
        return new TicTacToeGame();
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            this.handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            return;
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content()));
            return;
        } else if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        Gson gson = new Gson();
        IncomingMessage message = gson.fromJson(((TextWebSocketFrame) frame).text(), IncomingMessage.class);

        TicTacToeGame game = games.get(message.getGameId());
        Player opponent = game.getOpponent(message.getPlayer());
        Player player = game.getPlayer(PlayerLetter.valueOf(message.getPlayer()));
        game.markCell(message.getGridIdAsInt(), player.getLetter());
        boolean winner = game.isPlayerWinner(player.getLetter());
        boolean tied = game.isTied();
        String responseToOpponent = new OutgoingMessage(player.getLetter().toString(), message.getGridId(), winner, tied).toJson();
        opponent.getChannel().write(new TextWebSocketFrame(responseToOpponent));
        if (winner) {
            player.getChannel().write(new TextWebSocketFrame(new GameOverMessage(YOU_WIN).toJson()));
        } else if (tied) {
            player.getChannel().write(new TextWebSocketFrame(new GameOverMessage(TIED).toJson()));
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
        }
        ChannelFuture f = ctx.channel().write(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private String getWebSocketLocation(HttpRequest req) {
        String protocol = "ws";
        return protocol + "://" + req.headers().get(HOST) + WEBSOCKET_PATH;
    }
}
