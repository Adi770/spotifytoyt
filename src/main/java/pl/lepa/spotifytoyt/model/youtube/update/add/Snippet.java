package pl.lepa.spotifytoyt.model.youtube.update.add;


import lombok.Data;

@Data
public class Snippet {

     private String playlistId;
     private ResourceId resourceId;

     public Snippet() {
     }

     public Snippet(String playlistId, ResourceId resourceId) {
          this.playlistId = playlistId;
          this.resourceId = resourceId;
     }
}
