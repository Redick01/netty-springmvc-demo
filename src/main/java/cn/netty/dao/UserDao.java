package cn.netty.dao;

import cn.netty.entity.User;

import java.util.Map;

/**
 * Created by liu_penghui on 2017/11/7.
 */
public interface UserDao {
    int deleteByPrimaryKey(Integer id);

    void insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Map<String, String> map);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
