package pl.lepa.spotifytoyt.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.YoutubePlaylistClass;
import pl.lepa.spotifytoyt.model.youtube.Youtube;
import pl.lepa.spotifytoyt.model.youtube.create.answer.Answer;

import java.util.HashSet;
import java.util.Set;

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


    @Value("${google.oauth2.test.key}")
    private String authorizationKey;

    @Value("${youtube.api.key}")
    private String key;


    @Test
    void shouldGetYoutubePlaylist() {


    }

    @Test
    void shouldCreateSetYoutubeClipId() {



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
        Assertions.assertEquals(test.getItems().get(0).getId().getVideoId(),clipId);
    }

    @Test
    void shouldCreateYoutubePlaylist() {


        Set<String> videoIdList=new HashSet<>();
        videoIdList.add("Test Clip Id 1");
        videoIdList.add("Test Clip Id 2");
        Answer answer=new Answer();
        answer.setId("423");
        Mockito.when(mockRestTemplate.postForEntity(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(Answer.class)
        )).thenReturn(new ResponseEntity<>(answer, HttpStatus.OK));

       YoutubePlaylistClass current= mockYoutubeService.createYoutubePlaylist(videoIdList);

    }

    @Test
    void shouldAddToYoutubePlaylist() {
        YoutubePlaylistClass youtubePlaylist= new YoutubePlaylistClass();
        youtubePlaylist.setId("1fwffsd1f");
        youtubePlaylist.setVideoIdList(new HashSet<>());

        String linkToPlaylist= mockYoutubeService.addToYoutubePlaylist(youtubePlaylist);

    }
}
