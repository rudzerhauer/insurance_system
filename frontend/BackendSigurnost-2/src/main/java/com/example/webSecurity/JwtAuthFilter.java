package com.example.webSecurity;
import com.example.model.User;
import com.example.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepo userRepo;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();
        logger.debug("JwtAuthFilter: Processing path: {}", path);
        if (path.startsWith("/api/auth")) {
            chain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            logger.debug("Authorization header present (length {})", authHeader.length());
        } else {
            logger.debug("No Authorization header present");
        }
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                String v = c.getValue();
                String shortVal = v == null ? "<null>" : (v.length() > 20 ? v.substring(0, 20) + "..." : v);
                logger.debug("Incoming cookie: {}={} (path={})", c.getName(), shortVal, c.getPath());
            }
        } else {
            logger.debug("No cookies in incoming request");
        }
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            logger.warn("No token found (Authorization header or ACCESS_TOKEN cookie)");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing token");
            return;
        }
        try {
            if (!jwtUtil.isTokenValid(token)) {
                logger.warn("Token invalid or expired for request: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } catch (Exception ex) {
            logger.warn("Token validation threw exception: {}", ex.getMessage(), ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username);
        if (user == null) {
            logger.warn("Authenticated token user not found: {}", username);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not found");
            return;
        }
    String storedRole = user.getRole() == null ? "USER" : user.getRole();
    String authority = storedRole.startsWith("ROLE_") ? storedRole : ("ROLE_" + storedRole);
    var auth = new UsernamePasswordAuthenticationToken(user.getUsername(),
        null,
        List.of(new SimpleGrantedAuthority(authority)));
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }
}