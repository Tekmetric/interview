package com.interview.business.services.users;

import com.interview.business.services.users.dto.SignInRequest;
import com.interview.business.services.users.dto.SignUpRequest;
import com.interview.core.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @Test
    public void storePasswordEncrypted() {
        var request = new SignUpRequest("Kaan", "kaan@email.com", "password", "AVATAR");

        var user = usersService.signUp(request);

        assertThat(user.password).isNotEqualTo("password");
    }

    @Test
    public void doNotAllowSignUpWithSameEmail() {
        var request = new SignUpRequest("Kaan", "kaan@email.com", "password", "AVATAR");

        var user = usersService.signUp(request);

        assertThat(user.password).isNotEqualTo("password");

        assertThrows(ApiException.class, () -> usersService.signUp(request));
    }

    @Test
    public void allowToSignInWithCorrectPassword() {
        var request = new SignUpRequest("Kaan", "kaan@email.com", "password", "AVATAR");

        var user = usersService.signUp(request);

        var signInReq = new SignInRequest(request.email(), request.password());

        var result = usersService.signIn(signInReq);

        assertThat(result).isPresent();
        assertThat(result.get().id).isEqualTo(user.id);
    }

    @Test
    public void doNotAllowToSignInWithWrongPassword() {
        var request = new SignUpRequest("Kaan", "kaan@email.com", "password", "AVATAR");

        usersService.signUp(request);

        var signInReq = new SignInRequest(request.email(), request.password() + "SOME_PASS");

        var result = usersService.signIn(signInReq);

        assertThat(result).isEmpty();
    }
}