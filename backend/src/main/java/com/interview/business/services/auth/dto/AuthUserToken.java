package com.interview.business.services.auth.dto;

import com.interview.business.domain.AppUser;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.ArrayList;

@Value
@EqualsAndHashCode(callSuper = true)
public class AuthUserToken extends AbstractAuthenticationToken {
    String id;
    String name;
    String email;

    public AuthUserToken(String id, String name, String email) {
        super(new ArrayList<>());
        setAuthenticated(true);
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public AuthUserToken(AppUser user) {
        super(new ArrayList<>());
        setAuthenticated(true);
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this;
    }

}
