package pl.lepa.spotifytoyt.model.youtube;

import com.google.api.services.youtube.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Youtube {

    public String kind;
    public String etag;
    public String nextPageToken;
    public String regionCode;
    public PageInfo pageInfo;
    public List<Item> items;
}
