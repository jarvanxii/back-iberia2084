package com.iberia2084.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class IberiaOAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final GameService gameService;
    private final IberiaOAuthRedirects redirects;

    public IberiaOAuthSuccessHandler(GameService gameService, IberiaOAuthRedirects redirects) {
        this.gameService = gameService;
        this.redirects = redirects;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        var session = request.getSession(true);
        var returnTo = String.valueOf(session.getAttribute(IberiaOAuthRedirects.SESSION_RETURN_TO));
        var provider = authentication instanceof OAuth2AuthenticationToken oauth
                ? oauth.getAuthorizedClientRegistrationId()
                : "oauth";

        try {
            var handoffId = gameService.createOAuthHandoff(authentication);
            session.removeAttribute(IberiaOAuthRedirects.SESSION_RETURN_TO);
            response.sendRedirect(redirects.appendHandoff(returnTo, provider, handoffId));
        } catch (RuntimeException exception) {
            response.sendRedirect(redirects.appendStatus(
                    returnTo,
                    provider,
                    "error",
                    "No se pudo completar el acceso con Google."));
        }
    }
}
