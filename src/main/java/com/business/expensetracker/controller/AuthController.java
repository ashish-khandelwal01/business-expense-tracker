package com.business.expensetracker.controller;

import com.business.expensetracker.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(username);

        // Use explicit Set-Cookie header to specify SameSite; add Secure in non-local environments
        boolean isLocal = true; // naive default; adjust if needed based on env
        String cookie = "token=" + jwt + "; HttpOnly; Path=/; SameSite=Lax" + (isLocal ? "" : "; Secure");
        response.addHeader("Set-Cookie", cookie);

        return Map.of("message", "Login successful");
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletResponse response) {
        String cookie = "token=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax";
        response.addHeader("Set-Cookie", cookie);
        return Map.of("message", "Logged out successfully");
    }
}
