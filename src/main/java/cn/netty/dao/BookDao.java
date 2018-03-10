package cn.netty.dao;

import cn.netty.entity.Book;

public interface BookDao {
    int deleteByPrimaryKey(Integer id);

    void insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);
}