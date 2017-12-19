package com.rest.service;

import com.rest.model.Userdata;
import com.rest.repositories.UserdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userdataService")
@Transactional
public class UserdataServiceImpl implements UserdataService {

    @Autowired
    private UserdataRepository userdataRepository;

    public Userdata findById(Long id) {
        return userdataRepository.findOne(id);
    }

    public Userdata findByName(String name) {
        return userdataRepository.findByName(name);
    }

    public void saveUserdata(Userdata userdata) {
        userdataRepository.save(userdata);
    }

    public void updateUserdata(Userdata userdata) {
        saveUserdata(userdata);
    }

    public void deleteUserdataById(Long id) {
        userdataRepository.delete(id);
    }

    public void deleteAllUserdatas() {
        userdataRepository.deleteAll();
    }

    public List<Userdata> findAllUserdatas() {
        return userdataRepository.findAll();
    }

    public boolean isUserdataExists(Userdata userdata) {
        return findByName(userdata.getName()) != null;
    }
}
