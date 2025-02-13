package com.blaybus.server.config.security;

import com.blaybus.server.config.security.jwt.JwtRequestFilter;
import com.blaybus.server.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /* 권한 제외 대상 */
    private static final String[] permitAllUrl = new String[]{
            /** @brief Swagger Docs*/"/v3/api-docs/**", "/swagger-ui/**",
            /** @brief Retrieve status*/ "/status/all",
            /** @brief auth */"/auth/**",
            /** @brief auth */"/test/**",
    };
    /* Admin 접근 권한 */
    private static final String[] permitAdminUrl = new String[]{
            /** @brief Check Access Admin */ "/status/admin",
    };
    /* member 접근 권한 */
    private static final String[] permitMemberUrl = new String[]{
    };

    private final JwtRequestFilter filter;
    private final WebConfig webConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(permitAllUrl)
                            .permitAll();
                    auth.requestMatchers(permitAdminUrl)
                            .hasRole("ADMIN");
                    auth.anyRequest()
                            .authenticated();
                })
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(webConfig.corsFilter())
                .build();
    }
}
