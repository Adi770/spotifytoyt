package pl.lepa.spotifytoyt.model.youtube;

import lombok.Data;

@Data
public class VideoId {
    private String kind;
    private String videoId;

    public VideoId() {
    }

    public VideoId(String kind, String videoId) {
        this.kind = kind;
        this.videoId = videoId;
    }
}
