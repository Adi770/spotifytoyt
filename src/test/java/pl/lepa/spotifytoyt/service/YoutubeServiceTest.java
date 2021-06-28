package pl.lepa.spotifytoyt.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.youtube.Youtube;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties
@SpringBootTest
@Slf4j
class YoutubeServiceTest {

    static final String API_URL_YOUTUBE_SEARCH = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=";

    @InjectMocks
    YoutubeService youtubeService;

    @Mock
    private RestTemplate restTemplate=new RestTemplate();

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
        ReflectionTestUtils.setField(youtubeService, "youtubeApiKey", key);

    }

    @Test
    void shouldFindYoutubeClip() {

        String clipName = "Volvo Trucks - The Epic Split ";
        // ResponseEntity mockResponse = new ResponseEntity("M7FIvfx5J10", HttpStatus.OK);
        String clipId = "M7FIvfx5J10";
        Youtube youtube = new Youtube();
        youtube.setRegionCode("dsadadas");
        String abc = "https://www.googleapis.com/youtube/v3/search?maxResults=1&q=" + clipName + "&key=" + key;
//        Mockito.when(restTemplate.exchange("https://www.googleapis.com/youtube/v3/search?maxResults=1&q=" + clipName + "&key=" + key,
//                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Youtube.class)).thenReturn(new ResponseEntity<>(youtube, HttpStatus.OK));
        log.info("next");
//
//
//        ResponseEntity<Youtube> entity = restTemplate.exchange(abc, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Youtube.class);
//        Mockito.when(restTemplate.exchange(abc, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Youtube.class)).thenReturn(new ResponseEntity<>(youtube, HttpStatus.OK));
        Youtube test = youtubeService.findYoutubeClip(clipName);


//        Mockito.when(restTemplate.exchange(
//                ArgumentMatchers.anyString(),
//                ArgumentMatchers.any(HttpMethod.class),
//                ArgumentMatchers.any(),
//                eq(Class.class)
//        )).thenReturn(responseEntity);

//        Mockito.when(restTemplate.exchange(
//                anyString(),
//                eq(HttpMethod.GET),
//                ArgumentMatchers.any(),
//                eq(Youtube.class))
////        ).thenReturn(responseEntity);
//
        //    Mockito.when(restTemplate.getForEntity("https://www.googleapis.com/youtube/v3/search?maxResults=1&q=" + clipName + "&key=" + key,Youtube.class))
//                .thenReturn(new ResponseEntity<>(youtube,HttpStatus.OK));
////        Mockito.when(restTemplate.exchange(
////                ArgumentMatchers.anyString(),
////                ArgumentMatchers.any(HttpMethod.class),
////                ArgumentMatchers.any(),
////                ArgumentMatchers.<Class<Youtube>>any()))
////                .thenReturn(entity);
//////        when(restTemplate.exchange("https://www.googleapis.com/youtube/v3/search?maxResults=1&q=" + clipName + "&key=" + key,
////                HttpMethod.GET,
////                new HttpEntity<>(new HttpHeaders()),
////                Youtube.class))
////                .thenReturn(mockResponse);
////        when(restTemplate.exchange(anyString(),eq(HttpMethod.GET),any(HttpEntity.class),eq(String.class))).thenReturn(clipId);
//        Youtube actual = youtubeService.findYoutubeClip(clipName);
//
////        when(youtubeService.findYoutubeClip(clipName)).thenReturn(clipId);
//        // String actual = youtubeService.findYoutubeClip(clipName);
//        Assert.assertEquals(youtube, actual);
        //   Assertions.assertThat(youtubeService.findYoutubeClip(clipName)).isEmpty();


    }

    @Test
    void shouldCreateYoutubePlaylist() {
    }

    @Test
    void shouldAddToYoutubePlaylist() {
    }
}
