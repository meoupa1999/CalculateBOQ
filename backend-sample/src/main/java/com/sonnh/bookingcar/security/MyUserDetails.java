package com.sonnh.bookingcar.security;

import com.sonnh.bookingcar.data.domain.User;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public class MyUserDetails implements UserDetails {
    private final UUID id;
    private final String username;
    private final String password;
    private final boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    public MyUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername() != null ? user.getUsername() : user.getPhone();
        this.password = user.getPassword();
        this.isActive = user.getAudit().getIsActive();
        this.authorities = user.getRole() != null
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
                : Collections.emptyList();
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
        return isActive;
    }
}
