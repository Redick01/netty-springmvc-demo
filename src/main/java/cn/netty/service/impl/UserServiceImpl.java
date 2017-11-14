package cn.netty.service.impl;

import cn.netty.dao.UserDao;
import cn.netty.entity.User;
import cn.netty.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by liu_penghui on 2017/11/8.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    public User getUserById(Integer id) {
        return userDao.selectByPrimaryKey(id);
    }

    public int insert(User user) {
        return userDao.insert(user);
    }
}
