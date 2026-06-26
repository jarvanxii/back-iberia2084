package com.iberia2084.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iberia2084.auth")
public class IberiaOAuthProperties {
    private List<String> allowedRedirectOrigins = new ArrayList<>();
    private OAuth oauth = new OAuth();

    public List<String> getAllowedRedirectOrigins() {
        return allowedRedirectOrigins;
    }

    public void setAllowedRedirectOrigins(List<String> allowedRedirectOrigins) {
        this.allowedRedirectOrigins = allowedRedirectOrigins;
    }

    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    public static class OAuth {
        private long handoffTtlSeconds = 120;
        private Provider google = new Provider();

        public long getHandoffTtlSeconds() {
            return handoffTtlSeconds;
        }

        public void setHandoffTtlSeconds(long handoffTtlSeconds) {
            this.handoffTtlSeconds = handoffTtlSeconds;
        }

        public Provider getGoogle() {
            return google;
        }

        public void setGoogle(Provider google) {
            this.google = google;
        }
    }

    public static class Provider {
        private String clientId = "";
        private String clientSecret = "";

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }
}
