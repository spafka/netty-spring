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

import io.github.spafka.springnetty.annotation.NettyController;
import io.github.spafka.springnetty.annotation.NettyMapping;
import io.github.spafka.springnetty.annotation.ParserRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Slf4j
public class CommandClassPathScanner extends ClassPathBeanDefinitionScanner {

    private static final List<Class> annotations = new ArrayList() {{
        add(NettyController.class);
        add(NettyMapping.class);
        add(ParserRegister.class);
    }};

    public CommandClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        log.debug("开始扫描包下所有BeanDefinitionHolder");

        for (Class annotation : annotations) {
            addIncludeFilter(new AnnotationTypeFilter(annotation));
        }

        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        log.debug("扫描包下所有BeanDefinitionHolder完成");

        return beanDefinitionHolders;
    }


    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        log.debug("isCandidateComponent --> {}", beanDefinition.getBeanClassName());

        Set<String> annotationTypes = beanDefinition.getMetadata().getAnnotationTypes();
        for (Class annotation : annotations) {
            addIncludeFilter(new AnnotationTypeFilter(annotation));
            if (annotationTypes.contains(annotation.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        log.debug("checkCandidate --> {}", beanName);

        return super.checkCandidate(beanName, beanDefinition);
    }

}