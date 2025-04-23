package io.muzoo.ssc.project.backend.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.auth.OurUserDetailsService;
import io.muzoo.ssc.project.backend.util.AjaxUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for REST APIs and configure security
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/login", "/api/logout", "/api/whoami", "/api/register").permitAll() // Allow specific paths without authentication
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow OPTIONS requests

                        // Allow public access to user profile pictures
                        .requestMatchers(HttpMethod.GET, "/api/users/*/profile-picture").permitAll()
                        // Team endpoints
                        .requestMatchers(HttpMethod.GET, "/api/teams/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/teams/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/teams/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/teams/**").authenticated()
                        .anyRequest().authenticated() // Require authentication for any other path
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JsonHttp403ForbiddenEntryPoint()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Use updated CORS configuration

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(OurUserDetailsService ourUserDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(ourUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService(OurUserDetailsService ourUserDetailsService) {
        return ourUserDetailsService;
    }

    // CORS configuration for Spring Security 6.x
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "https://unitalk.live", "http://localhost:8081", "https://unitalk.live/api","https://unitalk.live/api/login", "https://unitalk.live/api/register", "https://unitalk.live/login","https://unitalk.live/register")); // Allowed origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE")); // Allowed methods
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization")); // Allowed headers
        configuration.setAllowCredentials(true); // Allow credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply the CORS settings to all paths
        return source;
    }

    class JsonHttp403ForbiddenEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            String ajaxJson = AjaxUtils.convertToString(SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message("You need to log in to perform this action.")
                    .build()
            );
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().println(ajaxJson);
        }
    }
}