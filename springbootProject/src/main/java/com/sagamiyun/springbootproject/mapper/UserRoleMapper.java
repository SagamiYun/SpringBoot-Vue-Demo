package com.sagamiyun.springbootproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sagamiyun.springbootproject.entity.UserRole;
import org.apache.ibatis.annotations.Select;

/**
 * @version 1.0
 * @Description 用户角色数据层
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("delete from user_role where user_id=#{user_id}")
    void deleteByUserRoleId(Long user_id);
}
