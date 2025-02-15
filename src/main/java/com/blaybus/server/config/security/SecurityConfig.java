package com.blaybus.server.config.security;

import com.blaybus.server.config.security.jwt.JwtRequestFilter;
import com.blaybus.server.handler.OAuth2AuthenticationFailureHandler;
import com.blaybus.server.handler.OAuth2AuthenticationSuccessHandler;
import com.blaybus.server.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.blaybus.server.service.CustomOAuth2UserService;
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
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
            /** @brief auth */"/login/**", "/oauth2/**"
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
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

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
                .oauth2Login(configure ->
                        configure.authorizationEndpoint(config -> config.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                                .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(webConfig.corsFilter())
                .build();
    }
}
