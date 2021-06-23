package pl.lepa.spotifytoyt.model.youtube;

import lombok.Data;

@Data
public class Item {
    private String kind;
    private String etag;
    private VideoId id;
}
