package com.interview.components;

import com.interview.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class UserRepoComponent {

  @Autowired
  private UserRepository userRepository;
  @Bean
  @Scope("singleton")
  public UserRepository getUserRepository() {
    return userRepository;
  }

}
