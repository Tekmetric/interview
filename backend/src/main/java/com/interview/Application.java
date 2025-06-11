package com.interview;

import static com.interview.util.LogUtil.logApplicationStartup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.interview.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class Application {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);
    Environment env = app.run(args).getEnvironment();
    logApplicationStartup(env);
  }

}
