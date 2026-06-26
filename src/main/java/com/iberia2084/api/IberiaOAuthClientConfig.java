package com.iberia2084.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class IberiaOAuthClientConfig {

    @Bean
    IberiaClientRegistrationRepository clientRegistrationRepository(IberiaOAuthProperties properties) {
        List<ClientRegistration> registrations = new ArrayList<>();
        addIfConfigured(registrations, google(properties.getOauth().getGoogle()));
        return new IberiaClientRegistrationRepository(registrations);
    }

    @Bean
    OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository registrations) {
        return new InMemoryOAuth2AuthorizedClientService(registrations);
    }

    private void addIfConfigured(List<ClientRegistration> registrations, ClientRegistration registration) {
        if (registration != null) {
            registrations.add(registration);
        }
    }

    private ClientRegistration google(IberiaOAuthProperties.Provider provider) {
        if (!hasCredentials(provider)) {
            return null;
        }
        return CommonOAuth2Provider.GOOGLE
                .getBuilder("google")
                .clientId(provider.getClientId().trim())
                .clientSecret(provider.getClientSecret().trim())
                .scope("openid", "profile", "email")
                .build();
    }

    private boolean hasCredentials(IberiaOAuthProperties.Provider provider) {
        return provider != null && hasText(provider.getClientId()) && hasText(provider.getClientSecret());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
