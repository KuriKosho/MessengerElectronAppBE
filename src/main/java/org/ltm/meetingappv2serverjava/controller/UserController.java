package org.ltm.meetingappv2serverjava.controller;


import lombok.RequiredArgsConstructor;
//import org.ltm.meetingappv2serverjava.config.WebSocketEventListener;
import org.ltm.meetingappv2serverjava.DTO.*;
import org.ltm.meetingappv2serverjava.model.User;
import org.ltm.meetingappv2serverjava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Controller
@RequestMapping("/api/")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @PostMapping("/verify")
    public Response<Boolean> verify(@RequestBody Verify verify) {
        System.out.println("Verify: " + verify.toString());
        boolean result = userService.verify(verify.getEmail(), verify.getCode());
        if (result) {
            messagingTemplate.convertAndSend("/topic/users", userService.getAllUsers());
            return new Response<Boolean>(true,result);
        } else {
            return new Response<Boolean>(false,null);
        }
    }

    @PostMapping("/register")
    public Response<User> register(@RequestBody Register register) {
        System.out.println("Register: " + register.toString());
        User user = userService.register(register);
        if (user == null) {
            return new Response<User>(false,null);
        } else {
            return new Response<User>(true,user);
        }
    }
    @PostMapping("/login")
    public Response<User> login(@RequestBody Login login) {
        User user = userService.login(login.getEmail(), login.getPassword());
        if (user == null) {
            return new Response<User>(false,null);
        } else {
            return new Response<User>(true,user);
        }
    }
    @PostMapping("/logout")
    public Response<Boolean> logout(@RequestBody Logout email) {
        System.out.println("Logout email: " + email);
        boolean result = userService.logout(email.getEmail());

        System.out.println("Logout result: " + result);
        if (result) {
            messagingTemplate.convertAndSend("/topic/users", userService.getAllUsers());
            return new Response<Boolean>(true,result);
        } else {
            return new Response<Boolean>(false,null);
        }
    }

    @GetMapping("/find-user-by-email")
    public Response<UserDTO> findUserByEmail(@RequestParam String email) {
        UserDTO user = userService.getUserByEmail(email);
        if (user == null) {
            return new Response<UserDTO>(false,null);
        } else {
            return new Response<UserDTO>(true,user);
        }
    }
    @GetMapping("/get-all-users")
    public Response<List<UserDTO>> getAllUsers(@RequestParam String userId) {
        List<UserDTO> users = userService.getAllUsersWithLastMessage(userId);
        if (users == null) {
            return new Response<List<UserDTO>>(false,null);
        } else {
            return new Response<List<UserDTO>>(true,users);
        }
    }

}
