package com.sujeet.secure_api_gateway.controller;

import com.sujeet.secure_api_gateway.model.User;
import com.sujeet.secure_api_gateway.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private UserRepository userRespository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/hello")
    public  String hello(){
        return "Hello World";
    }

    @GetMapping("user/")
    public  User  getUser(){
        User user= new User();
        user.setUsername("sujeet");
        user.setPassword(passwordEncoder.encode("suj1234"));
        return userRespository.save(user);
    }
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
