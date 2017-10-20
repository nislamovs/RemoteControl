package com.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

@RestController
@RequestMapping("/sys")
public class DiagnosticsController {

    public static final Logger logger = LoggerFactory.getLogger(DiagnosticsController.class);

    @RequestMapping(value = "/healthcheck", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> healthcheck() throws IOException {
        logger.info("Healthcheck requested.");
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("build.properties"));

        Map<String, String> params = new TreeMap<String, String>();
        params.put("Dashboard IO", properties.getProperty("application.version"));
        params.put("Environment", properties.getProperty("application.environment"));
        params.put("Deployment date", properties.getProperty("application.deploymentdate"));
        params.put("Status", "OK");

        return new ResponseEntity<Map<String, String>>(params, HttpStatus.OK);
    }
}
