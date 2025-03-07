package com.interview.entity;

import com.interview.dto.RegistrationDto;
import com.interview.dto.UserDto;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
@Entity
@Table(name = "t_user")
public class User implements UserDetails, Serializable {

    public static final String AUTHORITY_NAME_ADMIN = "ADMIN";
    public static final String AUTHORITY_NAME_USER = "USER";
    public static final GrantedAuthority AUTHORITY_ADMIN = new SimpleGrantedAuthority(AUTHORITY_NAME_ADMIN);
    public static final GrantedAuthority AUTHORITY_USER = new SimpleGrantedAuthority(AUTHORITY_NAME_USER);

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean admin;


    public User(UserDto userDto) {
        this.id = userDto.getId();
        this.firstName = userDto.getFirstName();
        this.lastName = userDto.getLastName();
        this.email = userDto.getEmail();
        this.admin = userDto.isAdmin();
    }

    public User(RegistrationDto registrationDto) {
        this.firstName = registrationDto.getFirstName();
        this.lastName = registrationDto.getLastName();
        this.email = registrationDto.getEmail();
        this.password = registrationDto.getEncodedPassword();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(AUTHORITY_USER);
        if (this.admin) {
            authorities.add(AUTHORITY_ADMIN);
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
