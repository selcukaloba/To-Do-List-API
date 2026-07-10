package com.selcukaloba.to_do_api_project.jwt;

import com.selcukaloba.to_do_api_project.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService; //veritabanından kullanıcı sorgulama servisi

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if(header == null || !header.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        token = header.substring(7); //bearer kelimesini çıkarıp jwt stringine bakılır

        try {
            username = jwtService.extractUsername(token);
            if(username != null && SecurityContextHolder.getContext().getAuthentication()==null)//İsim başarıyla söküldüyse ve bu istek daha önce bu sunucuda onaylanmadıysa (Context boşsa)
            {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(jwtService.isTokenValid(token, username)) //username bulunduysa token expired olmuş mu kontrol
                {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); //ikisi de geçerliyse auth. token basılır
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //gelen isteğin bilgileri eklenir
                    SecurityContextHolder.getContext().setAuthentication(authentication); //spring securitynin context holderına eklenir
                }
            }
        }
        catch(ExpiredJwtException e)
        {
            System.out.println("Token expired!" + e.getMessage());
        }
        catch(Exception e)
        {
            System.out.println("Token authentication error!" + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
