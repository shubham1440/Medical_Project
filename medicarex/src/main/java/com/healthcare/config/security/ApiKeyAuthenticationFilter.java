package com.healthcare.config.security;

import com.healthcare.service.ApiKeyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyManager apiKeyManager;

    public ApiKeyAuthenticationFilter(ApiKeyManager manager) {
        this.apiKeyManager = manager;
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String requestUri = request.getRequestURI();
//
//        // 1. Identify if the request is for Swagger UI or the API definition
//        boolean isSwaggerRequest = requestUri.contains("/swagger-ui") ||
//                requestUri.contains("/api-docs") ||
//                requestUri.contains("/swagger-ui.html");
//
//        if (isSwaggerRequest) {
//
//            // 2. BYPASS: Always allow the login page and static assets (CSS, JS, Images)
//            // If we don't do this, the login page will look broken or won't load.
//            if (requestUri.contains("/docs-login") ||
//                    requestUri.endsWith(".css") ||
//                    requestUri.endsWith(".js") ||
//                    requestUri.endsWith(".png") ||
//                    requestUri.endsWith(".ico") ||
//                    requestUri.endsWith(".svg")) {
//
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // 3. SESSION CHECK: Look for the key saved by DocAuthController
//            String sessionKey = (String) request.getSession().getAttribute("DOCS_ACCESS_KEY");
//
//            // 4. VALIDATION: If key is missing or invalid, redirect to the custom login gate
//            if (sessionKey == null || !apiKeyManager.authenticateAndGetClient(sessionKey).isLocked()) {
//                System.out.println("LOG: Blocked unauthenticated access to: " + requestUri);
//                response.sendRedirect("/docs-login");
//                return;
//            }
//
//            // 5. SECURITY CONTEXT: If valid, tell Spring Security this is an authenticated "Docs User"
//            // This prevents Spring's internal filters from redirecting to the standard /login page.
//            if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                        "DOCS_USER",
//                        null,
//                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCS_USER"))
//                );
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//
//        // Continue the filter chain for valid requests or non-swagger requests
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 1. Identify if the request is for Swagger UI or the API definition
        boolean isSwaggerRequest = requestUri.contains("/swagger-ui") ||
                requestUri.contains("/api-docs") ||
                requestUri.contains("/swagger-ui.html");

        if (isSwaggerRequest) {

            // 2. BYPASS: Allow login page and assets
            if (requestUri.contains("/docs-login") ||
                    requestUri.endsWith(".css") ||
                    requestUri.endsWith(".js") ||
                    requestUri.endsWith(".png") ||
                    requestUri.endsWith(".ico") ||
                    requestUri.endsWith(".svg")) {

                filterChain.doFilter(request, response);
                return;
            }

            // 3. SESSION CHECK
            HttpSession session = request.getSession(false); // Use false to avoid creating empty sessions
            String sessionKey = (session != null) ? (String) session.getAttribute("DOCS_ACCESS_KEY") : null;

            // 4. VALIDATION & REAL-TIME SECURITY CHECK
            if (sessionKey == null) {
                response.sendRedirect("/docs-login");
                return;
            }

            try {
                // This checks if the key still exists and if the account was locked
                // since the user last clicked a link.
                apiKeyManager.authenticateAndGetClient(sessionKey);

            } catch (Exception e) {
                // Key was deleted or locked in DB while user was browsing
                if (session != null) session.invalidate();
                response.sendRedirect("/docs-login?error=true&message=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
                return;
            }

            // 5. SECURITY CONTEXT: Keep Spring Security happy
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        "DOCS_USER",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCS_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}