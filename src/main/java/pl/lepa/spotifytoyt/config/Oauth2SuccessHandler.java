package pl.lepa.spotifytoyt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    public Oauth2SuccessHandler() {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        String name = "default";

        log.info(authentication.toString());
        authentication.getDetails();
        name = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().toString();
        log.info(name);
        switch (name) {
            case "google":
                httpServletResponse.sendRedirect("/Google");
                break;
            case "spotify":
                httpServletResponse.sendRedirect("/Spotify");
                break;
            default:
                log.info("doesnt exist");
                httpServletResponse.sendRedirect("/home");
        }
    }
}
