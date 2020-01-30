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

package io.github.yaaaaaaassica.iocnetty.parsers;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.github.yaaaaaaassica.iocnetty.annotation.ParserRegister;
import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;


@ParserRegister(messageType = GeneratedMessageV3.class)
@Slf4j
public class ProtobufParser implements MessageParser<byte[], GeneratedMessageV3> {

    private Parser parser;

    @Override
    public void setParser(Class parameterType) {
        try {
            Field parserField = parameterType.getDeclaredField("PARSER");
            parserField.setAccessible(true);
            Parser parser = (Parser) parserField.get(parameterType);
            this.parser = parser;
        } catch (NoSuchFieldException e) {
            log.error("", e);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
    }

    @SneakyThrows
    @Override
    public GeneratedMessageV3 parse( byte[] bytes){
        return (GeneratedMessageV3) parser.parseFrom(bytes);
    }
}
