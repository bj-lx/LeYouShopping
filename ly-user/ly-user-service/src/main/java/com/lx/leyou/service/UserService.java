package com.lx.leyou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lx.leyou.entity.User;
import com.lx.leyou.mapper.UserMapper;
import com.lx.leyou.utils.CodecUtils;
import com.lx.leyou.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify";
    /**
     * 根据类型查询用户
     * @param data 类型为1是用户名， 类型为2是手机号
     * @param type 类型
     * @return boolean值
     */
    public Boolean queryUser(String data, Integer type) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        if (type == 1) {
            qw.eq("username", data);
        } else if (type == 2) {
            qw.eq("phone", data);
        } else {
            return null;
        }

        return this.userMapper.selectCount(qw) == 0;
    }

    /**
     * 给手机号发送短信
     * @param phone 手机号
     */
    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return;
        }

        //生成验证码
        String code = NumberUtils.generateCode(6);
        //创建消息
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        //发送消息到rabbitMQ
        this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE", "sms.verify.code", msg);
        //保存验证码到redis
        try {
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户信息和验证码注册用户
     * @param user 用户信息
     * @param code 验证码
     * @return 注册结果
     */
    public Boolean register(@Valid User user, String code) {
        String key = KEY_PREFIX + user.getPhone();
        //查询验证码
        String number = this.redisTemplate.opsForValue().get(key);
        //验证验证码
        if (number == null || !number.equals(code)) {
            return false;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        //加密密码
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        //注册用户
        user.setCreated(new Date());
        user.setSalt(salt);

        boolean flag = user.insert();

        //如果注册成功删除redis数据
        if (flag) {
            try {
                this.redisTemplate.delete(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    public User login(String username, String password) {
        //根据用户名查询用户
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", username);
        User user = this.userMapper.selectOne(qw);
        if (user == null) {
            return null;
        }
        //确认密码
        String s = CodecUtils.md5Hex(password, user.getSalt());
        System.out.println(s);
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            return null;
        }
        return user;
    }
}
