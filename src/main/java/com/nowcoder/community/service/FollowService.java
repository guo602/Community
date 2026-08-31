package com.nowcoder.community.service;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    public void follow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public void unfollow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);
                return operations.exec();
            }
        });
    }

    //查询某个用户关注的实体数量
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询某个实体粉丝数
    public long findFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户有没有关注某实体
    public  boolean hasFollow(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null ;

    }

    //查询某用户关注的人
    public List<Map<String,Object>> findFollowees(int userId, int offset,int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset + limit - 1);
        if (ids == null){
            return null;
        }else{
            List<Map<String,Object>> list = new ArrayList<>();
            for(Integer id :ids){
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById(id);
                map.put("user",user);
                Double score = redisTemplate.opsForZSet().score(followeeKey,id);
                map.put("followTime",new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }

    }

    //查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId, int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset + limit - 1);
        if (ids == null){
            return null;
        }else{
            List<Map<String,Object>> list = new ArrayList<>();
            for(Integer id :ids){
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById(id);
                map.put("user",user);
                Double score = redisTemplate.opsForZSet().score(followerKey,id);
                map.put("followTime",new Date(score.longValue()));
                list.add(map);
            }
            return list;
        }

    }



}
