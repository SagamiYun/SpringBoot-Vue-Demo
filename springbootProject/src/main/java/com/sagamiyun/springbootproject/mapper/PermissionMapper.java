package com.sagamiyun.springbootproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sagamiyun.springbootproject.entity.Permission;
import com.sagamiyun.springbootproject.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("select * from role_permission where role_id = #{role_id}")
    List<RolePermission> getRolePermissionByRoleId(Integer role_id);

    @Delete("delete from role_permission where role_id = #{role_id}")
    void deletePermissionsByRoleId(Integer role_id);

    @Insert("insert into role_permission(role_id, permission_id) values(#{role_id}, #{permission_id})")
    void insertRoleAndPermission(@Param("role_id") Integer roleId, @Param("permission_id") Integer permission_id);

}
