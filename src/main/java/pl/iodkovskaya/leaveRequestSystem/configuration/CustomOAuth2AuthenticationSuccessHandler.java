package pl.iodkovskaya.leaveRequestSystem.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.iodkovskaya.leaveRequestSystem.service.CustomUserDetailsService;
import pl.iodkovskaya.leaveRequestSystem.service.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String login = oAuth2User.getAttribute("login");

        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(login);
        } catch (UsernameNotFoundException | NullPointerException e) {
            userService.registerOAuth2User(login);
        }


        response.sendRedirect("/");

    }


}
