package pl.lepa.spotifytoyt.model.youtube.create.answer;

import lombok.Data;

@Data
public class Answer {
    public String kind;
    public String etag;
    public String id;
    public Snippet snippet;
}
