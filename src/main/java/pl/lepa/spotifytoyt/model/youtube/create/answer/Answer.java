package pl.lepa.spotifytoyt.model.youtube.create.answer;

import lombok.Data;

@Data
public class Answer {
    private String kind;
    private String etag;
    private String id;
    private Snippet snippet;
}
