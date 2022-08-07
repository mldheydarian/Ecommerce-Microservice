package com.milad.userservice.auth.controller;

import com.milad.userservice.auth.model.AuthenticationRequest;
import com.milad.userservice.auth.model.AuthenticationResponse;
import com.milad.userservice.auth.util.JwtUtil;
import com.milad.userservice.model.User;
import com.milad.userservice.auth.util.impl.JwtUtilImpl;
import com.milad.userservice.service.UserService;
import com.milad.userservice.auth.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    JwtUtil jwtUtils;


    @PostMapping("/singup")
    public ResponseEntity<User> singup(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.status(200).body(user);
    }


    @PostMapping("/singin")
    public ResponseEntity<?> singin(@RequestBody AuthenticationRequest userAuthReqDto) throws Exception {
        // First step checks user credential
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userAuthReqDto.getUsername(), userAuthReqDto.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed /n" + e.getMessage());
        }

        // If User exists in context then
        // add user in security context???? by UserDetailsService loadUserByUsername() ???
        // and then Generate a new JWT token and send it to user
        try {
            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(userAuthReqDto.getUsername());
            final String token = jwtUtils.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Something went wrong in token generating part/n"+e.getMessage());
        }


    }


    @PostMapping("/signoutX")
    public String logoutUser() {
        return "Under Constraction";
        //TODO:complete singuot
    }

    @GetMapping("/profile1")
    public  String getUserInfo1(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return currentPrincipalName;
       }

    @GetMapping(value = "/profile2")
    @ResponseBody
    public String getUserInfo2(Principal principal) {
        return principal.getName();
    }

}