package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;
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
}
