package pl.lepa.spotifytoyt.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.service.YoutubeService;

@Controller
@Slf4j
@SessionAttributes({"tokenGoogle", "tokenSpotify"})
public class HomeController {


    private YoutubeService youtubeService;


    @Autowired
    public HomeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;

    }

    //TODO spotify and google works now create cookie to store
    //TODO session working. Now i must create cookies to save data and make request to api like getting playlist or creating playlist
    @GetMapping("/home")
    public String getHomepage(Model model) {

        OAuth2AuthenticationToken token2 = (OAuth2AuthenticationToken) model.getAttribute("tokenGoogle");
        OAuth2AuthenticationToken token3 = (OAuth2AuthenticationToken) model.getAttribute("tokenSpotify");

        try {
            log.info(token2.getPrincipal().getAttribute("name"));
            model.addAttribute("youtube", token2.getPrincipal().getAttribute("name"));
        } catch (NullPointerException e) {
            log.info("token is null");
        }

        try {
            log.info(token3.getPrincipal().getAttribute("display_name"));
            model.addAttribute("spotify", token3.getPrincipal().getAttribute("display_name"));
        } catch (NullPointerException e) {
            log.info("token is null");
        }

        model.addAttribute("spotifyList", null);

        try {

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity entity = new HttpEntity(youtubeService.addHeaders(token2));

            String url = "https://youtube.googleapis.com/youtube/v3/playlists?maxResults=25&mine=true";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            log.info(responseEntity.toString());

        } catch (NullPointerException e) {
            log.info("token is null");
        } finally {
            log.warn("test class");
            //log.info(youtubeService.getYoutubePlaylist("",model).toString());
            log.warn("test class");
            log.info(youtubeService.getSpotifyPlaylist("https://open.spotify.com/playlist/4eNyLaYTO6OfN62W54qUes?si=gMQSDTT2Qm2qL_o-61tvFA&nd=1",model).toString());
            //youtubeService.regex("https://open.spotify.com/playlist/4eNyLaYTO6OfN62W54qUes?si=gMQSDTT2Qm2qL_o-61tvFA&nd=1");
            return "homepage";
        }

    }

    @GetMapping("/")
    public String getMainPage() {
        return "homepage";
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
