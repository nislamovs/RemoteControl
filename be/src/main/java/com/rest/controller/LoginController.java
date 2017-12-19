package com.rest.controller;

import com.rest.model.User;
import com.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value={"/register/"}, method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody User user){
        System.out.println("Creating new account : " + user.getName());
        Map<String, String> response = new ManagedMap<>();

        if(userService.findUserByEmail(user.getEmail()) != null) {
            response.put("msg", "Account with username " + user.getEmail() + " already exists.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        userService.saveUser(user);

        response.put("msg", "User created.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //As we are using Basic auth, login method just verifies is this login:pass valid
    @RequestMapping(value={"/", "/login/"}, method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody User user){
        System.out.println("User login : " + user.getEmail());
        Map<String, String> response = new ManagedMap<>();

        User account = userService.findUserByEmail(user.getEmail());
        if(account == null) {
            response.put("msg", "User with username " + user.getEmail() + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else if (account.getActive() == UserService.NOT_ACTIVATED_USER) {
            response.put("msg", "User with username " + user.getEmail() + " not activated. \nPlease visit Your email.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if(!bCryptPasswordEncoder.matches(user.getPassword(), account.getPassword())) {
            response.put("msg", "Password wrong.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("msg", "Login successful.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value={"/changepass/"}, method = RequestMethod.PUT)
    public ResponseEntity<?> changePass(@RequestBody User user,
                                        @RequestHeader(value="Authorization", defaultValue="Unauthorised") String authorization){
        System.out.println("User change pass : " + user.getName());
        Map<String, String> response = new ManagedMap<>();

        if(    !userService.extractUsername(authorization).equals(user.getEmail())
                ||	userService.updateUser(user) == null) {

            response.put("msg", "User not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("msg", "Username " + user.getEmail() + " password updated.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value={"/activateuser/"}, method = RequestMethod.GET)
    public ResponseEntity<?> activateUser(@RequestParam("username") String username,
                                          @RequestParam("keyword") String keyword) {
        System.out.println("User activating: username : " + username + "; keyword : " + keyword);
        User user = new User();
        user.setKeyword(keyword);
        user.setEmail(username);
        user = userService.activateUser(user);
        Map<String, String> response = new ManagedMap<>();

        if (user == null) {
            response.put("msg","Username " + username + " not exists.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (user.getActive() == UserService.NOT_ACTIVATED_USER) {
            response.put("msg","Username " + username + " was not activated. Probably there is a problem with confirmation link");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("msg","Username " + username + " activated.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value={"/resetpass/"}, method = RequestMethod.PUT)
    public ResponseEntity<?> resetpass(@RequestBody User user){

        System.out.println("User change forgotten pass : " + user.getEmail());
        Map<String, String> response = new ManagedMap<>();

        if (userService.resetPass(user)==null) {
            response.put("msg","Username " + user.getEmail() + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("msg","Username " + user.getEmail() + " password updated. Check Your email.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Set this user as inactive in database
    //Create cron job in database for user deletion
    @RequestMapping(value={"/deleteuser/"}, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@RequestParam("username") String username){
        System.out.println("User " + username + " deleting.");
        User user = userService.deleteUser(username);
        Map<String, String> response = new ManagedMap<>();
        if(user == null) {
            response.put("msg","Username " + user.getEmail() + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if(user.getActive() == UserService.ACTIVATED_USER) {
            response.put("msg","Username " + user.getEmail() + " deletion error.");
            return new ResponseEntity<>("msg", HttpStatus.BAD_REQUEST);
        }

        response.put("msg","Username " + user.getEmail() + " deleted.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
