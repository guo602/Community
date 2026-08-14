package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Service
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService(){
        System.out.println("instance alphaservice");
    }
    @PostConstruct
    public void init(){
        System.out.println("Initialize Alpha Service");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("Destroy alphaservice");
    }

    public String find(){
        return alphaDao.select();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        User user = new User();
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.MD5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setUsername("alpha");
        user.setAvatarUrl("http://image.nowcoder.com/head/99t.png");
        user.setStatus(0);
        user.setCreateTime(new Date());
        userMapper.insertUser(user);


        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("HEllo");
        post.setContent("new to here");
        post.setCreateTime(new Date());

        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");


        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status){
                User user = new User();
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.MD5("123"+user.getSalt()));
                user.setUsername("beta");
                user.setEmail("beta@qq.com");
                user.setAvatarUrl("http://image.nowcoder.com/head/99t.png");
                user.setStatus(0);
                user.setCreateTime(new Date());
                userMapper.insertUser(user);


                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("yellow");
                post.setContent("new to here");
                post.setCreateTime(new Date());

                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "ok";
            }
        });
    }


}
