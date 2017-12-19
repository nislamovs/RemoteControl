package com.rest.repositories;

import com.rest.model.Userdata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserdataRepository extends JpaRepository<Userdata, Long> {
    Userdata findByName(String name);
}