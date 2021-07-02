package pl.lepa.spotifytoyt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import pl.lepa.spotifytoyt.service.MusicService;

import java.util.Objects;

@Controller
@Slf4j
@SessionAttributes({"tokenGoogle", "tokenSpotify", "spotifyList"})
public class HomeController {


    private static final String SPOTIFY_LIST = "spotifyList";
    private static final String TOKEN_GOOGLE = "tokenGoogle";
    private static final String TOKEN_SPOTIFY = "tokenSpotify";
    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private final MusicService musicService;

    @Autowired
    public HomeController(MusicService musicService) {
        this.musicService = musicService;
    }


    @GetMapping("/home")
    public String getHomepage(Model model) {
        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute(TOKEN_GOOGLE);
        OAuth2AuthenticationToken tokenSpotify = (OAuth2AuthenticationToken) model.getAttribute(TOKEN_SPOTIFY);

        try {
            log.info(tokenGoogle.getPrincipal().getAttribute(NAME));
            model.addAttribute("youtube", tokenGoogle.getPrincipal().getAttribute(NAME));
        } catch (NullPointerException e) {
            log.warn("Youtube token is null");
            return "redirect:/oauth2/authorization/google";
        }

        try {
            log.info(tokenSpotify.getPrincipal().getAttribute(DISPLAY_NAME));
            model.addAttribute("spotify", tokenSpotify.getPrincipal().getAttribute(DISPLAY_NAME));
        } catch (NullPointerException e) {
            log.warn("Spotify token is null");
            return "redirect:/oauth2/authorization/spotify";
        }

        if (model.getAttribute(SPOTIFY_LIST)=="") {
            return "redirect:/";
        }


        model.addAttribute(SPOTIFY_LIST, musicService.convertSpotifyToYoutube(Objects.requireNonNull(model.getAttribute(SPOTIFY_LIST)).toString(), model));
        return "homepage";
    }


    @GetMapping("/")
    public String getStart(Model model) {
        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute(TOKEN_GOOGLE);
        OAuth2AuthenticationToken tokenSpotify = (OAuth2AuthenticationToken) model.getAttribute(TOKEN_SPOTIFY);

        if (tokenGoogle != null) {
            model.addAttribute("youtube", tokenGoogle.getPrincipal().getAttribute(NAME));
        }
        if (tokenSpotify != null) {
            model.addAttribute("spotify", tokenSpotify.getPrincipal().getAttribute(DISPLAY_NAME));
        }

        return "homepage";
    }

    @PostMapping("/convertSpotify")
    public String convertPlaylist(Model model, @RequestParam String playlistToConvert) {
        model.addAttribute(SPOTIFY_LIST, playlistToConvert);
        return "redirect:home";
    }

    @GetMapping("/Google")
    public String loginWithGoogleAccount(Model model, OAuth2AuthenticationToken token) {
        log.info("token from google");
        model.addAttribute(TOKEN_GOOGLE, token);
        return "redirect:/";
    }

    @GetMapping("/Spotify")
    public String loginWithSpotifyAccount(Model model, OAuth2AuthenticationToken token) {
        log.info("token from spotify");
        model.addAttribute(TOKEN_SPOTIFY, token);
        return "redirect:/";

    }

    @GetMapping("/account/logout")
    public String logout(SessionStatus sessionStatus){
        sessionStatus.setComplete();
        return "redirect:/";
    }



}
