package pl.lepa.spotifytoyt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;
import pl.lepa.spotifytoyt.model.youtube.Youtube;
import pl.lepa.spotifytoyt.model.youtube.update.add.PlaylistItem;
import pl.lepa.spotifytoyt.model.youtube.update.add.ResourceId;
import pl.lepa.spotifytoyt.model.youtube.update.add.Snippet;
import pl.lepa.spotifytoyt.model.youtube.create.answer.Answer;
import pl.lepa.spotifytoyt.model.youtube.create.playlist.Playlist;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class YoutubeService {



    static final String API_URL_YOUTUBE_SEARCH = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=";
    static final String API_URL_YOUTUBE_PLAYLIST_ITEMS = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet";
    static final String API_URL_YOUTUBE_PLAYLIST = "https://youtube.googleapis.com/youtube/v3/playlists?part=snippet";
    static final String LINK_YOUTUBE_PLAYLIST="https://www.youtube.com/playlist?list=";

    private OAuth2AuthenticationToken tokenGoogle;

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientService clientService;
    private final ObjectMapper objectMapper;

    @Autowired
    public YoutubeService(RestTemplate restTemplate, OAuth2AuthorizedClientService clientService, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.clientService = clientService;
        this.objectMapper = objectMapper;
    }

    public HttpHeaders customHeaders(OAuth2AuthenticationToken token) {
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

    private void getToken(OAuth2AuthenticationToken tokenGoogle){
        this.tokenGoogle = tokenGoogle;
    }

    public ResponseEntity<String> getYoutubePlaylist(String url) {
        return restTemplate.exchange(API_URL_YOUTUBE_PLAYLIST, HttpMethod.GET, new HttpEntity<>(customHeaders(tokenGoogle)), String.class);
    }


    public String createSetYoutubeClipId(SpotifyPlaylistItems spotifyPlaylist,OAuth2AuthenticationToken tokenGoogle) {
        getToken(tokenGoogle);

        Set<String> videoIdList = new HashSet<>();
        for (pl.lepa.spotifytoyt.model.spotify.Item name : spotifyPlaylist.getItems()) {
            videoIdList.add(findYoutubeClip(name.track.getName()));
            break; //because consume to much google resource
        }
        log.info(videoIdList.toString());
        return createYoutubePlaylist(videoIdList);
    }

    public String findYoutubeClip(String name) {
        ResponseEntity<Youtube> entity = restTemplate.exchange(API_URL_YOUTUBE_SEARCH + name, HttpMethod.GET, new HttpEntity<>(customHeaders(this.tokenGoogle)), Youtube.class);
        return Objects.requireNonNull(entity.getBody()).items.get(0).id.getVideoId();
    }

    public String createYoutubePlaylist(Set<String> videoIdList) {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setKind("youtube#playlist");
        newPlaylist.setSnippet(
                new pl.lepa.spotifytoyt.model.youtube.create.playlist.Snippet
                        ("newPlaylist", "PL", "Playlist generate by SpotifyToYoutube"));

        String jsonObject ="";
        try {
            jsonObject = objectMapper.writeValueAsString(newPlaylist);

        } catch (RuntimeException | JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> request = new HttpEntity<>(jsonObject, customHeaders(this.tokenGoogle));
        ResponseEntity<Answer> entity=restTemplate.postForEntity(API_URL_YOUTUBE_PLAYLIST,request,Answer.class);


        return addToYoutubePlaylist(Objects.requireNonNull(entity.getBody()).id, videoIdList);
    }

    public String addToYoutubePlaylist(String id, Set<String> videoIdList) {

        PlaylistItem newPlaylistItem = new PlaylistItem();
        newPlaylistItem.setSnippet(new Snippet(id,new ResourceId("youtube#video", "")));
        String jsonObject ="";

        for (String videoId : videoIdList) {
            newPlaylistItem.getSnippet().getResourceId().setVideoId(videoId);
            try {
                jsonObject =objectMapper.writeValueAsString(newPlaylistItem);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            HttpEntity<String> request = new HttpEntity<>(jsonObject, customHeaders(this.tokenGoogle));
            restTemplate.postForEntity(API_URL_YOUTUBE_PLAYLIST_ITEMS,request,String.class);
        }
        return LINK_YOUTUBE_PLAYLIST+id;
    }


}
