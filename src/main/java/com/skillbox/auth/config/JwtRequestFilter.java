package com.skillbox.auth.config;

import com.skillbox.auth.security.UserDetailsServiceImpl;
import com.skillbox.auth.security.jwt.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private  final UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtTokenUtils.validateJwtToken(jwt)) {
                String userName = jwtTokenUtils.getUserName(jwt);

                log.info(" User name = {}", userName);

                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                log.info(" User details = {}", userDetails);
                log.info(" User details authority = {}", userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails
                        , null
                        , userDetails.getAuthorities()
                );
                log.info("Is user authenticated? " + token.isAuthenticated());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("setDetails");
                SecurityContextHolder.getContext().setAuthentication(token);
                log.info("set auth token");
            }
        }catch (Exception exception) {
            log.error("Cannot set user authentication: {}", exception.getLocalizedMessage());
        }

        log.info("begin do filter");
        filterChain.doFilter(request, response);
        log.info("end do filter");

    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
