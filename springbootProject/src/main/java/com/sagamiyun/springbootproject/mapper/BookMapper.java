package com.sagamiyun.springbootproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sagamiyun.springbootproject.entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMapper extends BaseMapper<Book> {
    // 根据用户id查询图书信息
    List<Book> findByUserId(@Param("user_id") Integer userId);
}
