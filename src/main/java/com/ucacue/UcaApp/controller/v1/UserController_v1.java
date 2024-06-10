package com.ucacue.UcaApp.controller.v1;

import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.repository.UserRepository;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class UserController_v1 {

    private static final Logger logger = LoggerFactory.getLogger(UserController_v1.class);

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<UserEntity> findAll() {
        try {
            List<UserEntity> users = new ArrayList<UserEntity>();
            users = userRepository.findAll();
            return users;
        } catch (Exception e) {
            logger.info("Error: {@GET /users}", e.getMessage());
            throw e;
        }
    }

}
