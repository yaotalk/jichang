package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.User;
import com.minivision.cameraplat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StandardPasswordEncoder passwordEncoder;
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
      if(user.getPassword().equals("******")) {
          User oldUser = userRepository.findOne(user.getId());
          user.setPassword(oldUser.getPassword());
      }
      else   user.setPassword(passwordEncoder.encode(user.getPassword()));
      return userRepository.save(user);
    }

    @Override
    public User create(User user) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      return userRepository.save(user);
    }

    @Override public void disable(User user) {
        user = userRepository.findOne(user.getId());
        if(user.isEnabled()) {
            user.setEnabled(false);
        }
        else user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void delete(User user) {
         userRepository.delete(user);
    }

    @Override public User findOne(long id) {
        return userRepository.findOne(id);
    }

    @Override
    public User findByUsername(String username) {
      return userRepository.findByUsername(username);
    }
}
