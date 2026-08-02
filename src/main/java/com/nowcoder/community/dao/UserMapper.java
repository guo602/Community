package com.nowcoder.community.dao;

import org.apache.ibatis.annotations.Mapper;
import com.nowcoder.community.entity.User;

import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String name);

    User selectByEmail(String email);

    int insertUser(User user);
    int updateStatus(int id,int status);

    int updateAvatar(int id,String avatarUrl);

    int updatePassword(int id,String password);
}
