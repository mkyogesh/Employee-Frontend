package com.example.EmployeeFrontend.service;

import com.example.EmployeeFrontend.model.Employeecls;
import com.example.EmployeeFrontend.repository.Employeerepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomerUserDetailService implements UserDetailsService {
    @Autowired
    private Employeerepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employeecls emp = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = "ROLE_" + emp.getRole();
        return new User(emp.getUsername(),emp.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(role)));
    }
}