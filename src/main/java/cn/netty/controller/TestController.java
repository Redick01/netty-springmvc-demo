package cn.netty.controller;

import cn.netty.entity.User;
import cn.netty.service.impl.UserServiceImpl;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by liu_penghui on 2017/11/4.
 */
@RestController
public class TestController {

    @Resource
    private UserServiceImpl userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/hello/{id}")
    @ResponseBody
    public User hello(@PathVariable int id){
        return userService.getUserById(id);
    }

    @RequestMapping(value = "/add")
    @ResponseBody
    public JSONObject add(HttpServletRequest request) {
        User user = new User();
        user.setId(Integer.parseInt(request.getParameter("id")));
        user.setName(request.getParameter("name"));
        user.setPassword(request.getParameter("password"));
        user.setAge(Integer.parseInt(request.getParameter("age")));
        //String a = request.getParameter("id");
        int i = userService.insert(user);
        JSONObject jb = new JSONObject();
        jb.put("code", i);
        jb.put("message", "insert successfully...");
        return jb;
    }
    @RequestMapping(value = "/setRedis")
    @ResponseBody
    @CacheEvict(value = {}, allEntries = true)
    public String setData(){

        String key = "111";
        String value = "liuhui";
        redisTemplate.opsForValue().set(key,value);
        return value;
    }
    @RequestMapping(value = "getRedis")
    @ResponseBody
    public String getData(){
        return (String) redisTemplate.opsForValue().get("111");
    }
}
