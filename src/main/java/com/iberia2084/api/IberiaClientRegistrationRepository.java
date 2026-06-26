package com.iberia2084.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class IberiaClientRegistrationRepository
        implements ClientRegistrationRepository, Iterable<ClientRegistration> {
    private final Map<String, ClientRegistration> registrations;

    public IberiaClientRegistrationRepository(Collection<ClientRegistration> registrations) {
        this.registrations = new LinkedHashMap<>();
        registrations.forEach(registration -> this.registrations.put(registration.getRegistrationId(), registration));
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.get(registrationId);
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return registrations.values().iterator();
    }

    public boolean isConfigured(String registrationId) {
        return registrations.containsKey(registrationId);
    }
}
