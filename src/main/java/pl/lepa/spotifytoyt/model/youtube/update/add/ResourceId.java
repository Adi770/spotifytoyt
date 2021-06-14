package pl.lepa.spotifytoyt.model.youtube.update.add;


import lombok.Data;

@Data
public class ResourceId {
    private String kind;
    private String videoId;

    public ResourceId(String kind, String videoId) {
        this.kind = kind;
        this.videoId = videoId;
    }
}
