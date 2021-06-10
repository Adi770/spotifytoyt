package pl.lepa.spotifytoyt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SpotifyService {

    private static final String URL_SPOTIFY = "";

    private RestTemplate restTemplate;
    private OAuth2AuthorizedClientService clientService;


    public SpotifyService() {

    }

    public void getPlaylist(String url){

    }




}
