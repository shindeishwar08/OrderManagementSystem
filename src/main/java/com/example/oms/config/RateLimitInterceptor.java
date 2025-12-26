package com.example.oms.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.oms.common.entity.RateLimitType;
import com.example.oms.common.exception.RateLimitExceededException;
import com.example.oms.common.service.RateLimiterService;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final RateLimiterService rateLimiterService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String key ="";

        if(auth!=null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")){
            key=auth.getName();
        }else{
            key = request.getHeader("X-Forwarded-For");
            if (key == null || key.isBlank()) {
                key = request.getRemoteAddr();
            }
        }

        String requestURI = request.getRequestURI();
        RateLimitType type = RateLimitType.GENERAL;

        if(requestURI.startsWith("/auth/login")) type=RateLimitType.LOGIN;
        else if(requestURI.startsWith("/orders")) type=RateLimitType.ORDER;
        

        Bucket bucket = rateLimiterService.resolveBucket(type,key);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if(probe.isConsumed()){
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;

        }else{
            throw new RateLimitExceededException("Rate Limit Exceeded, try again in sometime");
        }
    }
}
