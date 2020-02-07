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

import io.github.spafka.springnetty.annotation.NettyScan;
import io.github.spafka.springnetty.annotation.ParserRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class CommandScannerRegistrar implements ResourceLoaderAware, ImportBeanDefinitionRegistrar {

    private static final List<String> annotationPackages = new ArrayList() {{
        add(ParserRegister.class.getPackage().getName());
    }};

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        log.debug("registerBeanDefinitions start: {}", annotationMetadata.getClassName());
        String[] scanPackages = getScanPackages(annotationMetadata);

        //自定义的包扫描器
        CommandClassPathScanner commandClassPathScanner = new CommandClassPathScanner(beanDefinitionRegistry, true);

        if (resourceLoader != null) {
            commandClassPathScanner.setResourceLoader(resourceLoader);
        }

        //这里实现的是根据名称来注入
        commandClassPathScanner.setBeanNameGenerator(new CommandNameGenerator());

        log.info("commandClassPathScanner 扫描路径:{}",Arrays.toString(scanPackages));

        //扫描指定路径下的接口
        Set<BeanDefinitionHolder> beanDefinitionHolders = commandClassPathScanner.doScan(scanPackages);

        String beanNames = beanDefinitionHolders.stream().map(it -> it.getBeanName()).collect(Collectors.joining(", "));
        log.info("commandClassPathScanner 扫描到的bean名称:{}", beanNames);

    }

    private String[] getScanPackages(AnnotationMetadata annotationMetadata) {
        //获取所有注解的属性和值
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(NettyScan.class.getName()));
        //获取到basePackage的值
        String[] basePackages = annoAttrs.getStringArray("basePackage");
        //如果没有设置basePackage 扫描路径,就扫描对应包下面的值
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }

        List<String> scanPackages = new ArrayList<>();
        scanPackages.addAll(Arrays.asList(basePackages));
        scanPackages.addAll(annotationPackages);

        String[] packages = new String[scanPackages.size()];
        for (int i = 0; i < scanPackages.size(); i++) {
            packages[i] = scanPackages.get(i);
        }
        return packages;
    }


}
