package com.huetterprojects.social_media_app_backend;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public record CustomUserDetails(
        String userId,
        String email,
        String password, // Das verschlüsselte Passwort aus der DB
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // In Spring Security ist der "Username" der eindeutige Identifier.
        // Da du die Email dafür nutzt, gibst du hier die Email zurück.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account ist nicht abgelaufen
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account ist nicht gesperrt
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Passwort/Credentials sind gültig
    }

    @Override
    public boolean isEnabled() {
        return true; // Account ist aktiv
    }
}