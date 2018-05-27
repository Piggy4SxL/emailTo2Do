package sxl.mp.et2d.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sxl.mp.et2d.entity.Todo;
import sxl.mp.et2d.service.MailService;
import sxl.mp.et2d.util.HttpServletRequestUtil;
import sxl.mp.et2d.util.MD5;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SxL
 *         Created on 2017/12/29.
 */
@Controller
@RequestMapping("/2do")
public class EmailController {
    @Autowired
    private MailService mailService;
    private Map<String, String> allowUsers = new HashMap<>();
    private StringBuffer autoRegisterBuffer = new StringBuffer();
    private String[] autoRegister;
    private static final String SEPARATOR = ",";

    @RequestMapping("")
    private String emailForm() {
        return "email/email";
    }

    @PostMapping("/send")
    @ResponseBody
    private Map<String, Object> addTodo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ObjectMapper mapper = new ObjectMapper();
        Todo todo = new Todo();
        String todoStr = HttpServletRequestUtil.getString(request, "todo");

        try {
            todo = mapper.readValue(todoStr, Todo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String title = todo.getTitle() + " list(" + todo.getList() + ") tag(" + (todo.getTag().isEmpty() || todo.getTag() == null ? "default" : todo.getTag()) + ") start(" +
                simpleDateFormat.format(todo.getStart()) + ") due(" + simpleDateFormat.format(todo.getDue()) + ") priority(" + todo.getPriority() +
                ") action(" + todo.getAction() + ")";
        String content = todo.getContent();

        try {
            mailService.sendSimple(todo.getEmail(), title, content);
            modelMap.put("success", true);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
        }

        return modelMap;
    }

    @PostMapping("/login")
    @ResponseBody
    private Map<String, Object> login(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        String phone = HttpServletRequestUtil.getString(request, "phone");
        String password = HttpServletRequestUtil.getString(request, "password");

        initStringBuffer(autoRegisterBuffer);
        autoRegister = StringBufferToString(autoRegisterBuffer);

        if (allowUsers.containsKey(phone)) {
            if (allowUsers.get(phone).equals(MD5.getMd5(password)))
                modelMap.put("success", true);
            else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "手机或密码错误");
            }
        } else {
            for (String p : autoRegister){
                if (p.equals(phone)) {
                    allowUsers = fillUser(phone, password);
                    modelMap.put("success", true);

                    return modelMap;
                }
            }
            modelMap.put("success", false);
            modelMap.put("errMsg", "手机号不存在");
        }

        return modelMap;
    }

    @PostMapping("/addUser")
    @ResponseBody
    private Map<String, Object> addUser(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        String phone = HttpServletRequestUtil.getString(request, "phone");
        String password = HttpServletRequestUtil.getString(request, "password");
        int count = allowUsers.size();

        allowUsers = fillUser(phone, password);

        if (count == allowUsers.size() - 1) {
            autoRegisterBuffer.append(phone + SEPARATOR);
            modelMap.put("success", true);

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "注册失败");
        }

        return modelMap;
    }

    @PutMapping("/modifyPwd")
    @ResponseBody
    private Map<String, Object> modifyPwd(@RequestBody Map<String, Object> reqMap) {
        Map<String, Object> modelMap = new HashMap<>();
        String phone = reqMap.get("phone").toString();
        String password = reqMap.get("password").toString();
        String newPassword = reqMap.get("newPassword").toString();
        int count = allowUsers.size();

        if (MD5.getMd5(password).equals(MD5.getMd5(newPassword))){
            modelMap.put("success", false);
            modelMap.put("errMsg", "新旧密码不能相同");

            return modelMap;
        }

        if (allowUsers.get(phone).equals(MD5.getMd5(password))) {
            allowUsers.remove(phone);
            allowUsers = fillUser(phone, newPassword);

            if (count == allowUsers.size())
                modelMap.put("success", true);
            else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "密码修改失败");
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "原密码输入错误");
        }

        return modelMap;
    }

    @DeleteMapping("/delUser")
    @ResponseBody
    private Map<String, Object> delUser(@RequestBody Map<String, Object> reqMap) {
        Map<String, Object> modelMap = new HashMap<>();
        String phone = reqMap.get("phone").toString();

        if (allowUsers.containsKey(phone)) {
            int count = allowUsers.size();

            allowUsers.remove(phone);

            if (count == allowUsers.size() + 1)
                modelMap.put("success", true);
            else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "重置失败");
            }
        }else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "该账户已被重置");
        }
        return modelMap;
    }

    @DeleteMapping("/removeUser")
    @ResponseBody
    private Map<String, Object> removeUser(@RequestBody Map<String, Object> reqMap) {
        Map<String, Object> modelMap = new HashMap<>();
        String phone = reqMap.get("phone").toString();
        int indexOf = autoRegisterBuffer.indexOf(phone);

        if (allowUsers.containsKey(phone)) {
            int count = allowUsers.size();

            allowUsers.remove(phone);
            autoRegisterBuffer.delete(indexOf, indexOf + 12);

            if (count == allowUsers.size() + 1)
                modelMap.put("success", true);
            else {
                modelMap.put("success", false);
                modelMap.put("errMsg", "注销失败");
            }
        }else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "该账户已被注销");
        }
        return modelMap;
    }


    @GetMapping("/initUser")
    @ResponseBody
    private void initUser() {
        allowUsers.clear();
        if (!allowUsers.containsKey("15620998276"))
            allowUsers.put("15620998276", "9sq6esl25s5ly9sbb52se6s5es5e260b");

        System.out.println(allowUsers);
    }

    @GetMapping("/getUser")
    @ResponseBody
    private Map<String, Object> getUser() {
        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("allowUsers", allowUsers);

        return modelMap;
    }

    @GetMapping("/getAllUsers")
    @ResponseBody
    private Map<String, Object> getAllUsers(){
        Map<String, Object> modelMap = new HashMap<>();

        initStringBuffer(autoRegisterBuffer);
        autoRegister = StringBufferToString(autoRegisterBuffer);

        modelMap.put("phones", autoRegister);

        return modelMap;
    }

    private Map<String, String> fillUser(String phone, String password) {
        if (!allowUsers.containsKey(phone))
            allowUsers.put(phone, MD5.getMd5(password));

        return allowUsers;
    }

    private String[] StringBufferToString(StringBuffer autoRegisterBuffer){
        return autoRegisterBuffer.toString().split(",");
    }

    private void initStringBuffer(StringBuffer autoRegisterBuffer){
        if (autoRegisterBuffer.length() == 0) {
            autoRegisterBuffer.append("15620998276,");
            autoRegisterBuffer.append("13352029150,");
        }
    }
}
