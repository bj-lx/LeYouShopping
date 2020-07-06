package com.lx.leyou.api;

import com.lx.leyou.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind .annotation.RequestParam;


public interface UserApi {
    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    @GetMapping("/query")
    User login(@RequestParam("username") String username,
            @RequestParam("password") String password);
}
