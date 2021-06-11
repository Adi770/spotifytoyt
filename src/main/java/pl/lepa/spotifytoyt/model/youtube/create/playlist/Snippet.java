package pl.lepa.spotifytoyt.model.youtube.create.playlist;


import lombok.Data;

@Data
public class Snippet {
    private String title;
    private String defaultLanguage;
    private String description;

    public Snippet(String title, String defaultLanguage, String description) {
        this.title = title;
        this.defaultLanguage = defaultLanguage;
        this.description = description;
    }

    public Snippet() {

    }
}
