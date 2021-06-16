package pl.lepa.spotifytoyt.exceptions;

public class CustomToManyRequests extends Throwable {
    public CustomToManyRequests(String message) {
        super(message);
    }

    public CustomToManyRequests() {
    }
}
