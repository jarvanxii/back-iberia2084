package com.iberia2084.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IberiaOAuthRedirects {
    public static final String SESSION_RETURN_TO = "iberia2084.returnTo";

    private final IberiaOAuthProperties properties;
    private final String frontendBaseUrl;

    public IberiaOAuthRedirects(
            IberiaOAuthProperties properties,
            @Value("${iberia2084.frontend-base-url:http://localhost:5173}") String frontendBaseUrl) {
        this.properties = properties;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public String sanitizeReturnTo(String returnTo) {
        var fallback = defaultReturnTo();
        if (!hasText(returnTo)) {
            return fallback;
        }

        try {
            URI uri = new URI(returnTo.trim());
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                return fallback;
            }
            var origin = origin(uri);
            if (!allowedOrigins().contains(origin)) {
                return fallback;
            }
            return uri.toString();
        } catch (URISyntaxException exception) {
            return fallback;
        }
    }

    public String defaultReturnTo() {
        return stripTrailingSlash(frontendBaseUrl) + "/home";
    }

    public String appendHandoff(String returnTo, String provider, String handoffId) {
        return appendStatus(returnTo, provider, "success", "", handoffId);
    }

    public String appendStatus(String returnTo, String provider, String status, String message) {
        return appendStatus(returnTo, provider, status, message, "");
    }

    private String appendStatus(String returnTo, String provider, String status, String message, String handoffId) {
        var target = sanitizeReturnTo(returnTo);
        var separator = target.contains("?") ? "&" : "?";
        var builder = new StringBuilder(target)
                .append(separator)
                .append("oauth=")
                .append(encode(status))
                .append("&provider=")
                .append(encode(provider));
        if (hasText(handoffId)) {
            builder.append("&handoff=").append(encode(handoffId));
        }
        if (hasText(message)) {
            builder.append("&message=").append(encode(message));
        }
        return builder.toString();
    }

    private List<String> allowedOrigins() {
        if (properties.getAllowedRedirectOrigins() == null || properties.getAllowedRedirectOrigins().isEmpty()) {
            return List.of(origin(URI.create(defaultReturnTo())));
        }
        return properties.getAllowedRedirectOrigins().stream()
                .filter(this::hasText)
                .map(String::trim)
                .map(this::stripTrailingSlash)
                .toList();
    }

    private String origin(URI uri) {
        var host = uri.getHost();
        if (host == null) {
            return "";
        }
        var port = uri.getPort();
        return uri.getScheme() + "://" + host + (port >= 0 ? ":" + port : "");
    }

    private String stripTrailingSlash(String value) {
        return String.valueOf(value == null ? "" : value).trim().replaceAll("/+$", "");
    }

    private String encode(String value) {
        return URLEncoder.encode(String.valueOf(value == null ? "" : value), StandardCharsets.UTF_8);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isBlank();
    }
}
