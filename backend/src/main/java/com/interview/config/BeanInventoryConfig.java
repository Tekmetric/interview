package com.interview.config;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * This class logs all bean names in the application context when the application starts. It is
 * useful for debugging and understanding the beans available in the Spring context.
 */
@ConditionalOnProperty(
    name = {"log.bean-names"},
    havingValue = "true"
)
@Configuration
public class BeanInventoryConfig {

  private static final Logger log = org.slf4j.LoggerFactory.getLogger(BeanInventoryConfig.class);

  private final ConfigurableApplicationContext applicationContext;

  public BeanInventoryConfig(ConfigurableApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @PostConstruct
  public void init() {
    String[] allBeanNames = applicationContext.getBeanDefinitionNames();
    AtomicInteger counter = new AtomicInteger();
    final String SPACE = " ";

    Arrays.stream(allBeanNames)
        .sorted()
        .map(beanName ->
            counter.incrementAndGet()
                + SPACE
                + beanName)
        .forEach(log::info);
  }

}
