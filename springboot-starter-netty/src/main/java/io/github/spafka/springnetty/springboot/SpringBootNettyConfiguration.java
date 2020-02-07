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

package io.github.spafka.springnetty.springboot;

import io.github.spafka.springnetty.netty.NettyCommandHandler;
import io.github.spafka.springnetty.netty.NettyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Configuration
@EnableConfigurationProperties(SpringBootNettyProperties.class)
@Slf4j
public class SpringBootNettyConfiguration {

    @Autowired
    private SpringBootNettyProperties properties;


    /**
     * springboot 启动时自动加载NettyStarter, 例如CommandLineRunner 启动netty服务器.
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(NettyStarter.class)
    public NettyStarter nettyStarter() {
        return new NettyStarter();
    }

    @Bean
    @ConditionalOnMissingBean(NettyCommandHandler.class)
    public NettyCommandHandler dispatchHander() {

        return new NettyCommandHandler();
    }

    @Deprecated
    @ConditionalOnBean(NettyCommandHandler.class)
    @ConditionalOnMissingBean({NettyServer.class})
    @Bean
    public NettyServer nettyServer() throws IllegalAccessException, InstantiationException {

        // fixme
        return new NettyServer() {
            @Override
            public void start() {
                log.info("Netty Server starting...");

                try {
                    EventLoopGroup bossGroup = new NioEventLoopGroup(properties.getBossGroupThreadSize());
                    EventLoopGroup workerGroup = new NioEventLoopGroup(properties.getWorkGroupThreadSize());
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .handler(new LoggingHandler(LogLevel.DEBUG))
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel ch) {
                                     ch.pipeline().addLast("dummy",new ChannelDuplexHandler(){

                                     });
                                }
                            });

                    int port = properties.getPORT();
                    b.bind("localhost", port).sync();

                    log.info("Netty Server listening at:{}", port);


                } catch (InterruptedException e) {
                    log.error("", e);
                    stop();
                }
            }

            @Override
            public void stop() {

            }
        };
    }


    @ConditionalOnBean(NettyServer.class)
    @Component
    public static class NettyStarter implements InitializingBean, DisposableBean {

        @Resource
        private SpringBootNettyProperties springBootNettyProperties;

        @Autowired
        private NettyServer nettyServer;

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("Starting The Netty Server");
            nettyServer.start();
            log.info("Started The Netty Server");
        }

        @Override
        public void destroy() throws Exception {
            log.info("Stopping The Netty Server");

            nettyServer.stop();

            log.info("Stopped The Netty Server");
        }


    }

}
