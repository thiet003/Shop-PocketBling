package com.project.shop.filters;

import com.project.shop.components.JwtTokenUtils;
import com.project.shop.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
            try {
                // Khong yeu cau token
                if(isByPassToken(request))
                {
                    filterChain.doFilter(request,response);
                    return;
                }
                // Yeu cau token
                final String authHeader = request.getHeader("Authorization");
                if(authHeader == null
                        || !authHeader.startsWith("Bearer "))
                {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
                }
                if(authHeader != null
                        && authHeader.startsWith("Bearer ")
                )
                {
                    final String token = authHeader.substring(7);
                    final String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
                    if (phoneNumber != null
                            && SecurityContextHolder.getContext().getAuthentication() == null
                    ){
                        User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                        if(jwtTokenUtil.validateToken(token,userDetails))
                        {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
                filterChain.doFilter(request,response);
            }catch (Exception e)
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
            }


    }
    private boolean isByPassToken(@NonNull HttpServletRequest request)
    {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of("api/v1/products","GET"),
                Pair.of("api/v1/orders","GET"),
                Pair.of("api/v1/products/images/*","GET"),
                Pair.of("api/v1/categories","GET"),
                Pair.of("api/v1/products","POST"),
                Pair.of("api/v1/users/register","POST"),
                Pair.of("api/v1/users/login","POST"),
                Pair.of("api/v1/roles","GET")
        );
        for(Pair<String, String> bypassToken : bypassTokens)
        {
            if (request.getServletPath().contains(bypassToken.getFirst())
                    && request.getMethod().equals(bypassToken.getSecond())
            ){
                return true;
            }
        }
        return false;
    }
}
