package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import myProtocol.MyRequest;
import myProtocol.MyResponse;

import java.lang.reflect.InvocationTargetException;

public class ServerHandler extends SimpleChannelInboundHandler<MyRequest> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        super.userEventTriggered(ctx, evt);
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state().equals(IdleState.READER_IDLE)){
                System.out.println("服务端长期未收到客户端请求");
                ctx.channel().close();
            }else if(event.state().equals(IdleState.WRITER_IDLE)){
                System.out.println("服务端长期未发送消息");
            }else if(event.state().equals(IdleState.ALL_IDLE)){
                System.out.println("服务端长期不动");
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyRequest msg) throws Exception {
        RpcServer.submit(() -> {
            MyResponse myResponse = new MyResponse();
            myResponse.setRequestId(msg.getRequestId());
            Object obj = handle(msg);
            System.out.println("server return :" + obj);
            myResponse.setResult(obj);
            ctx.writeAndFlush(myResponse);
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("client close!!!!");
    }

    private Object handle(MyRequest myRequest) {
        RpcBean rpcBean = ServerManager.handleMap.get(myRequest.getRpcConstant());
        if (rpcBean == null) {
            return null;
        }

        try {

            return rpcBean.getMethod().invoke(rpcBean.getClazz(), myRequest.getParams());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}

