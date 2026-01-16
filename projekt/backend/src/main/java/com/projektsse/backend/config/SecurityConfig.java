package com.projektsse.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers(
                            "/api/auth/login",
                            "/api/auth/register",
                            "/api/auth/verify-email",
                            "/api/documents/public",
                            "/api/documents/public/search"
                    )
                  .spa()
            )
            .sessionManagement( session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                    .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
            )
            .authorizeHttpRequests(auth -> auth
                   .requestMatchers(
                           "/api/documents/public",
                           "/api/documents/public/search",
                            "/api/auth/register",
                            "/api/auth/login",
                            "/api/auth/verify-email",
                            "/api/auth/csrf",
                            "/api/auth/rt/refresh-token",
                            "/api/auth/rt/logout"
                   ).permitAll().anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            //.addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
            // .addFilterBefore(new CsrfCookieFilter(), CsrfFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
