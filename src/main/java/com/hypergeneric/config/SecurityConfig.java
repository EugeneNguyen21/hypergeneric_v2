package com.hypergeneric.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain operatingConsoleFilterChain(HttpSecurity http) throws Exception {
        http
            .requestMatchers()
                .antMatchers("/operatingconsole/**")
                .and()
            .authorizeRequests()
                .antMatchers("/operatingconsole/login").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/operatingconsole/login")
                .loginProcessingUrl("/operatingconsole/login")
                .defaultSuccessUrl("/operatingconsole/index.html", true)
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/operatingconsole/logout")
                .logoutSuccessUrl("/operatingconsole/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            .and()
            .csrf().disable();
        
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        http
            .requestMatchers()
                .antMatchers("/h2-console/**", "/hypergeneric/**", "/static/**", "/favicon.ico")
                .and()
            .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/hypergeneric/**").permitAll()
                .antMatchers("/static/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .headers().frameOptions().disable();
        
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("adminpass"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 