package com.example.oms.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.oms.auth.util.JwtUtil;
import com.example.oms.user.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Get the Auth Header
        final String authHeader = request.getHeader("Authorization");
        final String userEmail;
        final String jwtToken;

        // 2. Check if Header is valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Pass to next filter (reject)
            return;
        }

        // 3. Extract Token (Remove "Bearer ")
        jwtToken = authHeader.substring(7);

        // 4. Extract Email using our JwtUtil
        userEmail = jwtUtil.extractUsername(jwtToken);

        // 5. If email is found and user is not already authenticated...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load User Details from DB (This uses the method we just added to UserService!)
            UserDetails userDetails = this.userService.loadUserByUsername(userEmail);

            // 7. Validate Token
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {

                // 8. Create Authentication Token (The "Green Pass")
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 9. Give the Green Pass to Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Continue (Let the request reach the Controller)
        filterChain.doFilter(request, response);
    }
}