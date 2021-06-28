package pl.lepa.spotifytoyt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;

@Service
@Slf4j
public class SpotifyService {


    static final String API_URL_SPOTIFY_PLAYLIST = "https://api.spotify.com/v1/playlists";


    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientService clientService;


    @Autowired
    public SpotifyService(RestTemplate restTemplate, OAuth2AuthorizedClientService clientService) {
        this.restTemplate = restTemplate;
        this.clientService = clientService;
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

    public SpotifyPlaylistItems getSpotifyPlaylist(String playlistId, HttpHeaders headers) {
        String playlistID = "/" +playlistId;
        ResponseEntity<SpotifyPlaylistItems> template = restTemplate.exchange(API_URL_SPOTIFY_PLAYLIST + playlistID + "/tracks", HttpMethod.GET, new HttpEntity<>(headers), SpotifyPlaylistItems.class);
        return template.getBody();
    }

}
