package com.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController {

    private static final String healthCheckString = "<p>Dashboard IO:  v.1.0.0</p><p>Status : OK</p>";

    @RequestMapping("/")
    String home(ModelMap modal) {
        modal.addAttribute("title", "CRUD Example");
        return "index";
    }

    @RequestMapping("/partials/{page}")
    String partialHandler(@PathVariable("page") final String page) {
        return page;
    }

    @RequestMapping("/healthcheck")
    @ResponseBody
    public ResponseEntity<String> healthCheck() {
        String env = "<p>Environment : " + System.getProperty("env") + "</p>";

        String sysProp = System.getProperties()
                .stringPropertyNames()
                .toString()
                .replaceAll("[,]", "<br>")
                .replaceAll("\\[", "<p>")
                .replaceAll("\\]", "</p>");

        return ResponseEntity.status(HttpStatus.OK).body(healthCheckString + env + sysProp);
    }
}
