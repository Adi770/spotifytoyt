package pl.lepa.spotifytoyt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.lepa.spotifytoyt.exceptions.UrlException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class MusicService {

    private static final String REGEX_START = Pattern.quote("playlist/");
    private static final String REGEX_END = Pattern.quote("?");
    private static final Pattern SPOTIFY_PATTERN = Pattern.compile(REGEX_START + "(.*?)" + REGEX_END);
    private static final String TOKEN_GOOGLE = "tokenGoogle";
    private static final String TOKEN_SPOTIFY = "tokenSpotify";
    private YoutubeService youtubeService;
    private SpotifyService spotifyService;
    private OAuth2AuthenticationToken tokenSpotify;
    private OAuth2AuthenticationToken tokenGoogle;

    private final OAuth2AuthorizedClientService clientService;


    @Autowired
    public MusicService(YoutubeService youtubeService, SpotifyService spotifyService, OAuth2AuthorizedClientService clientService) {
        this.youtubeService = youtubeService;
        this.spotifyService = spotifyService;
        this.clientService = clientService;
    }

    public void getTokenFromSession(Model model) {
        this.tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute(TOKEN_GOOGLE);
        this.tokenSpotify = (OAuth2AuthenticationToken) model.getAttribute(TOKEN_SPOTIFY);
    }

    public HttpHeaders customHeaders(OAuth2AuthenticationToken token) {
        OAuth2AuthorizedClient authorizedClient;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            authorizedClient = this.clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
            headers.set("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue());
        } catch (NullPointerException e) {
            log.info("token is null");
        }
        return headers;
    }


    public String findPlaylistId(String url) {
        Matcher matcher = SPOTIFY_PATTERN.matcher(url);
        if (matcher.find()) {
            log.info("playlist id " + matcher.group(1));
            return matcher.group(1);
        } else {
            log.warn("incorrect url");
            throw new UrlException("Problem with playlist url");
        }
    }

    public String convertSpotifyToYoutube(String url,Model model) {
       getTokenFromSession(model);
       return getMusicPlaylist(findPlaylistId(url));
    }

    public String getMusicPlaylist(String playlistId) {
      return youtubeService.createSetYoutubeClipId(spotifyService.getSpotifyPlaylist(playlistId, customHeaders(this.tokenSpotify)),customHeaders(this.tokenGoogle));

    }
}
