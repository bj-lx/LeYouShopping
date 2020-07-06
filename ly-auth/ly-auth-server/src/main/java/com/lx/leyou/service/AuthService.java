package com.lx.leyou.service;

import com.lx.leyou.client.UserClient;
import com.lx.leyou.config.JwtProperties;
import com.lx.leyou.entity.User;
import com.lx.leyou.pojo.UserInfo;
import com.lx.leyou.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    public String login(String username, String password) {
        try {
            //根据用户名和密码查询用户
            User user = this.userClient.login(username, password);
            //判断用户
            if (user == null) {
                return null;
            }
            //设置token
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());

            return JwtUtils.generateToken(userInfo, this.prop.getPrivateKey(), this.prop.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
