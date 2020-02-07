package io.github.spafka.springnetty.parsers;

import io.github.spafka.springnetty.annotation.ParserRegister;
import io.netty.channel.ChannelHandlerContext;

@ParserRegister(messageType = ChannelHandlerContext.class)
public class ChannelContextParser implements MessageParser<ChannelHandlerContext, ChannelHandlerContext> {
    @Override
    public void setParser(Class v) {

    }

    @Override
    public ChannelHandlerContext parse(ChannelHandlerContext ctx){
        return ctx;
    }
}
