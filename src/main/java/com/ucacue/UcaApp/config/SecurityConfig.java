package com.ucacue.UcaApp.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ucacue.UcaApp.config.filter.JwtTokenValidator;
import com.ucacue.UcaApp.exception.auth.CustomAccessDeniedHandler;
import com.ucacue.UcaApp.exception.token.CustomJwtAuthenticationEntryPoint;
import com.ucacue.UcaApp.repository.UserRepository;
import com.ucacue.UcaApp.service.admin.impl.AdminManagerServiceImpl;
import com.ucacue.UcaApp.service.token.TokenService;
import com.ucacue.UcaApp.util.token.JwtUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final SecurityProperties securityProperties;

    @Value("${swagger.enabled}")
    private boolean swaggerEnabled;

    public SecurityConfig(
            JwtUtils jwtUtils,
            CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            TokenService tokenService,
            UserRepository userRepository,
            SecurityProperties securityProperties) {
        this.jwtUtils = jwtUtils;
        this.customJwtAuthenticationEntryPoint = customJwtAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookieName(securityProperties.getCsrfCookie());
        csrfTokenRepository.setHeaderName(securityProperties.getCsrfHeader());
        csrfTokenRepository.setCookiePath(securityProperties.getCsrfCookiePath());

        httpSecurity
            .securityContext(securityContext -> securityContext.securityContextRepository(securityContextRepository()))
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
                .csrfTokenRepository(csrfTokenRepository))
            .sessionManagement(sessions -> sessions
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy()))
            .authorizeHttpRequests(http -> {
                http.requestMatchers(HttpMethod.GET, "/auth/**").permitAll();
                http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                http.requestMatchers(HttpMethod.GET, "/api/v1/catia/core/health").permitAll();

                if (swaggerEnabled) {
                    http.requestMatchers(HttpMethod.GET, "/apidoc/**").permitAll();
                } else {
                    http.requestMatchers(HttpMethod.GET, "/apidoc/api-docs").hasRole("ADMIN");
                    http.requestMatchers(HttpMethod.GET, "/apidoc/swagger-ui/**").denyAll();
                }

                http.anyRequest().authenticated();
            })
            .addFilterBefore(new JwtTokenValidator(jwtUtils, tokenService, userRepository),BasicAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(customJwtAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(AdminManagerServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
            "http://localhost:4200"
            // "https://tu-frontend-produccion.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(
            "Content-Type",
            "Authorization",
            securityProperties.getCsrfHeader()));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new NullSecurityContextRepository();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }
}
