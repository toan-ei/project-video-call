package com.example.webrtc.controller;

import com.example.webrtc.request.UserCreateRequest;
import com.example.webrtc.response.UserResponse;
import com.example.webrtc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    public UserResponse createUser(@RequestBody UserCreateRequest request){
        return userService.createUser(request);
    }

    @GetMapping("/getAllUser")
    public List<UserResponse> getall(){
        return userService.getAll();
    }

    @GetMapping("/{username}")
    public UserResponse getUser(@PathVariable @RequestBody String username){
        return userService.getUser(username);
    }

    @GetMapping("/myinfo")
    public UserResponse getMyInfo(){
        return userService.getMyInfo();
    }
}
