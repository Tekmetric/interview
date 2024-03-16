package com.interview.business.services.users;

import com.interview.business.domain.AppUser;
import com.interview.business.repositories.UsersRepository;
import com.interview.business.services.users.dto.SignInRequest;
import com.interview.business.services.users.dto.SignUpRequest;
import com.interview.core.exception.ApiException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository repository;

    public UsersService(UsersRepository repository) {
        this.repository = repository;
    }

    private String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    private boolean checkPassword(String password, String hashedPassword) {
        return new BCryptPasswordEncoder().matches(password, hashedPassword);
    }

    public boolean existsBy(String email) {
        return repository.existsByEmail(email);
    }

    public Optional<AppUser> signIn(SignInRequest dto) {
        final var user = repository.findOneByEmail(dto.email()).orElse(null);

        if (user == null) return Optional.empty();

        final var passwordMatches = checkPassword(dto.password(), user.password);

        if (!passwordMatches) return Optional.empty();

        return Optional.of(user);
    }

    @Transactional
    public AppUser signUp(SignUpRequest dto) {
        if (existsBy(dto.email())) {
            throw ApiException.userExists();
        }

        final var hashedPassword = hashPassword(dto.password());

        final var user = AppUser.builder()
                .name(dto.name())
                .email(dto.email())
                .password(hashedPassword)
                .avatar(dto.avatar())
                .createdAt(new Date())
                .build();

        return repository.save(user);
    }
}
