package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class TicTacToeServer {

    public static void main(String[] args) throws Exception{
        System.out.println("Go Go Go...");
        new TicTacToeServer(9000).initServer();
    }

    private final int port;

    public TicTacToeServer(int port){
        this.port = port;
    }

    public void initServer() throws InterruptedException{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TicTacToeServerInitializer());

            serverBootstrap.bind(port).sync().channel().closeFuture().sync();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
