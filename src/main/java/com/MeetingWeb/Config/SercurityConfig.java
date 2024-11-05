package com.MeetingWeb.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SercurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/img/**").permitAll()// 정적 리소스는 접근 허용
                .mvcMatchers("/home", "/start/**").permitAll()
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                .and()
                .formLogin()  // 기본 제공 로그인 페이지 사용
                .loginPage("/start/login") // 커스텀 로그인 페이지를 사용할 경우 설정 (기본 페이지 사용 시 생략 가능)
                .loginProcessingUrl("/login") // 로그인 폼에서 처리할 URL 설정 (default는 "/login")
                .usernameParameter("userName")
                .passwordParameter("password")
                .defaultSuccessUrl("/home", true)
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher( new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/home");

//        http.formLogin().disable();
        http.csrf().disable();




        return http.build();
    }
}
