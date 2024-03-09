package com.project.shop.configuations;

import com.project.shop.filters.JwtTokenFilter;
import com.project.shop.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
// Co nhiem vu kiem tra xem user quyen gi de vao
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    "api/v1/users/register",
                                    "api/v1/users/login",
                                    "api/v1/roles"
                            ).permitAll()

                            .requestMatchers(GET, "api/v1/categories").permitAll()
//                            .requestMatchers(POST, "api/v1/categories/**").hasRole(Role.ADMIN)
                            .requestMatchers(POST, "api/v1/categories").permitAll()
                            .requestMatchers(PUT, "api/v1/categories/**").hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, "api/v1/categories/**").hasRole(Role.ADMIN)
                            .requestMatchers(GET, "api/v1/products**").permitAll()
                            .requestMatchers(GET, "api/v1/products/*").permitAll()
                            .requestMatchers(GET, "api/v1/products/images/*").permitAll()
//                            .requestMatchers(POST, "api/v1/products/**").hasRole(Role.ADMIN)
                            .requestMatchers(POST, "api/v1/products").permitAll()
                            .requestMatchers(POST, "api/v1/products/**").permitAll()
                            .requestMatchers(PUT, "api/v1/products/**").hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, "api/v1/products/**").hasRole(Role.ADMIN)

                            .requestMatchers(GET, "api/v1/orders/**").permitAll()
                            .requestMatchers(POST, "api/v1/orders/**").hasRole(Role.USER)
                            .requestMatchers(PUT, "api/v1/orders/**").hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, "api/v1/orders/**").hasRole(Role.ADMIN)

                            .requestMatchers(GET, "api/v1/order_details/**").hasAnyRole(Role.USER,Role.ADMIN)
                            .requestMatchers(POST, "api/v1/order_details/**").hasRole(Role.USER)
                            .requestMatchers(PUT, "api/v1/order_details/**").hasRole(Role.ADMIN)



                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization","content-type","x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**",configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
