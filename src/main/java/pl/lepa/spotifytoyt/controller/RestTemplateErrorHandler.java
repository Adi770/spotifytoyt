package pl.lepa.spotifytoyt.controller;

import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import pl.lepa.spotifytoyt.exceptions.CustomToManyRequests;

import java.io.IOException;


@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return (clientHttpResponse.getStatusCode().is4xxClientError() || clientHttpResponse.getStatusCode().is5xxServerError());
    }

    @SneakyThrows
    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {

        if (clientHttpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

        } else if (clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            if (clientHttpResponse.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new CustomToManyRequests();
            }
        }
    }
}
