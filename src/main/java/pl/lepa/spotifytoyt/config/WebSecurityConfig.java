package pl.lepa.spotifytoyt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;
import pl.lepa.spotifytoyt.controller.RestTemplateErrorHandler;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private Oauth2SuccessHandler oauth2SuccessHandler;

    @Autowired
    public WebSecurityConfig(Oauth2SuccessHandler oauth2SuccessHandler) {
        this.oauth2SuccessHandler = oauth2SuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().disable().oauth2Login().defaultSuccessUrl("/home").successHandler(oauth2SuccessHandler);
    }


    @Bean
    public RestTemplate restTemplate() {
        ///RestTemplate restTemplate=res
        return restTemplateBuilder()
                .errorHandler(new RestTemplateErrorHandler())
                .build();

    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
