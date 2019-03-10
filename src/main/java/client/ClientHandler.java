package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import myProtocol.MyRequest;
import myProtocol.MyResponse;

import java.util.concurrent.TimeUnit;


public class ClientHandler extends SimpleChannelInboundHandler<MyResponse> {

    private RpcClient rpcClient;

    public ClientHandler(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                MyRequest myRequest = new MyRequest();
                myRequest.setType(1);
                ctx.writeAndFlush(myRequest);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyResponse msg) throws Exception {
        if (msg.getType() != 1) {
            try {
                ClientManager.taskMap.get(msg.getRequestId()).setResult(msg.getResult());
                ClientManager.taskQueue.put(msg.getRequestId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("断线重连");
        EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> {
            rpcClient.connect();
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("连接成功！");
        ClientManager.channel = ctx.channel();
        ClientManager.notifyChannel();
    }
}
