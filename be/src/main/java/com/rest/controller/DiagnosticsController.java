package com.rest.controller;

import com.rest.service.MailService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/sys")
public class DiagnosticsController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(DiagnosticsController.class);
    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Autowired
    Environment environment;
    @Autowired
    MailService mailService;

    @ApiOperation(value="healthcheck", notes="Application healthcheck.")
    @GetMapping(value = "/healthcheck")
    public ResponseEntity<Map<String, String>> healthcheck() throws IOException {
        LOG.info("Healthcheck requested.");
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("build.properties"));

        Map<String, String> params = new ManagedMap<>();
        params.put("Dashboard IO", properties.getProperty("application.version"));
        params.put("Environment", properties.getProperty("application.environment"));
        params.put("Active Spring profile(s)", System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME));
        params.put("Deployment date", properties.getProperty("application.deploymentdate"));
        params.put("Status", "OK");

        return new ResponseEntity<Map<String, String>>(params, HttpStatus.OK);
    }

    @ApiOperation(value="alertmail", notes="Generates exception, catches it and send stacktrace to email.")
    @Profile("development")
    @GetMapping(value = "/alertmail")
    public ResponseEntity<Map<String, String>> alertmail() {

        try {
            LOG.info("Dropping exception ...");
            throw new RuntimeException("Testing alert service... check mail :)");
        } catch (RuntimeException e) {
            LOG.info("Catching exception ...");
            mailService.sendError(e);
        }

        LOG.info("Exception generated. Alert mail sent.");

        Map<String, String> params = new ManagedMap<>();
        params.put("Status", "Exception generated. Alert mail sent.");
        params.put("Time", String.valueOf(DATETIME_FORMAT.format(new Date())));

        return new ResponseEntity<Map<String, String>>(params, HttpStatus.OK);
    }

    @GetMapping(value = "/sendemail/")
    public ResponseEntity<?> sendMail(
            @RequestParam(value = "keyword", required = false, defaultValue = "This is test message.") String keyword) {

        mailService.sendMail(keyword, environment.getProperty("alert.email.to"));
        return new ResponseEntity<>("{ok}", HttpStatus.OK);
    }
}
