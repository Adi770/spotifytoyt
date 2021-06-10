package pl.lepa.spotifytoyt.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;


@Slf4j
public class CustomAuthorizationCodeInstalledApp {
    private final AuthorizationCodeFlow flow;
    private final VerificationCodeReceiver receiver;
    // private static final Logger LOGGER = Logger.getLogger(com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.class.getName());
    private final CustomAuthorizationCodeInstalledApp.Browser browser;

    public CustomAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
        this(flow, receiver, new CustomAuthorizationCodeInstalledApp.DefaultBrowser());
    }

    public CustomAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver, CustomAuthorizationCodeInstalledApp.Browser browser) {
        this.flow = (AuthorizationCodeFlow) Preconditions.checkNotNull(flow);
        this.receiver = (VerificationCodeReceiver) Preconditions.checkNotNull(receiver);
        this.browser = browser;
    }

    public static void browse(String url) {
        Preconditions.checkNotNull(url);
        System.out.println("Please open the following address in your browser:");
        System.out.println("  " + url);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Action.BROWSE)) {
                    System.out.println("Attempting to open that address in the default browser now...");
                    desktop.browse(URI.create(url));
                }
            } else {
                Runtime rt = Runtime.getRuntime();
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        } catch (IOException var2) {
            log.info("Unable to open browser", var2);
        } catch (InternalError var3) {
            log.info("Unable to open browser", var3);
        }

    }

    public Credential authorize(String userId) throws IOException {
        Credential var7;
        try {
            Credential credential = this.flow.loadCredential(userId);
            if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds() > 60L)) {
                Credential var11 = credential;
                return var11;
            }

            String redirectUri = this.receiver.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl = this.flow.newAuthorizationUrl().setRedirectUri(redirectUri);
            this.onAuthorization(authorizationUrl);
            String code = this.receiver.waitForCode();
            TokenResponse response = this.flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            var7 = this.flow.createAndStoreCredential(response, userId);
        } finally {
            this.receiver.stop();
        }

        return var7;
    }

    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        String url = authorizationUrl.build();
        Preconditions.checkNotNull(url);
        this.browser.browse(url);
    }

    public final AuthorizationCodeFlow getFlow() {
        return this.flow;
    }

    public final VerificationCodeReceiver getReceiver() {
        return this.receiver;
    }

    public interface Browser {
        void browse(String var1) throws IOException;
    }

    public static class DefaultBrowser implements CustomAuthorizationCodeInstalledApp.Browser {
        public DefaultBrowser() {
        }

        public void browse(String url) throws IOException {
            CustomAuthorizationCodeInstalledApp.browse(url);
        }
    }
}
