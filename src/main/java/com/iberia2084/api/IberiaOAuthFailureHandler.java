package com.iberia2084.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class IberiaOAuthFailureHandler implements AuthenticationFailureHandler {
    private final IberiaOAuthRedirects redirects;

    public IberiaOAuthFailureHandler(IberiaOAuthRedirects redirects) {
        this.redirects = redirects;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        var session = request.getSession(true);
        var returnTo = String.valueOf(session.getAttribute(IberiaOAuthRedirects.SESSION_RETURN_TO));
        session.removeAttribute(IberiaOAuthRedirects.SESSION_RETURN_TO);
        response.sendRedirect(redirects.appendStatus(
                returnTo,
                "google",
                "error",
                "No se pudo completar el acceso con Google."));
    }
}
