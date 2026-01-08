package com.example.EmployeeFrontend.controller;

import org.springframework.ui.Model;
import com.example.EmployeeFrontend.model.Employeecls;
import com.example.EmployeeFrontend.repository.Employeerepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Controller
public class EmployeeController {

    @Autowired
    private Employeerepo repo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping ("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("employee", new Employeecls());
        return "register";
    }
    @PostMapping("/register")
    public String register(@ModelAttribute("employee") Employeecls employee,
                           @RequestParam("image") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            String filename = imageFile.getOriginalFilename();
            String uploadDir  = "employee-photos/";

            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            Path path = Paths.get(uploadDir + filename);
            Files.write(path, imageFile.getBytes());

            employee.setImageName(filename);
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        repo.save(employee);

        return "redirect:/login";
    }
    @GetMapping("/employee/list")
    public String showEmployeeList(Model model, Principal principal){
        model.addAttribute("employees",repo.findAll());

        Employeecls loggedInUser = repo.findByUsername(principal.getName()).get();
        model.addAttribute("userRole", loggedInUser.getRole());
        return "employeelist";

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employee/add")
    public String showAddForm(Model model){
        model.addAttribute("employee",new Employeecls());
        return "employee_form";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute Employeecls emp,
                               @RequestParam("image") MultipartFile imageFile) throws IOException {

        if (!imageFile.isEmpty()) {
            String filename = imageFile.getOriginalFilename();
            String uploadDir  = "employee-photos/";

            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            Path path = Paths.get(uploadDir + filename);
            Files.write(path, imageFile.getBytes());

            emp.setImageName(filename);
        }

        // encode password
        emp.setPassword(passwordEncoder.encode(emp.getPassword()));

        repo.save(emp);
        return "redirect:/employee/list";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employee/edit/{id}")
    public String showEditForm (@PathVariable("id") int id, Model model){
        Employeecls emp = repo.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Invalid employee ID: " + id));

        emp.setPassword("");
        model.addAttribute("employee", emp);
        return "employee_form";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employee/delete/{id}")
    public String deleteEmployee(@PathVariable int id){
        repo.deleteById(id);
        return "redirect:/employee/list";
    }
    @GetMapping("/admin/view")
    public String show(){
        return "admin";
    }


}

