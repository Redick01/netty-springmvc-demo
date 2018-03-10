package cn.netty.service;

import cn.netty.entity.Book;

/**
 * Created by liu_penghui on 2018/3/8.
 */
public interface BookService {
    void insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(Integer id);
}
