package com.oinkvalley.user_profile_svc.config;

import com.oinkvalley.user_profile_svc.security.JwtPrincipalConverter;
import com.oinkvalley.user_profile_svc.security.SecurityJsonHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtPrincipalConverter jwtPrincipalConverter,
            SecurityJsonHandlers securityJsonHandlers) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        securityJsonHandlers.writeUnauthorized(response))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        securityJsonHandlers.writeForbidden(response)));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers(HttpMethod.GET, "/profiles").permitAll()
                .requestMatchers(HttpMethod.GET, "/profiles/**").permitAll()
                .anyRequest().denyAll());
        http.oauth2ResourceServer(oauth2 -> oauth2
                .authenticationEntryPoint((request, response, authException) ->
                        securityJsonHandlers.writeInvalidToken(response))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        securityJsonHandlers.writeForbidden(response))
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtPrincipalConverter)));
        return http.build();
    }
}
