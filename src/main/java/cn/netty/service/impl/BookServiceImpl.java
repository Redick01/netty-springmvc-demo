package cn.netty.service.impl;

import cn.netty.dao.BookDao;
import cn.netty.entity.Book;
import cn.netty.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by liu_penghui on 2018/3/8.
 */
@Service("bookService")
public class BookServiceImpl implements BookService {

    @Resource
    private BookDao bookDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void insert(Book record) {
        bookDao.insert(record);
    }

    public int insertSelective(Book record) {
        return bookDao.insertSelective(record);
    }

    public Book selectByPrimaryKey(Integer id) {
        return bookDao.selectByPrimaryKey(id);
    }
}
