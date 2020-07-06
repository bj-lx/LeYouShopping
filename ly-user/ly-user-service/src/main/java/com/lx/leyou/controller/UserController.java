package com.lx.leyou.controller;

import com.lx.leyou.entity.User;
import com.lx.leyou.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据类型查询用户
     * @param data 类型为1是用户名， 类型为2是手机号
     * @param type 类型
     * @return boolean值
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> queryUser(
            @PathVariable("data") String data,
            @PathVariable("type") Integer type) {

        Boolean flag = this.userService.queryUser(data, type);
        if (flag == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(flag);
    }

    /**
     * 给手机号发送短信
     * @param phone 手机号
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        this.userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户信息和验证码注册用户
     * @param user 用户信息
     * @param code 验证码
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(User user, @RequestParam("code") String code) {
        Boolean flag = this.userService.register(user, code);
        if (!flag) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    @GetMapping("/query")
    public ResponseEntity<User> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        User user = this.userService.login(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }
}
