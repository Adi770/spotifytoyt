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
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;
import pl.lepa.spotifytoyt.model.youtube.Youtube;
import pl.lepa.spotifytoyt.model.youtube.add.PlaylistItem;
import pl.lepa.spotifytoyt.model.youtube.add.ResourceId;
import pl.lepa.spotifytoyt.model.youtube.add.Snippet;
import pl.lepa.spotifytoyt.model.youtube.create.answer.Answer;
import pl.lepa.spotifytoyt.model.youtube.create.playlist.Playlist;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class YoutubeService {



    static final String URL_YOUTUBE_SEARCH = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=";
    static final String URL_YOUTUBE_PLAYLIST_ITEMS = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet";
    static final String URL_YOUTUBE_PLAYLIST = "https://youtube.googleapis.com/youtube/v3/playlists?part=snippet";
    static final String URL_SPOTIFY_PLAYLIST = "https://api.spotify.com/v1/playlists";
    static final String LINK_YOUTUBE_PLAYLIST="https://www.youtube.com/playlist?list=";

    public static final String REGEX_START = Pattern.quote("playlist/");
    public static final String REGEX_END = Pattern.quote("?");
    static final Pattern SPOTIFY_PATTERN = Pattern.compile(REGEX_START + "(.*?)" + REGEX_END);


    private RestTemplate restTemplate;
    private OAuth2AuthorizedClientService clientService;
    private ObjectMapper objectMapper;

    @Autowired
    public YoutubeService(RestTemplate restTemplate, OAuth2AuthorizedClientService clientService,ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.clientService = clientService;
        this.objectMapper=objectMapper;
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
        return restTemplate.exchange(URL_YOUTUBE_PLAYLIST, HttpMethod.GET, headersRequest(addHeaders(tokenGoogle)), String.class);

    }

    public String findPlaylistId(String url) {
        Matcher matcher = SPOTIFY_PATTERN.matcher(url);
        if (matcher.find()) {
            log.info("playlist id " + matcher.group(1).toString());
            return matcher.group(1).toString();
        } else {
            log.warn("incorrect url");
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
            videoIdList.add(findYoutubeClip(name.track.getName(), model));
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

        Playlist newPlaylist = new Playlist();
        newPlaylist.setKind("youtube#playlist");
        newPlaylist.setSnippet(new pl.lepa.spotifytoyt.model.youtube.create.playlist.Snippet("newPlaylist", "PL", "Playlist generate by SpotifyToYoutube"));

        String jsonObject ="";
        try {
            jsonObject = objectMapper.writeValueAsString(newPlaylist);

        } catch (RuntimeException | JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> request = new HttpEntity<String>(jsonObject,addHeaders(tokenGoogle));
        ResponseEntity<Answer> entity=restTemplate.postForEntity(URL_YOUTUBE_PLAYLIST,request,Answer.class);


        return addToYoutubePlaylist(entity.getBody().id, videoIdList, model);
    }

    public String addToYoutubePlaylist(String id, Set<String> videoIdList, Model model) {

        OAuth2AuthenticationToken tokenGoogle = (OAuth2AuthenticationToken) model.getAttribute("tokenGoogle");
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
            HttpEntity<String> request = new HttpEntity<String>(jsonObject,addHeaders(tokenGoogle));
            ResponseEntity<String> entity= restTemplate.postForEntity(URL_YOUTUBE_PLAYLIST_ITEMS,request,String.class);
        }
        return LINK_YOUTUBE_PLAYLIST+id;
    }


}
