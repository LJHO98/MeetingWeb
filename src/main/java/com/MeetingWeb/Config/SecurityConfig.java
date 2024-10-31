package com.MeetingWeb.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

//        http.formLogin()
//                .loginPage("/member/signIn")
//                .defaultSuccessUrl("/")
//                .usernameParameter("userId")
//                .passwordParameter("password")
//                .failureUrl("/member/signIn/error")
//                .and()
//                .logout()
//                .logoutRequestMatcher( new AntPathRequestMatcher("/member/logout"))
//                .logoutSuccessUrl("/");
//
//        //인가,인증 ,  누구든 접근 허용주소 설정
//        http.authorizeRequests()
//                .mvcMatchers("/", "/member/**", "/images/**", "/item/**").permitAll()
//                .mvcMatchers("/css/**","/js/**", "/image/**").permitAll()
//                .mvcMatchers("/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated();
//
//        http.csrf()
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        http.formLogin().disable();  //기본 로그인 페이지 비활성화
        //get방식을 제외한 post방식은 csrf 토큰이 있어야된다.
        http.csrf().disable();  //csrf토큰 비활성화

        return http.build();
    }
}
