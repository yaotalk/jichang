package com.minivision.cameraplat.repository;

import com.minivision.cameraplat.domain.ClientUser;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ClientUserRepository extends PagingAndSortingRepository<ClientUser,Long> {

    List<ClientUser> findAll();

    ClientUser findByUsername(String username);
}
