package com.ucacue.UcaApp.controller.v1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/method")
public class TestAuthController {

    @GetMapping("/get")
    public String helloGet(){
        return "Hello World - GET";
    }

    @PostMapping("/post")
    public ResponseEntity<Map<String, String>> helloPost() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Hello World - POST");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/put")
    public String helloPut(){
        return "Hello World - PUT";
    }

    @DeleteMapping("/delete")
    public String helloDelete(){
        return "Hello World - DELETE";
    }

    @PatchMapping("/patch")
    public String helloPatch(){
        return "Hello World - PATCH";
    }
}
