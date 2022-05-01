package com.sagamiyun.springbootproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sagamiyun.springbootproject.entity.Role;
import com.sagamiyun.springbootproject.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    @Select("select * from user_role where user_id = #{user_id}")
    List<UserRole> getUserRoleByUserId(Integer user_id);

    @Delete("delete from user_role where user_id = #{user_id}")
    void deleteRoleByUserId(Integer user_id);

    @Insert("insert into user_role(user_id, role_id) values(#{user_id}, #{role_id})")
    void insertUserRole(Integer user_id, Integer role_id);

}
