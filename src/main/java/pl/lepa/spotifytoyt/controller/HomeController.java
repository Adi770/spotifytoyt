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
import pl.lepa.spotifytoyt.service.MusicService;

@Controller
@Slf4j
@SessionAttributes({"tokenGoogle", "tokenSpotify", "spotifyList"})
public class HomeController {


    private static final String SPOTIFY_LIST = "spotifyList";
    private MusicService musicService;

    @Autowired
    public HomeController(MusicService musicService) {
        this.musicService = musicService;
    }

    private void getToken() {

    }

    @GetMapping("/home")
    public String getHomepage(Model model) {

        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute("tokenGoogle");
        OAuth2AuthenticationToken tokenSpotify = (OAuth2AuthenticationToken) model.getAttribute("tokenSpotify");

        try {
            log.info(tokenGoogle.getPrincipal().getAttribute("name"));
            model.addAttribute("youtube", tokenGoogle.getPrincipal().getAttribute("name"));
        } catch (NullPointerException e) {
            log.warn("Youtube token is null");
            return "redirect:/oauth2/authorization/google";
        }

        try {
            log.info(tokenSpotify.getPrincipal().getAttribute("display_name"));
            model.addAttribute("spotify", tokenSpotify.getPrincipal().getAttribute("display_name"));
        } catch (NullPointerException e) {
            log.warn("Spotify token is null");
            return "redirect:/oauth2/authorization/spotify";
        }
        if (!model.containsAttribute(SPOTIFY_LIST)) {
            return "redirect:/";
        }
        // model.addAttribute("spotifyList", "https://open.spotify.com/playlist/4eNyLaYTO6OfN62W54qUes?si=gMQSDTT2Qm2qL_o-61tvFA&nd=1");
        log.info(musicService.convertSpotifyToYoutube(model.getAttribute(SPOTIFY_LIST).toString(), model));

        return "homepage";


    }

    @GetMapping("/")
    public String getStart(Model model) {
        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute("tokenGoogle");
        OAuth2AuthenticationToken tokenSpotify = (OAuth2AuthenticationToken) model.getAttribute("tokenSpotify");

        if (tokenGoogle != null) {
            model.addAttribute("youtube", tokenGoogle.getPrincipal().getAttribute("name"));
        }
        if (tokenSpotify != null) {
            model.addAttribute("spotify", tokenSpotify.getPrincipal().getAttribute("display_name"));
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
        model.addAttribute("tokenGoogle", token);
        return "redirect:home";
    }

    @GetMapping("/Spotify")
    public String loginWithSpotifyAccount(Model model, OAuth2AuthenticationToken token) {
        log.info("token from spotify");
        model.addAttribute("tokenSpotify", token);
        return "redirect:home";

    }


}
