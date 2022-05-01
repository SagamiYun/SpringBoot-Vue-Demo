package com.sagamiyun.springbootproject.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("user_role")
public class UserRole {
    private Integer user_id;
    private Integer role_id;
}
