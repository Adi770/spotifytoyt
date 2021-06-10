package pl.lepa.spotifytoyt.model.youtube;

import lombok.Data;

@Data
public class Item {
    public String kind;
    public String etag;
    public VideoId id;
}
