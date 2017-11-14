package cn.netty.dao;

import cn.netty.entity.User;

/**
 * Created by liu_penghui on 2017/11/7.
 */
public interface UserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
