package pl.lepa.spotifytoyt.model.youtube.create.answer;

import lombok.Data;

import java.util.Date;

@Data
public class Snippet {
    public Date publishedAt;
    public String channelId;
    public String title;
    public String description;
    public String channelTitle;
    public String defaultLanguage;
    public Localized localized;
}
