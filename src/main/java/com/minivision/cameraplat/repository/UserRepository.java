package com.minivision.cameraplat.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.cameraplat.domain.User;

import java.util.List;
public interface UserRepository extends PagingAndSortingRepository<User,Long> {

    List<User> findAll();

    User findByUsernameAndPassword(String username, String password);
    
    User findByUsername(String username);
}
