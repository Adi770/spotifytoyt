package pl.lepa.spotifytoyt.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Set;


@Getter
@Setter
public class YoutubePlaylistClass {

    private String id;
    private Set<String> videoIdList;

    public YoutubePlaylistClass(String id, Set<String> videoIdList) {
        this.id = id;
        this.videoIdList = videoIdList;
    }

    public YoutubePlaylistClass() {

    }
}
