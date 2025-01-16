package org.example.melodymatch.system;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.melodymatch.account.AccountCredentials;
import org.example.melodymatch.account.AccountCredentialsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.example.melodymatch.system.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Collection;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final AccountCredentialsRepository accountCredentialsRepository;

    @Value("${security.jwt.cookieName}")
    private String cookieName;

    @Bean
    public UserDetailsService userDetailsService() {
        return phoneNumber -> buildUserDetailsFromCredentials(
                accountCredentialsRepository
                        .findByPhoneNumber_PhoneNumber(phoneNumber)
                        .orElseThrow(UserEmailNotFoundException::new)
        );
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(JwtDecoder jwtDecoder) {
        return new JwtAuthenticationProvider(jwtDecoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return;
            SecurityContextHolder.clearContext();
            HttpCookie cookie = ResponseCookie.from(cookieName, "").path("/").build();
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        };
    }

    private UserDetails buildUserDetailsFromCredentials(@Valid AccountCredentials accountCredentials) {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singleton((GrantedAuthority) () -> accountCredentials.getRole().name());
            }
            @Override
            public String getPassword() {
                return accountCredentials.getEncodedPassword().getEncodedPassword();
            }

            @Override
            public String getUsername() {
                return accountCredentials.getPhoneNumber().getPhoneNumber();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return !accountCredentials.getAccountLocked().isLocked();
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() { return true; }
        };
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }

}
