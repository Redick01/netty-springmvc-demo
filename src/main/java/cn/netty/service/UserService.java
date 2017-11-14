package cn.netty.service;

import cn.netty.entity.User;

/**
 * Created by liu_penghui on 2017/11/8.
 */
public interface UserService {
    public User getUserById(Integer id);

    public int insert(User user);
}
