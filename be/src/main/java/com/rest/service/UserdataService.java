package com.rest.service;

import com.rest.model.Userdata;

import java.util.List;

public interface UserdataService {

    Userdata findById(Long id);

    Userdata findByName(String name);

    void saveUserdata(Userdata user);

    void updateUserdata(Userdata user);

    void deleteUserdataById(Long id);

    void deleteAllUserdatas();

    List<Userdata> findAllUserdatas();

    boolean isUserdataExists(Userdata user);
}
