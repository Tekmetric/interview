package com.interview.util;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBeans {

    @SuppressWarnings("unchecked")
    public static <T> T getBeanWithoutProxy(final ConfigurableApplicationContext applicationContext,
                                            final String beanName,
                                            final Class<T> clazz) {
        final var bean = (T) applicationContext.getBean(beanName);
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            try {
                return (T) ((Advised) bean).getTargetSource().getTarget();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
