package me.mugon.todolist.config;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.oauth.CustomOAuth2Provider;
import me.mugon.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.mugon.todolist.domain.enums.SocialType.KAKAO;

@Configuration
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST,"/users", "/login", "/users/checkEmail")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/js/**", "/css/**", "/images/**", "/fonts/**", "/users", "/login/**")
                        .permitAll()
                    .antMatchers("/kakao")
                        .hasAuthority(KAKAO.getRoleType())
                .anyRequest()
                    .authenticated()
                .and()
                    .oauth2Login()
                    .defaultSuccessUrl("/login/oauth/success")
                    .failureUrl("/loginFailure")
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and()
                    .formLogin()
                    .usernameParameter("email")
                    .loginPage("/login")
                    .defaultSuccessUrl("/login/success")
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                .and()
                    .addFilterBefore(filter, CsrfFilter.class)
                    .csrf()
                        .disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(@Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) {
        ClientRegistration clientRegistration = CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId)
                .clientSecret("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있다고 한다.
                .jwkSetUri("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있다고 한다.
                .build();

        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
}