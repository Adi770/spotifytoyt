package pl.lepa.spotifytoyt.model.youtube;

import com.google.api.services.youtube.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Youtube {

    private String kind;
    private String etag;
    private String nextPageToken;
    private String regionCode;
    private PageInfo pageInfo;
    private List<Item> items;

    public Youtube() {
    }
}
