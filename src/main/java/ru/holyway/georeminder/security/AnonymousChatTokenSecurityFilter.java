package ru.holyway.georeminder.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class AnonymousChatTokenSecurityFilter extends AnonymousAuthenticationFilter {


    private final String key;

    public AnonymousChatTokenSecurityFilter(String key) {
        super(key);
        this.key = key;
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        final String chatId = request.getParameter("chatId");
        return new AnonymousAuthenticationToken(key, getPrincipal(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }
}
