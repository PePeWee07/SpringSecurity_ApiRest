package com.ucacue.UcaApp.controller.v1;

import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.repository.UserRepository;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class UserController_v1 {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<UserEntity> findAll(){
        List<UserEntity> users = new ArrayList<UserEntity>();
        users = userRepository.findAll();
        return users;
    }

}
