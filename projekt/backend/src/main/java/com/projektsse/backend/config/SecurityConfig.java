package com.projektsse.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.csrf.CsrfFilter;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            //.cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf
                  .requireCsrfProtectionMatcher(new RequestMatcher() {
                      @Override
                      public boolean matches(@NonNull HttpServletRequest request) {
                          String uri = request.getRequestURI();
                          return uri.equals("/api/auth/refresh-token") || uri.equals("/api/auth/logout");
                      }
                  })
                  .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                  .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            .sessionManagement( session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    })
            )
            .authorizeHttpRequests(auth -> auth
                   .requestMatchers(
                           "/api/documents/public",
                           "/api/documents/public/search",
                            "/api/auth/register",
                            "/api/auth/login",
                            "/api/auth/verify-email",
                            "/api/auth/refresh-token",
                            "/api/auth/logout"
                   ).permitAll().anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            // .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
