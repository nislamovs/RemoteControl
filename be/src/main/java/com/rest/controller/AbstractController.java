package com.rest.controller;

import com.rest.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {

    @Autowired
    protected AlertService alertService;

}
