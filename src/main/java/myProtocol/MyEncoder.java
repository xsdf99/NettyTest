package myProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import utils.SerializationUtil;

public class MyEncoder extends MessageToByteEncoder {

    private Class<?> clazz;

    public MyEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (clazz.isInstance(msg)) {
            byte[] bytes = SerializationUtil.serialize(msg);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
