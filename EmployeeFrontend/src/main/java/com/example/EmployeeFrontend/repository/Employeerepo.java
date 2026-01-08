package com.example.EmployeeFrontend.repository;

import com.example.EmployeeFrontend.model.Employeecls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Employeerepo extends JpaRepository<Employeecls, Integer> {
    Optional<Employeecls> findByUsername(String username);

}


