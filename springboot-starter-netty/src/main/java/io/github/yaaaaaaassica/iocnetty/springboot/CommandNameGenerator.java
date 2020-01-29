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

import io.github.yaaaaaaassica.iocnetty.annotation.NettyController;
import io.github.yaaaaaaassica.iocnetty.annotation.NettyMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;


@Slf4j
public class CommandNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        //从自定义注解中拿name
        String name = getNameByServiceFindAnntation(definition, registry);
        if (name != null) {
            return name;
        }
        //走父类的方法
        return super.generateBeanName(definition, registry);
    }

    private String getNameByServiceFindAnntation(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanClassName = definition.getBeanClassName();
        try {
            Class<?> aClass = Class.forName(beanClassName);
            NettyController commandController = aClass.getAnnotation(NettyController.class);
            if (commandController != null) {
                String className = aClass.getSimpleName();
                className = className.substring(0, 1).toLowerCase() + className.substring(1);
                return className;
            }
            NettyMapping commandMapping = aClass.getAnnotation(NettyMapping.class);
            if (commandMapping != null) {
                return beanClassName;
            }
            //获取到注解name的值并返回
            return null;
        } catch (ClassNotFoundException e) {
            log.error("getNameByServiceFindAnntation error:{}", beanClassName, e);
            return null;
        }
    }

}
