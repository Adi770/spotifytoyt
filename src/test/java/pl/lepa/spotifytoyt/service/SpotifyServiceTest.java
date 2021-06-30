package pl.lepa.spotifytoyt.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.model.spotify.Item;
import pl.lepa.spotifytoyt.model.spotify.SpotifyPlaylistItems;
import pl.lepa.spotifytoyt.model.spotify.Track;

import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties
class SpotifyServiceTest {

    static final String API_URL_SPOTIFY_PLAYLIST = "https://api.spotify.com/v1/playlists";

    @InjectMocks
    SpotifyService spotifyService;

    @Mock
    RestTemplate restTemplate;


    public SpotifyPlaylistItems getSpotifyPlaylist(String playlistId, HttpHeaders headers) {
        String playlistID = "/" + playlistId;
        ResponseEntity<SpotifyPlaylistItems> template = restTemplate.exchange(API_URL_SPOTIFY_PLAYLIST + playlistID + "/tracks", HttpMethod.GET, new HttpEntity<>(headers), SpotifyPlaylistItems.class);
        return template.getBody();
    }

    @Test
    void getSpotifyPlaylist() {
        String playListId = "3cEYpjA9oz9GiPac4AsH4n";
        SpotifyPlaylistItems spotifyPlaylistItems = new SpotifyPlaylistItems();
        Item item = new Item();
        Track track = new Track();

        List<Item> items = new ArrayList<>();

        track.setHref("test Href");
        track.setName("test Track");
        item.setTrack(track);

        items.add(item);

        spotifyPlaylistItems.setItems(items);

        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<SpotifyPlaylistItems>>any()
        ))
                .thenReturn(new ResponseEntity<>(spotifyPlaylistItems, HttpStatus.OK));

        SpotifyPlaylistItems testList = spotifyService.getSpotifyPlaylist(playListId);
        Assertions.assertEquals(testList.getItems().get(0).getTrack(),track);

    }
}
