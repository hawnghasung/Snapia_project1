package com.snapia.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {

        security.csrf(csrf -> csrf.disable());

        security.authorizeHttpRequests(auth -> auth
                // 1. 컨트롤러에 있는 주소 그대로 허용
                .requestMatchers("/join", "/login", "/main").permitAll()
                // 2. 정적 리소스(CSS, JS 등) 허용 (이게 없으면 화면이 깨질 수 있어요)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/ws/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                // 3. 나머지는 로그인해야 접근 가능
                .anyRequest().authenticated()
        );

        security.formLogin(form -> form
                // 컨트롤러의 @GetMapping("/login")과 일치시킴
                .loginPage("/login")
                // HTML 폼의 action 주소와 일치시킴
                .loginProcessingUrl("/login")
                .usernameParameter("loginId")
                .defaultSuccessUrl("/main", true)
                .permitAll()
        );

        security.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/main")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        return security.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}