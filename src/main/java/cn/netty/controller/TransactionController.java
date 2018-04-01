package cn.netty.controller;

import cn.netty.entity.Book;
import cn.netty.entity.User;
import cn.netty.service.impl.BookServiceImpl;
import cn.netty.service.impl.UserServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by liu_penghui on 2018/3/8.
 */
@RestController
@Transactional
public class TransactionController {

    @Resource
    private BookServiceImpl bookService;

    @Resource
    private UserServiceImpl userService;

    @RequestMapping(value = "/trans")
    public String transTest() {
        insertBook();
        try {
            throw new Exception("Exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setAge(12);
        user.setPassword("12342234");
        user.setName("234323");
        userService.insert(user);

        return "000";

    }
    public void insertBook() {
        Book book = new Book();
        book.setName("第一本");
        book.setAuthor("第一人");
        book.setIsbn("第一");
        bookService.insert(book);
    }
}
