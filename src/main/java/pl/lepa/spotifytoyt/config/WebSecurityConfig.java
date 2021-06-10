package pl.lepa.spotifytoyt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private Oauth2SuccessHandler oauth2SuccessHandler;

    @Autowired
    public WebSecurityConfig(Oauth2SuccessHandler oauth2SuccessHandler) {
        this.oauth2SuccessHandler = oauth2SuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      http.authorizeRequests().antMatchers("/").authenticated()
              .and().cors().disable().oauth2Login().defaultSuccessUrl("/home").successHandler(oauth2SuccessHandler);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}