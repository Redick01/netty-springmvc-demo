package cn.netty.service.impl;

import cn.netty.dao.UserDao;
import cn.netty.entity.User;
import cn.netty.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu_penghui on 2017/11/8.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public User getUserById(String id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("orderParam", "id");
        return userDao.selectByPrimaryKey(map);
    }

    public void insert(User user) {
        userDao.insert(user);

    }
}
