package bamboo.config.security;

import bamboo.config.jwt.TokenFilter;
import bamboo.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@Slf4j
public class OAuthSecurityConfig {
    private final TokenProvider tokenProvider;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        log.info("[SecurityFilterChain] 실행");
        httpSecurity.cors().and().csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable();

        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeRequests()
                .antMatchers("/swagger-ui/index.html/**",
                        "/favicon.ico",
                        "/error",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**").permitAll()
                .antMatchers(HttpMethod.POST, "/user").permitAll()
                .antMatchers("/user/token", "/user/duplicate", "/user/signup", "/user/google/**").permitAll()
                .antMatchers("/**").authenticated()
                .anyRequest().permitAll();

        httpSecurity.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/user/**"));
        return httpSecurity.build();
    }

    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter(tokenProvider);
    }


}