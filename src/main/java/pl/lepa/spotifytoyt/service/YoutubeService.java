package pl.lepa.spotifytoyt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;
import pl.lepa.spotifytoyt.model.youtube.Youtube;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class YoutubeService {


    public static final String REGEX_START = Pattern.quote("playlist/");
    public static final String REGEX_END = Pattern.quote("?");
    static final String URL_YOUTUBE_SEARCH = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=";
    static final String URL_YOUTUBE_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlists";
    static final String URL_SPOTIFY_PLAYLIST = "https://api.spotify.com/v1/playlists";
    static final Pattern SPOTIFY_PATTERN = Pattern.compile(REGEX_START + "(.*?)" + REGEX_END);


    private RestTemplate restTemplate;
    private OAuth2AuthorizedClientService clientService;


    @Autowired
    public YoutubeService(RestTemplate restTemplate, OAuth2AuthorizedClientService clientService) {
        this.restTemplate = restTemplate;
        this.clientService = clientService;
    }

    public HttpHeaders addHeaders(OAuth2AuthenticationToken token) {

        OAuth2AuthorizedClient authorizedClient = null;
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

    public HttpEntity headersRequest(HttpHeaders headers) {
        return new HttpEntity(headers);
    }

    public ResponseEntity<String> getYoutubePlaylist(String url, Model model) {
        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute("tokenGoogle");
        return restTemplate.exchange("https://youtube.googleapis.com/youtube/v3/playlists?mine=true", HttpMethod.GET, headersRequest(addHeaders(tokenGoogle)), String.class);

    }

    public String findPlaylistId(String url) {
        Matcher matcher = SPOTIFY_PATTERN.matcher(url);
        if (matcher.find()) {
            log.info("playlist id " + matcher.group(1).toString());
            return matcher.group(1).toString();
        } else {
            log.warn("incorrect spotify url");
        }
        return url;
    }


    //TODO Working and above the same. create search link video
    public ResponseEntity<SpotifyPlaylistItems> getSpotifyPlaylist(String url, Model model) {
        OAuth2AuthenticationToken tokenSpotify = (OAuth2AuthenticationToken) model.getAttribute("tokenSpotify");
        String playlistID = "/" + findPlaylistId(url);
        ResponseEntity<SpotifyPlaylistItems> template = restTemplate.exchange(URL_SPOTIFY_PLAYLIST + playlistID + "/tracks", HttpMethod.GET, headersRequest(addHeaders(tokenSpotify)), SpotifyPlaylistItems.class);
        convertSpotifyToYoutubePlaylist(template.getBody(), model);
        return template;
    }

    public String convertSpotifyToYoutubePlaylist(SpotifyPlaylistItems spotifyPlaylist, Model model) {
        Set<String> videoIdList = new HashSet<>();
        for (pl.lepa.spotifytoyt.model.spotify.Item name : spotifyPlaylist.getItems()) {
//            videoIdList.add(findYoutubeClip(name.track.getName().toString(),model));
            for (int i = 0; i < 5; i++) {
                videoIdList.add(findYoutubeClip(name.track.getName().toString(), model));
            }
            break;
        }

        log.info(videoIdList.toString());
        return createYoutubePlaylist(videoIdList, model);
    }

    public String findYoutubeClip(String name, Model model) {

        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute("tokenGoogle");
        ResponseEntity<Youtube> entity = restTemplate.exchange(URL_YOUTUBE_SEARCH + name, HttpMethod.GET, headersRequest(addHeaders(tokenGoogle)), Youtube.class);
        return entity.getBody().items.get(0).id.getVideoId();
    }

    public String createYoutubePlaylist(Set<String> videoIdList, Model model) {
        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute("tokenGoogle");
       // ResponseEntity<String> entity = restTemplate.exchange(URL_SPOTIFY_PLAYLIST, HttpMethod.POST, headersRequest(addHeaders(tokenGoogle)), String.class, );

        return "";
    }


}
