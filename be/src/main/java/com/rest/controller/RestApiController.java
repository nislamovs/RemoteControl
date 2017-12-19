package com.rest.controller;

import com.rest.model.Userdata;
import com.rest.service.UserdataService;
import com.rest.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestApiController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    UserdataService userdataService; //Service which will do all data retrieval/manipulation work

    // -------------------Retrieve All Users---------------------------------------------
    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    public ResponseEntity<List<Userdata >> listAllUsers() {
        List<Userdata> users = userdataService.findAllUserdatas();
        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Userdata>>(users, HttpStatus.OK);
    }

    // -------------------Retrieve Single Userdata by ID------------------------------------------
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserByID(@PathVariable("id") long id) {
        LOG.info("Fetching Userdata with id {}", id);
        Userdata user = userdataService.findById(id);
        if (user == null) {
            LOG.error("Userdata with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Userdata with id " + id + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Userdata >(user, HttpStatus.OK);
    }

    // -------------------Retrieve Single Userdata by name------------------------------------------
    @RequestMapping(value = "/getuser/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserByUsername(@PathVariable("username") String username) {
        LOG.info("Fetching Userdata with username {}", username);
        Userdata user = userdataService.findByName(username);
        if (user == null) {
            LOG.error("Userdata with username {} not found.", username);
            return new ResponseEntity(new CustomErrorType("Userdata with username " + username + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Userdata>(user, HttpStatus.OK);
    }

    // -------------------Create a Userdata -------------------------------------------

    @RequestMapping(value = "/user/", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody Userdata user, UriComponentsBuilder ucBuilder) {
        LOG.info("Creating Userdata : {}", user);

        if (userdataService.isUserdataExists(user)) {
            LOG.error("Unable to create. A Userdata with name {} already exists", user.getName());
            return  new ResponseEntity(new CustomErrorType("Unable to create. A user with name " + user.getName() + " already exists."), HttpStatus.CONFLICT);
        }
        userdataService.saveUserdata(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    // -------------------Update a Userdata -------------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody Userdata user) {
        LOG.info("Updating Userdata with id {}", id);

        Userdata currentUserdata = userdataService.findById(id);

        if(currentUserdata == null) {
            LOG.error("Unable to update. Userdata with id {} not exists.", id);
            return new ResponseEntity(new CustomErrorType("Unable to update. Userdata with id " + id + " not found."), HttpStatus.NOT_FOUND);
        }

        currentUserdata.setName(user.getName());
        currentUserdata.setAge(user.getAge());
        currentUserdata.setSalary(user.getSalary());

        userdataService.updateUserdata(currentUserdata);
        return new ResponseEntity<Userdata >(currentUserdata, HttpStatus.OK);
    }

    // ------------------- Delete a Userdata -----------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        LOG.info("Fetching & Deleting Userdata with id {}", id);

        Userdata user = userdataService.findById(id);
        if (user == null) {
            LOG.error("Unable to delete. Userdata with id {} not found.", id);
            return new ResponseEntity(new CustomErrorType("Unable to delete. Userdata with id " + id + " not found."), HttpStatus.NOT_FOUND);
        }
        userdataService.deleteUserdataById(id);
        return new ResponseEntity<Userdata >(HttpStatus.NO_CONTENT);
    }

    // ------------------- Delete All Users-----------------------------

    @RequestMapping(value = "/user/", method = RequestMethod.DELETE)
    public ResponseEntity<Userdata> deleteAllUserdatas() {
        LOG.info("Deleting All Users");

        userdataService.deleteAllUserdatas();
        return new ResponseEntity<Userdata>(HttpStatus.NO_CONTENT);
    }
}
