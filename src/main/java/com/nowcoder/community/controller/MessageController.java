package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;


    //私信列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User currentUser = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(currentUser.getId()));

        List<Message> conversations = messageService.findConversations(currentUser.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> cvoList = new ArrayList<>();
        if (conversations!=null){
            for (Message message : conversations ){
                    Map<String,Object> map = new HashMap<>();
                    map.put("conversation",message);
                    map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                    map.put("unreadCount",messageService.findLetterUnreadCount(
                            currentUser.getId(),message.getConversationId()));
                    int targetId = currentUser.getId() == message.getFromId() ?
                            message.getToId() : message.getFromId();
                    User targetUser = userService.findUserById(targetId);
                    map.put("target",targetUser);
                    cvoList.add(map);

            }
        }

        model.addAttribute("cvoList",cvoList);
        model.addAttribute("letterUnreadCount",messageService.findLetterUnreadCount(currentUser.getId(),null));

        return "/site/letter";
    }

    @RequestMapping(path="/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page ,Model model){
        //分页信息
        page.setLimit(6);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        List<Message> messageList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());

        List<Map<String,Object>> letters = new ArrayList<>();
        if (messageList!=null){
            for (Message message : messageList ){
                Map<String ,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
            model.addAttribute("target",getLetterTarget(conversationId));
        }
        model.addAttribute("letters",letters);


        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] ids =  conversationId.split("_");
        System.out.println(ids);
        System.out.println(conversationId);
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        return id0 == hostHolder.getUser().getId() ?
                userService.findUserById(id1) : userService.findUserById(id0);
    }

}
