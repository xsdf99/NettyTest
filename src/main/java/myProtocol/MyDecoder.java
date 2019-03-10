package myProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import utils.SerializationUtil;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    public MyDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            in.markReaderIndex();
            return;
        }

        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = SerializationUtil.deserialize(bytes, clazz);
        out.add(obj);
    }
}
