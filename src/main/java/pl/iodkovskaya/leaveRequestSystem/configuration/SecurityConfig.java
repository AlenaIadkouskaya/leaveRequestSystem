package pl.iodkovskaya.leaveRequestSystem.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.iodkovskaya.leaveRequestSystem.service.CustomUserDetailsService;

import static org.springframework.security.config.Customizer.withDefaults;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomOAuth2AuthenticationSuccessHandler successHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .requestMatchers("/", "/login**", "/oauth2/**").permitAll() // Разрешаем доступ к этим страницам без авторизации
//                .anyRequest().authenticated() // Все остальные запросы требуют авторизации
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .permitAll() // Разрешаем доступ к странице логина без авторизации
//                .and()
//                .httpBasic(); // Для базовой аутентификации (если нужно)
//
//        return http.build();
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/", "/login**", "/oauth2/**").permitAll()
                        .requestMatchers("/api/leave-requests/new").permitAll()
                        .requestMatchers("/api/leave-requests/all-for-user").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .anyRequest().authenticated()

                )
                .formLogin().loginPage("/login.html").permitAll()
                .and()
                .httpBasic(withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html")
                        .successHandler(successHandler)
                )
        ;

        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }


}
