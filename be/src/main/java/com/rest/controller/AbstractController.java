package com.rest.controller;

import com.rest.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {

    @Autowired
    protected MailService mailService;

}
