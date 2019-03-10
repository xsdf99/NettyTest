package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import myProtocol.MyDecoder;
import myProtocol.MyEncoder;
import myProtocol.MyRequest;
import myProtocol.MyResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcClient {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Bootstrap bootstrap;

    public void start() {
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new MyEncoder(MyRequest.class))
                        .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 0))
                        .addLast(new MyDecoder(MyResponse.class))
                        .addLast(new ClientHandler(RpcClient.this));
            }
        });
        connect();
//            }
//        });
    }

    public void connect() {
        bootstrap.connect("127.0.0.1", 8080).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("client connect ok!!!!!!!!!!!!!!!!!!");
                }
            }
        });
    }
}
