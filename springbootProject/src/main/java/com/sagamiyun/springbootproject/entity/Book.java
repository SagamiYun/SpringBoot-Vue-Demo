package com.sagamiyun.springbootproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

//TableName此注释用于映射数据库中表的名字
@TableName("book")
@Data
public class Book {

    @TableId(type = IdType.AUTO)       //绑定ID并自增
    private Integer id;
    private String name;
    private BigDecimal price;
    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date create_time;
    private String cover;
    private Integer user_id;

    @TableField(exist = false)
    private String username;

}
