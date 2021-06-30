package pl.lepa.spotifytoyt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.YoutubePlaylistClass;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;
import pl.lepa.spotifytoyt.model.youtube.Youtube;
import pl.lepa.spotifytoyt.model.youtube.create.answer.Answer;
import pl.lepa.spotifytoyt.model.youtube.create.playlist.Playlist;
import pl.lepa.spotifytoyt.model.youtube.update.add.PlaylistItem;
import pl.lepa.spotifytoyt.model.youtube.update.add.ResourceId;
import pl.lepa.spotifytoyt.model.youtube.update.add.Snippet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@Data
public class YoutubeService {

    static final String API_URL_YOUTUBE_SEARCH = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=";
    static final String API_URL_YOUTUBE_PLAYLIST_ITEMS = "https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet";
    static final String API_URL_YOUTUBE_PLAYLIST = "https://youtube.googleapis.com/youtube/v3/playlists?part=snippet";
    static final String LINK_YOUTUBE_PLAYLIST = "https://www.youtube.com/playlist?list=";

    private HttpHeaders googleHeaders;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;
    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Autowired
    public YoutubeService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void getToken(HttpHeaders headers) {
        this.googleHeaders = headers;
    }

    public ResponseEntity<String> getYoutubePlaylist(String url) {
        return restTemplate.exchange(API_URL_YOUTUBE_PLAYLIST, HttpMethod.GET, new HttpEntity<>(googleHeaders), String.class);
    }


    public Set<String> createSetYoutubeClipId(SpotifyPlaylistItems spotifyPlaylist) {
        Set<String> videoIdList = new HashSet<>();

        for (pl.lepa.spotifytoyt.model.spotify.Item name : spotifyPlaylist.getItems()) {
            videoIdList.add(findYoutubeClip(name.getTrack().getName()).getItems().get(0).getId().getVideoId());
            break; //because consume to much google resource
        }
        log.info(videoIdList.toString());
        return videoIdList;
    }

    public Youtube findYoutubeClip(String name) {
        String url = API_URL_YOUTUBE_SEARCH + name + "&key=" + youtubeApiKey;
        ResponseEntity<Youtube> entity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Youtube.class);
        return entity.getBody();

    }


    public YoutubePlaylistClass createYoutubePlaylist(Set<String> videoIdList) {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setKind("youtube#playlist");
        newPlaylist.setSnippet(
                new pl.lepa.spotifytoyt.model.youtube.create.playlist.Snippet
                        ("newPlaylist", "PL", "Playlist generate by SpotifyToYoutube"));

        String jsonObject = "";
        try {
            jsonObject = objectMapper.writeValueAsString(newPlaylist);

        } catch (RuntimeException | JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<String> request = new HttpEntity<>(jsonObject, googleHeaders);
        ResponseEntity<Answer> entity = restTemplate.postForEntity(API_URL_YOUTUBE_PLAYLIST, request, Answer.class);


        return new YoutubePlaylistClass(Objects.requireNonNull(entity.getBody()).getId(), videoIdList);
    }


    public String addToYoutubePlaylist(YoutubePlaylistClass youtubePlaylist) {
        String id=youtubePlaylist.getId();
        Set<String> videoIdList=youtubePlaylist.getVideoIdList();

        PlaylistItem newPlaylistItem = new PlaylistItem();
        newPlaylistItem.setSnippet(new Snippet(id, new ResourceId("youtube#video", "")));
        String jsonObject = "";

        for (String videoId : videoIdList) {
            newPlaylistItem.getSnippet().getResourceId().setVideoId(videoId);
            try {
                jsonObject = objectMapper.writeValueAsString(newPlaylistItem);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            HttpEntity<String> request = new HttpEntity<>(jsonObject, googleHeaders);
            restTemplate.postForEntity(API_URL_YOUTUBE_PLAYLIST_ITEMS, request, String.class);
        }
        return LINK_YOUTUBE_PLAYLIST + id;
    }


}
