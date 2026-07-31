package com.nowcoder.community.service;

import ch.qos.logback.core.util.StringUtil;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.headurl.library}")
    private String headurlLibrary;

    @Value("${server.servlet.context-path}")
    private String contextPath;




    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        //null value
        if(user==null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号名不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码名不能为空");
            return map;
        }



        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");

            return map;
        }

        //验证账号唯一性
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号名已被注册");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱名已被注册");
            return map;
        }

        //register
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.MD5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format(headurlLibrary + "/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8091/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" +user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation.html",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_DUPLICATE;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAIL;
        }

    }

    public Map<String,Object> login(String username,String password, int  expiredSeconds){
        Map<String,Object> map =new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //合法性验证
        User user = userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg","该账号名不存在");
            return map;
        }

        if (user.getStatus()==0){
            map.put("usernameMsg","该账号尚未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.MD5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码错误");
            return map;
        }

        //登录成功
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        System.out.println("expiredHours:" + expiredSeconds/3600);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));

        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }
}
