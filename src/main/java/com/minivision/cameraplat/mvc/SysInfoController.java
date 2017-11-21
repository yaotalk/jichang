package com.minivision.cameraplat.mvc;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.minivision.cameraplat.config.OpAnnotation;
import com.minivision.cameraplat.domain.ClientUser;
import com.minivision.cameraplat.domain.SysLog;
import com.minivision.cameraplat.param.PageParam;
import com.minivision.cameraplat.service.ClientUserService;
import com.minivision.cameraplat.service.SysLogService;
import com.minivision.cameraplat.task.ExImportTask;

@RestController
public class SysInfoController {

  @Autowired
  private SysLogService sysLogService;

  @Autowired
  private ClientUserService clientUserService;

  /*@Value("${system.store.people}")
  private String filepath;*/

  @Autowired
  private ExImportTask exImportTask;

  @GetMapping("sysinfo")
  public ModelAndView sysInfo() {
    return new ModelAndView("sysmanage/sysinfo");
  }

  @GetMapping(value = "syslog")
  public ModelAndView getSyslog() {
    return new ModelAndView("sysmanage/sysloglist");
  }

  @GetMapping(value = "syslog", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Map<String, Object> getSyslog(PageParam pageParam, String modelName, String startTime,
      String endTime) throws ParseException {
    Page<SysLog> pages = sysLogService.findByPage(pageParam, modelName, startTime, endTime);
    List<SysLog> sysLogs = pages.getContent();
    long totalcout = pages.getTotalElements();
    Map<String, Object> map = new HashMap<>();
    map.put("rows", sysLogs);
    map.put("total", totalcout);
    return map;
  }

  @GetMapping("clientuser")
  public ModelAndView clientUserPage() {
    return new ModelAndView("sysmanage/clientuserlist");
  }

  @GetMapping(value = "clientuser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<ClientUser> userlist() {
    return clientUserService.findAll();
  }

  @PostMapping("clientuser")
  @OpAnnotation(modelName = "客户端用户", opration = "新增客户端用户")
  public String addClientUser(ClientUser clientUser) {
    ClientUser oldUser = clientUserService.findByUsername(clientUser.getUsername());
    if (oldUser != null) {
      return "failed,username can not be dupplicate";
    }
    clientUserService.save(clientUser);
    return "success";
  }

  @PatchMapping("clientuser")
  @OpAnnotation(modelName = "客户端用户", opration = "编辑客户端用户")
  public String updateClientUser(ClientUser clientUser) {
    ClientUser oldUser = clientUserService.findByUsername(clientUser.getUsername());
    if (oldUser != null && !oldUser.getId().equals(clientUser.getId())) {
      return "failed,username can not be dupplicate";
    }
    clientUserService.update(clientUser);
    return "success";
  }

  @DeleteMapping("clientuser")
  @OpAnnotation(modelName = "客户端用户", opration = "删除客户端用户")
  public String deleteClientUser(ClientUser clientUser) {
    clientUserService.delete(clientUser);
    return "success";
  }


  @GetMapping("backup")
  public ModelAndView page() {
    return new ModelAndView("sysmanage/sysback").addObject("status", exImportTask.getStatus())
        .addObject("exportStatus", exImportTask.getExportStatus());
  }

  @GetMapping("export")
  public String export() {
    return exImportTask.export();
  }

  @PostMapping("import")
  public String databack(String filepath) {
    File fileDir = new File(filepath);
    if (!fileDir.exists() || !fileDir.isDirectory()) {
      return "failed";
    }
    List<File> files =
        (List<File>) FileUtils.listFiles(new File(filepath), new String[] {"zip", "rar"}, false);
    if (files.size() > 0) {
      File zipFile = files.get(0);
      return exImportTask.importTask(zipFile);
    } else
      return "failed";

  }
}

