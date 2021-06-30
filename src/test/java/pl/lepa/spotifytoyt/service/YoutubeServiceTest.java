package pl.lepa.spotifytoyt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.YoutubePlaylistClass;
import pl.lepa.spotifytoyt.model.spotify.Item;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;
import pl.lepa.spotifytoyt.model.spotify.Track;
import pl.lepa.spotifytoyt.model.youtube.VideoId;
import pl.lepa.spotifytoyt.model.youtube.Youtube;
import pl.lepa.spotifytoyt.model.youtube.create.answer.Answer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties
@SpringBootTest
@Slf4j
class YoutubeServiceTest {

    @Autowired
    YoutubeService youtubeService;

    @InjectMocks
    YoutubeService mockYoutubeService;

    @Mock
    RestTemplate mockRestTemplate;

    @Mock
    ObjectMapper mockObjectMapper;


    @Value("${google.oauth2.test.key}")
    private String authorizationKey;

    @Value("${youtube.api.key}")
    private String key;


    @Test
    void shouldGetYoutubePlaylist() {


    }

    @Test
    void shouldCreateSetYoutubeClipId() {
        SpotifyPlaylistItems spotifyPlaylistItems = new SpotifyPlaylistItems();
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        Track track = new Track();
        track.setHref("test Href");
        track.setName("test Name");
        item.setTrack(track);
        itemList.add(item);
        spotifyPlaylistItems.setItems(itemList);

        Youtube youtube = new Youtube();

        youtube.setRegionCode("");
        youtube.setKind("test Kind");
        List<pl.lepa.spotifytoyt.model.youtube.Item> ytItemList= new ArrayList<>();
        pl.lepa.spotifytoyt.model.youtube.Item ytItem=new pl.lepa.spotifytoyt.model.youtube.Item();

        ytItem.setKind("test yt Kind");
        ytItem.setId(new VideoId("test VideoI Kind","test VideoId id"));
        ytItem.setEtag("test yt Etag");

        ytItemList.add(ytItem);

        youtube.setItems(ytItemList);

        Mockito.when(mockRestTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Youtube>>any()
        )).thenReturn(new ResponseEntity<>(youtube, HttpStatus.OK));

        Set<String> current = mockYoutubeService.createSetYoutubeClipId(spotifyPlaylistItems);
    }

    @BeforeEach
    void setUp() {
        //ReflectionTestUtils.setField(youtubeService, "youtubeApiKey", key);

    }

    @Test
    void shouldFindYoutubeClip() {
        String clipName = "Volvo Trucks - The Epic Split ";
        String clipId = "M7FIvfx5J10";
        Youtube test = youtubeService.findYoutubeClip(clipName);
        assertEquals(test.getItems().get(0).getId().getVideoId(), clipId);
    }

    @Test
    void shouldCreateYoutubePlaylist() {
        Set<String> videoIdList = new HashSet<>();
        videoIdList.add("Test Clip Id 1");
        videoIdList.add("Test Clip Id 2");
        Answer answer = new Answer();
        answer.setId("423");
        Mockito.when(mockRestTemplate.postForEntity(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(Answer.class)
        )).thenReturn(new ResponseEntity<>(answer, HttpStatus.OK));

        YoutubePlaylistClass current = mockYoutubeService.createYoutubePlaylist(videoIdList);

        assertEquals(videoIdList, current.getVideoIdList());

    }

    @Test
    void shouldAddToYoutubePlaylist() {
        YoutubePlaylistClass youtubePlaylist = new YoutubePlaylistClass();
        youtubePlaylist.setId("1fwffsd1f");
        youtubePlaylist.setVideoIdList(new HashSet<>());

        String linkToPlaylist = mockYoutubeService.addToYoutubePlaylist(youtubePlaylist);
        assertTrue(!linkToPlaylist.isEmpty());

    }
}
