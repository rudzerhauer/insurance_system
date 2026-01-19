package com.example.webSecurity;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.model.User;
import com.example.repository.UserRepo;
import com.example.service.TwoFactorAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class TwoFactorAuthFilter extends OncePerRequestFilter {
	@Autowired
	private TwoFactorAuthService twoFactorAuthService;
	@Autowired
	private UserRepo userRepo;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI(); 
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response); 
            return;
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); 
        User user = userRepo.findByUsername(username);
        if(user != null && !user.isVerified()) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
        	response.getWriter().write("2FA verification required");
        }
        filterChain.doFilter(request, response);
}
}
