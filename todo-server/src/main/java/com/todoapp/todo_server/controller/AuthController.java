package com.todoapp.todo_server.controller;

import com.todoapp.todo_server.dto.AuthResponseDTO;
import com.todoapp.todo_server.dto.LoginDTO;
import com.todoapp.todo_server.dto.RegisterDTO;
import com.todoapp.todo_server.entity.Roles;
import com.todoapp.todo_server.entity.UserEntity;
import com.todoapp.todo_server.repository.UserRepository;
import com.todoapp.todo_server.security.JWTGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.todoapp.todo_server.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping(path = "/api/auth")
@Log4j2
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            if(!userRepository.existsByUsername(loginDTO.getUsername())) {
                log.error("No user with username: {} exists", loginDTO.getUsername());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            log.info("User {} found!",loginDTO.getUsername());
            log.info("Setting up JWT Token for user {}...", loginDTO.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            log.info("Generated JWT Token for user {}", loginDTO.getUsername());
            return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register/{role}")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO, @PathVariable String role) {
        try {
            if(userRepository.existsByUsername(registerDTO.getUsername())) {
                log.error("User {} already exists!", registerDTO.getUsername());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            log.info("Setting up new user...");
            UserEntity user = new UserEntity();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            Roles roles = roleRepository.findByRoleName(role.equalsIgnoreCase("admin")? "ADMIN" : "USER").get();
            user.setRolesList(Collections.singleton(roles));
            userRepository.save(user);
            log.info("User {} registered successfully with role: {}", registerDTO.getUsername(), roles.getRoleName());
            return new ResponseEntity<>("User registered!", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
