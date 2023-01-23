package com.interview.repository;

import com.interview.model.Token;
import com.interview.model.TokenType;
import com.interview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByUserAndTokenType(User user, TokenType tokenType);

    Optional<Token> findByValueAndTokenType(String value, TokenType tokenType);

}
