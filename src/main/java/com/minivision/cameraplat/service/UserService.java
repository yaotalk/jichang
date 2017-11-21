package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User update(User user);

    User create(User user);

    void disable(User user);

    void delete(User user);

    User findOne(long id);
    
    User findByUsername(String username);
}
