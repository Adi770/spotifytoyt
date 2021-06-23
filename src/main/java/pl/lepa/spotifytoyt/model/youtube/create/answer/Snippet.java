package pl.lepa.spotifytoyt.model.youtube.create.answer;

import lombok.Data;

import java.util.Date;

@Data
public class Snippet {
    private Date publishedAt;
    private String channelId;
    private String title;
    private String description;
    private String channelTitle;
    private String defaultLanguage;
    private Localized localized;
}
