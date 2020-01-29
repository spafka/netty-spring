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

package io.github.yaaaaaaassica.iocnetty.springboot;

import io.netty.channel.ChannelOption;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.NetworkInterface;


@ConfigurationProperties(prefix = "spring.boot.netty")
@Data
public class SpringBootNettyProperties {

    /********************************************************************
     ***********************Netty 服务配置参数*****************************
     ********************************************************************/
    private Integer PORT = 8081;
    private int readerIdleTimeSeconds = 5;
    private int writerIdleTimeSeconds = 5;
    private int allIdleTimeSeconds = 5;
    private int bossGroupThreadSize = 1;
    private int workGroupThreadSize = 1;


    /********************************************************************
     **************Netty #{@link ChannelOption} 配置参数******************
     ********************************************************************/
    /**
     * #{@link ChannelOption#ALLOCATOR}
     */
    private String allocator = null;

    /**
     * #{@link ChannelOption#RCVBUF_ALLOCATOR}
     */
    private String rcvbufAllocator = null;

    /**
     * #{@link ChannelOption#MESSAGE_SIZE_ESTIMATOR}
     */
    private String messageSizeEstimator = null;

    /**
     * #{@link ChannelOption#CONNECT_TIMEOUT_MILLIS}
     */
    private Integer connectTimeoutMillis = null;

    /**
     * #{@link ChannelOption#MAX_MESSAGES_PER_READ}
     */
    private Integer maxMessagesPerRead = null;

    /**
     * #{@link ChannelOption#WRITE_SPIN_COUNT}
     */
    private Integer writeSpinCount = null;

    /**
     * #{@link ChannelOption#WRITE_BUFFER_HIGH_WATER_MARK}
     */
    private Integer writeBufferHighWaterMark = null;

    /**
     * #{@link ChannelOption#WRITE_BUFFER_LOW_WATER_MARK}
     */
    private Integer writeBufferLowWaterMark = null;

    /**
     * #{@link ChannelOption#ALLOW_HALF_CLOSURE}
     */
    private Boolean allowHalfClosure = null;

    /**
     * #{@link ChannelOption#AUTO_READ}
     */
    private Boolean autoRead = null;

    /**
     * #{@link ChannelOption#SO_BROADCAST}
     */
    private Boolean soBroadcast = null;

    /**
     * #{@link ChannelOption#SO_KEEPALIVE}
     */
    private Boolean soKeepalive = null;

    /**
     * #{@link ChannelOption#SO_SNDBUF}
     */
    private Integer soSndbuf = null;

    /**
     * #{@link ChannelOption#SO_RCVBUF}
     */
    private Integer soRcvbuf = null;

    /**
     * #{@link ChannelOption#SO_REUSEADDR}
     */
    private Boolean soReuseaddr = null;

    /**
     * #{@link ChannelOption#SO_LINGER}
     */
    private Integer soLinger = null;

    /**
     * #{@link ChannelOption#SO_BACKLOG}
     */
    private Integer soBacklog = null;

    /**
     * #{@link ChannelOption#SO_TIMEOUT}
     */
    private Integer soTimeout = null;

    /**
     * #{@link ChannelOption#IP_TOS}
     */
    private Integer ipTos = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_ADDR}
     */
    private InetAddress ipMulticastAddr = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_IF}
     */
    private NetworkInterface ipMulticastIf = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_TTL}
     */
    private Integer ipMulticastTtl = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_LOOP_DISABLED}
     */
    private Boolean ipMulticastLoopDisabled = null;

    /**
     * #{@link ChannelOption#TCP_NODELAY}
     */
    private Boolean tcpNodelay = null;

    /**
     * #{@link ChannelOption#SINGLE_EVENTEXECUTOR_PER_GROUP}
     */
    private Boolean singleEventexecutorPerGroup = null;


}
