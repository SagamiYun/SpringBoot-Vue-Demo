package com.sagamiyun.springbootproject.utils;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sagamiyun.springbootproject.entity.User;
import com.sagamiyun.springbootproject.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Slf4j
@Component
public class TokenUtils {

    @Autowired
    private UserMapper userMapper;

    private static UserMapper staticUserMapper;

    @PostConstruct
    public void init() {
        staticUserMapper = userMapper;
    }

    public static String genToken(User user) {
        return JWT.create().withAudience(user.getId().toString()) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // 2小时后token过期
                .sign(Algorithm.HMAC256(user.getPassword())); // 以 password 作为 token 的密钥
    }

    public static User getUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            String aud = JWT.decode(token).getAudience().get(0);
            Integer userId = Integer.valueOf(aud);
            return staticUserMapper.selectById(userId);
        } catch (Exception e) {
            log.error("解析token失败", e);
            return null;
        }
    }
}
