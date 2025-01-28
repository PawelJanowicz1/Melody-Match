package org.example.melodymatch.system;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.melodymatch.account.AccountCredentials;
import org.example.melodymatch.account.AccountCredentialsRepository;
import org.example.melodymatch.system.exception.InvalidJwtException;
import org.example.melodymatch.system.value_object.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.example.melodymatch.system.exception.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;

@Component
@AllArgsConstructor
class LoginFilterConfig extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    private final JwtService jwtService;

    private final AccountCredentialsRepository accountCredentialsRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (!validateAuthHeader(request, response, filterChain, authHeader)) {
                return;
            }

            final var jwtFromHeader = Jwt.from(authHeader.substring(7));
            final var jwtPhoneNumber = jwtService.extractUserPhoneNumberFromJwt(jwtFromHeader);
            if (jwtPhoneNumber.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token format");
                return;
            }

            final var accountCredentials = getUserCredentials(jwtPhoneNumber.get());
            if (!jwtService.validateJwt(accountCredentials, jwtFromHeader)) {
                throw new InvalidJwtException();
            }

            final var role = jwtService.extractRoleFromJwt(jwtFromHeader);
            if (role.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Role not found in token");
                return;
            }

            UserDetails userDetails = User.builder()
                    .username(jwtPhoneNumber.get())
                    .authorities("ROLE_" + role.get())
                    .password("")
                    .build();

            setAuthTokenToSecurityContext(request, userDetails, accountCredentials);
            filterChain.doFilter(request, response);
        } catch (InvalidJwtException e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }

    private boolean validateAuthHeader(HttpServletRequest request,
                                       HttpServletResponse response,
                                       FilterChain filterChain,
                                       String authHeader) throws ServletException, IOException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return false;
        }
        return true;
    }

    private static void setAuthTokenToSecurityContext(HttpServletRequest request,
                                                      UserDetails userDetails,
                                                      AccountCredentials accountCredentials) {
        var authToken = new UsernamePasswordAuthenticationToken(accountCredentials, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private AccountCredentials getUserCredentials(String phoneNumber) {
        return accountCredentialsRepository.findByPhoneNumber_PhoneNumber(phoneNumber)
                .orElseThrow(UserEmailNotFoundException::new);
    }
}