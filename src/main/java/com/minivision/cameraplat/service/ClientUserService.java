package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.ClientUser;
import com.minivision.cameraplat.rest.result.system.UserResult;

import java.util.List;

public interface ClientUserService {

    List<ClientUser> findAll();

    void save(ClientUser clientUser);

    void update(ClientUser clientUser);

    void delete(ClientUser clientUser);

    UserResult validateUser(String name,String password);

    ClientUser findByUsername(String username);

}
