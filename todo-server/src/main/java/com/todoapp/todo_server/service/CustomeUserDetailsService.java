package com.todoapp.todo_server.service;

import com.todoapp.todo_server.entity.Roles;
import com.todoapp.todo_server.entity.UserEntity;
import com.todoapp.todo_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomeUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Load User By Name...");
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        return new User(user.getUsername(), user.getPassword(), mapRolesToGrantedAuthority(user.getRolesList()));

    }

    private Collection<GrantedAuthority> mapRolesToGrantedAuthority(Set<Roles> roles) {
        return roles.stream().map((role) -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toSet());
    }

}
