package com.rest.service;

import com.rest.model.User;

public interface MailService {
    void sendError(Throwable t);
    void sendMail(String msg, String email);
    void sendActivationMail(User user);
    void sendNewPasswordMail(User user, String passwd);
}
