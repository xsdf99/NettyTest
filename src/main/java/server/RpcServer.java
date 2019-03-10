package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import myProtocol.MyDecoder;
import myProtocol.MyEncoder;
import myProtocol.MyRequest;
import myProtocol.MyResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcServer implements ApplicationContextAware, InitializingBean {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            8, 16, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ServerManager.handleMap = new HashMap<>();
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(Rpc.class);
        map.forEach((key, value) -> {
            for (Method method : value.getClass().getDeclaredMethods()) {
                RpcMethod rpcMethod = method.getDeclaredAnnotation(RpcMethod.class);
                if (rpcMethod != null) {
                    ServerManager.handleMap.put(rpcMethod.alias(), new RpcBean(value, method));
                }
            }
        });

    }


    private void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(5,5,5))
                                .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 0))
                                .addLast(new MyDecoder(MyRequest.class))
                                .addLast(new MyEncoder(MyResponse.class))
                                .addLast(new ServerHandler());
                    }
                }
        ).option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture future = bootstrap.bind("127.0.0.1", 8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void stop() {

    }

    protected static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }
}
