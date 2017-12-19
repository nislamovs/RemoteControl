package com.rest.service;

import com.rest.model.User;

public interface UserService {

    int ACTIVATED_USER = 1;
    int NOT_ACTIVATED_USER = 0;

    User findUserByEmail(String email);
    void saveUser(User user);
    User updateUser(User user);
    String extractUsername(String authHeader);
    String randomWord();
    User activateUser(User user);
    User deleteUser(String username);
    User resetPass(User user);
}
