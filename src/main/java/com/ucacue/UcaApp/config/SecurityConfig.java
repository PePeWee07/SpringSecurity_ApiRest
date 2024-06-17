package com.ucacue.UcaApp.config;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;

import com.ucacue.UcaApp.config.filter.JwtTokenValidator;
import com.ucacue.UcaApp.service.user.impl.UserServiceImpl;
import com.ucacue.UcaApp.util.token.CustomJwtAuthenticationEntryPoint;
import com.ucacue.UcaApp.util.token.JwtUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomJwtAuthenticationEntryPoint customjwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    // Configurar los endpoints públicos
                    http.requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll();

                    http.requestMatchers(HttpMethod.GET, "/api/v2/audit/**").permitAll();

                    http.requestMatchers(HttpMethod.GET, "/v3/**").permitAll();
                    http.requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll();

                    http.requestMatchers(HttpMethod.GET, "/auth/get").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();

                    http.requestMatchers(HttpMethod.GET, "/api/v2/**").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/api/v2/**").permitAll();
                    http.requestMatchers(HttpMethod.PUT, "/api/v2/**").permitAll();
                    http.requestMatchers(HttpMethod.DELETE, "/api/v2/**").permitAll();

                    // Configurar los endpoints por permisos
                    //http.requestMatchers(HttpMethod.POST, "/context-path").hasAnyRole("ADMIN", "DEVELOPER");
                    //http.requestMatchers(HttpMethod.PATCH, "/context-path").hasAnyAuthority("REFACTOR");

                    // Configurar el resto de endpoints - NO ESPECIFICADOS
                    http.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customjwtAuthenticationEntryPoint))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserServiceImpl userDetailService) {
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
        List<String> access = new ArrayList<>();
        access.add("*");
        config.setAllowedOriginPatterns(access);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(
                new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
