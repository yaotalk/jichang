package com.minivision.cameraplat.mvc;

import com.minivision.cameraplat.config.OpAnnotation;
import com.minivision.cameraplat.domain.User;
import com.minivision.cameraplat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView userpage() {
    return new ModelAndView("sysmanage/userlist");
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<User> userlist() {
    return this.userService.findAll();
  }

  @GetMapping(value = "userInfo",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public  User findOne(String username) {
    return this.userService.findByUsername(username);
  }

  @PostMapping
  @OpAnnotation(modelName = "系统用户",opration = "新增系统用户")
  public String createUser(User user) {
    User oldUser = userService.findByUsername(user.getUsername());
    if(oldUser !=null) {
      return "failed,username can not be dupplicate";
    }
    this.userService.create(user);
    return "success";
  }

  @PatchMapping
  @OpAnnotation(modelName = "系统用户",opration = "编辑系统用户")
  public String updateUser(User user) {
    User oldUser = userService.findByUsername(user.getUsername());
    if(oldUser !=null && !oldUser.getId().equals(user.getId())){
      return "failed,username can not be dupplicate";
    }
    this.userService.update(user);
    return "success";
  }

  @PostMapping(value = "disabled")
  @OpAnnotation(modelName = "系统用户",opration = "禁(启)用系统用户")
  public String disabled(User user) {
    this.userService.disable(user);
    return "success";
  }

  @DeleteMapping
  @OpAnnotation(modelName = "系统用户",opration = "删除系统用户")
  public String delete(User user) {
    this.userService.delete(user);
    return "success";
  }

  @PostMapping("initUser")
  public String initUser(User user){
    User oldUser = userService.findByUsername(user.getUsername());
    if(oldUser !=null) {
      return "failed,username can not be dupplicate";
    }
    this.userService.create(user);
    return "success";
  }
}
