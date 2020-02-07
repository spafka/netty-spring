/*
 *
 * Copyright 2009-2020 the original author Yaaaaaassica(HMJ fucker).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.spafka.springnetty.netty;

import io.github.spafka.springnetty.annotation.NettyController;
import io.github.spafka.springnetty.annotation.NettyMapping;
import io.github.spafka.springnetty.annotation.NettyResponseBody;
import io.github.spafka.springnetty.annotation.ParserRegister;
import io.github.spafka.springnetty.parsers.ChannelContextParser;
import io.github.spafka.springnetty.parsers.MessageParser;
import io.github.spafka.springnetty.util.SpringProxyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Component
@ChannelHandler.Sharable
@Slf4j
public class NettyCommandHandler extends ChannelDuplexHandler implements InitializingBean, DisposableBean, ApplicationContextAware {

    ApplicationContext beanFactory;

    ConcurrentMap<String, HandlerMethod> methodMappings;
    ConcurrentMap<Class, MessageParser> parsers;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage nettyMessage = (NettyMessage) msg;

        try {
            dispatch(ctx, nettyMessage.getMessageId(), nettyMessage.getBuf());
        }catch (Throwable e){
            log.error("swall a error {}",e);
            throw  e;
        }


    }

    private void dispatch(ChannelHandlerContext ctx, int messageId, Object messageBytes) throws Exception {

        HandlerMethod handlerMethod = methodMappings.get(messageId + "");

        if (handlerMethod == null) {
            log.warn("ignore unknow message{}", messageId);
            throw new RuntimeException(String.format("unknow message %s",messageId));
        }

        // 调用方法
        Method method = handlerMethod.method;

        Parameter[] parameters = method.getParameters();

        Object[] params = Arrays.
                stream(parameters)
                .map(x -> parsers.get(x.getParameterizedType()))
                .map(x -> {
                    if (x.equals(ChannelContextParser.class)) {
                        return x.parse(ctx);
                    } else {
                        return x.parse(messageBytes);
                    }
                })
                .toArray();


        Object result = method.invoke(handlerMethod.bean, params);


        if (method.isAnnotationPresent(NettyResponseBody.class) && method.getReturnType() != null) {
            NettyResponseBody responseBody = method.getAnnotation(NettyResponseBody.class);
            Class<? extends NettyResponseBody> bodyClass = responseBody.getClass();

            if (method.getReturnType() != Void.TYPE) {
                if (result != null) {

                    try {
                        MessageParser messageParser = parsers.get(bodyClass);
                        Object parse = messageParser.parse(result);

                        if (ctx.channel().isWritable()) {
                            ctx.writeAndFlush(parse);
                        } else {
                            log.error("channel is not writable");
                            throw new RuntimeException("channel is not writable");
                        }

                    } catch (Exception e) {
                        log.error("{}", ExceptionUtils.getStackTrace(e));
                        throw e;
                    }
                }
            }

        }


    }

    /**
     * 生成调用  #{@link NettyMapping} 方法的参数.
     * 目前只支持Protobuf参数和 #{@link ChannelHandlerContext}
     *
     * @param messageBytes
     * @param messageParsers
     * @return
     */
    private List getParameters(byte[] messageBytes, List<MessageParser> messageParsers) throws Exception {
        List paramters = new ArrayList();

        for (MessageParser messageParser : messageParsers) {
            paramters.add(messageParser.parse(messageBytes));
        }

        return paramters;
    }

    /**
     * 生成调用  #{@link NettyMapping} 方法的参数.
     * 目前只支持Protobuf参数和 #{@link ChannelHandlerContext}
     *
     * @param messageBytes
     * @param messageParsers
     * @return
     */
    private List getParameters(ByteBuf messageBytes, List<MessageParser> messageParsers) throws Exception {
        List paramters = new ArrayList();

        for (MessageParser messageParser : messageParsers) {
            paramters.add(messageParser.parse(messageBytes));
        }
        return paramters;
    }


    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {


        methodMappings = new ConcurrentHashMap<>(16);
        parsers = new ConcurrentHashMap<>(16);

        String[] nettyControlers = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(beanFactory, NettyController.class);


        // 初始化methodResolver
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(ParserRegister.class);


        beansWithAnnotation.forEach((n, beanObject) -> {

            Class<?> klass = null;
            try {
                klass = SpringProxyUtils.findTargetClass(beanObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ParserRegister annotation = klass.getAnnotation(ParserRegister.class);

            Class<? extends ParserRegister> messageType = annotation.messageType();

            parsers.put(messageType, (MessageParser) beanObject);


        });

        // 初始化controller
        for (String controler : nettyControlers) {

            Object bean = beanFactory.getBean(controler);

            Class<?> aClass = SpringProxyUtils.findTargetClass(bean);
            // 初始化methodhandler
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                ReflectionUtils.makeAccessible(method);
                boolean present = method.isAnnotationPresent(NettyMapping.class);

                if (present) {
                    NettyMapping annotation = method.getAnnotation(NettyMapping.class);

                    long id = annotation.id();


                    Class<?>[] parameterTypes = method.getParameterTypes();
                    List<MessageParser> parsers = new ArrayList<>();

                    //
                    for (Class<?> parameterType : parameterTypes) {
                        MessageParser parser = this.parsers.get(parameterType);
                        if (parser == null) {
                            throw new RuntimeException(" Can not find parser for " + parameterType.getSimpleName());
                        }
                        parsers.add(parser);
                    }

                    methodMappings.put(id + "", HandlerMethod.builder().bean(bean).parsers(parsers).method(method).build());

                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {


        beanFactory = applicationContext;
    }

    @Builder
    static class HandlerMethod {

        Object bean;
        Method method;

        List<MessageParser> parsers;


    }
}
