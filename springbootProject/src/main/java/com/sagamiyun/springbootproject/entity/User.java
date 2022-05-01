package com.sagamiyun.springbootproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

//TableName此注释用于映射数据库中表的名字
@TableName("user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @TableId(type = IdType.AUTO)       //绑定ID并自增
    private Integer id;

    private String username ;
    private String password;
    private String nick_name;
    private Integer age;
    private String sex;
    private String address ;

    private String avatar;

    @TableField(exist = false)
    private List<Integer> roles;

    @TableField(exist = false)
    private List<Book> bookList;

    @TableField(exist = false)
    private String token;

    private BigDecimal account;

    @TableField(exist = false)
    private Set<Permission> permissions;
}
