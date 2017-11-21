package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.ClientUser;
import com.minivision.cameraplat.repository.ClientUserRepository;
import com.minivision.cameraplat.rest.result.system.UserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientUserServiceImpl implements ClientUserService {

    @Autowired
    private ClientUserRepository clientUserRepository;

   @Override
   public List<ClientUser> findAll(){
      return clientUserRepository.findAll();
   }

    @Override public void save(ClientUser clientUser) {
           clientUserRepository.save(clientUser);
    }

    @Override public void update(ClientUser clientUser) {
           clientUserRepository.save(clientUser);
    }

    @Override public void delete(ClientUser clientUser) {
           clientUserRepository.delete(clientUser);
    }

    public UserResult validateUser(String username,String password){
        if(null == username || "".equals(username.trim())){
            return  new UserResult(400,"请填写用户名");
        }
        if(null == password || "".equals(password.trim())){
            return  new UserResult(400,"请填写密码");
        }
        ClientUser clientUser  = clientUserRepository.findByUsername(username);
        if(clientUser == null || !clientUser.getPassword().equals(password)){
            return  new UserResult(401,"用户名或密码错误");
        }
        return new UserResult(200,"登录成功");
    }

    @Override public ClientUser findByUsername(String username) {
        return clientUserRepository.findByUsername(username);
    }

}
