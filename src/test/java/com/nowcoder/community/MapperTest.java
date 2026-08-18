package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class )
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user  = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

    }

    @Test
    public void insertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("23456");
        user.setEmail("test@qq.com");
        user.setSalt("abc");
        user.setAvatarUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int row = userMapper.insertUser(user);
        System.out.println(row);
        System.out.println(user.getId());

    }

    @Test
    public void testUpdate(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateAvatar(150,"http://www.noewcoder.com/107.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"34567");
        System.out.println(rows);


    }

    @Test
    public void testSelectPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,0,10);

        for(DiscussPost post : list){
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("d3d");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);

    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("d3d");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("d3d",1);
        loginTicket = loginTicketMapper.selectByTicket("d3d");
        System.out.println(loginTicket);

    }

    @Test
    public void testMessageMapper(){
        System.out.println("********************************************************");

        List<Message> lst = messageMapper.selectConversations(111,2,10);
        for (Message l:lst){
            System.out.println(l);
        }

        System.out.println("********************************************************");

        System.out.println(messageMapper.selectConversationCount(111));

        System.out.println("********************************************************");

        lst = messageMapper.selectLetters("111_112",1,3);
        for (Message l:lst){
            System.out.println(l);
        }

        System.out.println("********************************************************");

        System.out.println(messageMapper.selectLetterCount("111_112"));

        System.out.println("********************************************************");

        System.out.println(messageMapper.selectLetterUnreadCount(111,null));
        System.out.println(messageMapper.selectLetterUnreadCount(131,"111_131"));



    }

}
