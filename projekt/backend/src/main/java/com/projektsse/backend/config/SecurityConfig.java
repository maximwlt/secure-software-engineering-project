package com.projektsse.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    private final JwtFilter jwtFilter;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    SecurityConfig(JwtFilter jwtFilter,
                   CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                   CustomAccessDeniedHandler customAccessDeniedHandler
    ) {
        this.jwtFilter = jwtFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setCookieCustomizer(cookie -> {
            cookie.secure(cookieSecure);
            cookie.sameSite("Strict");
            cookie.path("/");
        });

        http
            .cors(AbstractHttpConfigurer::disable) // TODO: Set in production
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers(
                            "/api/auth/login",
                            "/api/auth/register",
                            "/api/auth/verify-email",
                            "/api/documents/public",
                            "/api/documents",
                            "/api/documents/public/search",
                            "/api/auth/forgot-password",
                            "/api/auth/reset-password",
                            "/api/auth/test"
                    )
                    .spa()
                    .csrfTokenRepository(repo)
            )
            .sessionManagement( session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                   .requestMatchers(
                           "/api/documents/public",
                           "/api/documents/public/search",
                            "/api/auth/register",
                            "/api/auth/login",
                            "/api/auth/verify-email",
                            "/api/auth/rt/refresh-token",
                            "/api/auth/rt/logout",
                           "/api/auth/forgot-password",
                           "/api/auth/reset-password",
                           "/api/auth/csrf",
                           "/api/auth/test"
                   ).permitAll().anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
