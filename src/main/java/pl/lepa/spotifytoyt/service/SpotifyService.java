package pl.lepa.spotifytoyt.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;

@Service
@Slf4j
public class SpotifyService {

    static final String API_URL_SPOTIFY_PLAYLIST = "https://api.spotify.com/v1/playlists";

    private final RestTemplate restTemplate;
    private HttpHeaders spotifyHeaders;


    @Autowired
    public SpotifyService(RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }


    public SpotifyPlaylistItems getSpotifyPlaylist(String playlistId) {
        String playlistID = "/" +playlistId;
        ResponseEntity<SpotifyPlaylistItems> template = restTemplate.exchange(
                API_URL_SPOTIFY_PLAYLIST + playlistID + "/tracks",
                HttpMethod.GET,
                new HttpEntity<>(spotifyHeaders),
                SpotifyPlaylistItems.class);
        return template.getBody();
    }

    public void getToken(HttpHeaders headers) {
        this.spotifyHeaders = headers;
    }
}
