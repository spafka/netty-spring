package io.github.yaaaaaaassica.iocnetty.parsers;

import io.github.yaaaaaaassica.iocnetty.annotation.ParserRegister;
import io.netty.channel.ChannelHandlerContext;

@ParserRegister(messageType = ChannelHandlerContext.class)
public class ChannelContextParser implements MessageParser<ChannelHandlerContext, ChannelHandlerContext> {
    @Override
    public void setParser(Class v) {

    }

    @Override
    public ChannelHandlerContext parse(ChannelHandlerContext ctx) throws Exception {
        return ctx;
    }
}
