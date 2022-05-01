package com.sagamiyun.springbootproject.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sagamiyun.springbootproject.common.Result;
import com.sagamiyun.springbootproject.entity.Permission;
import com.sagamiyun.springbootproject.entity.RolePermission;
import com.sagamiyun.springbootproject.entity.User;
import com.sagamiyun.springbootproject.entity.UserRole;
import com.sagamiyun.springbootproject.enums.PwdEnum;
import com.sagamiyun.springbootproject.enums.RoleEnum;
import com.sagamiyun.springbootproject.mapper.PermissionMapper;
import com.sagamiyun.springbootproject.mapper.RoleMapper;
import com.sagamiyun.springbootproject.mapper.UserMapper;
import com.sagamiyun.springbootproject.mapper.UserRoleMapper;
import com.sagamiyun.springbootproject.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    // Resource注解负责将Mapper引入
    @Resource
    UserMapper userMapper;
    @Resource
    RoleMapper roleMapper;
    @Resource
    PermissionMapper permissionMapper;
    @Resource
    UserRoleMapper userRoleMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;  //注入bcryct加密


    // RequestBody此注解的含义为将Json文件转换成Java对象
    @PostMapping
    public Result save(@RequestBody User user) {
        if (user.getPassword() == null) {
            user.setPassword(bCryptPasswordEncoder.encode(PwdEnum.PASSWORD.getPassword()));
        }
        userMapper.insert(user);

        UserRole userRole = UserRole.builder()
                .user_id(user.getId())
                .role_id(RoleEnum.USER.getRole_id())
                .build();
        userRoleMapper.insert(userRole);

        return Result.success();
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User userParam) {

        User userPwd = userMapper.selectByName(userParam.getUsername());
        User res = null;
        try {
            res = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getUsername, userParam.getUsername())
                    .eq(User::getPassword, userPwd.getPassword()));
        } catch (Exception e) {
            return Result.error("-1", "无此用户");
        }
        // QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // queryWrapper.eq("username", userParam.getUsername());
        // queryWrapper.eq("password", userParam.getPassword());
        // User res = userMapper.selectOne(queryWrapper);

        // 判断密码是否正确
        if (!bCryptPasswordEncoder.matches(userParam.getPassword(), userPwd.getPassword())) {
            return Result.error("-1", "密码错误");
        }
        if (res == null) {
            return Result.error("-1", "用户名或密码错误");
        }

        HashSet<Permission> permissionSet = new HashSet<>();
        // 1. 从user_role表通过用户id查询所有的角色信息
        Integer userId = res.getId();
        List<UserRole> userRoles = roleMapper.getUserRoleByUserId(userId);
        // 设置角色id
        res.setRoles(userRoles.stream().map(UserRole::getRole_id).distinct().collect(Collectors.toList()));
        for (UserRole userRole : userRoles) {
            // 2.根据roleId从role_permission表查询出所有的permissionId
            List<RolePermission> rolePermissions = permissionMapper.getRolePermissionByRoleId(userRole.getRole_id());
            for (RolePermission rolePermission : rolePermissions) {
                Integer permissionId = rolePermission.getPermission_id();
                // 3. 根据permissionId查询permission信息
                Permission permission = permissionMapper.selectById(permissionId);
                permissionSet.add(permission);
            }
        }
        // 对资源根据id进行排序
        LinkedHashSet<Permission> sortedSet = permissionSet.stream().sorted(Comparator.comparing(Permission::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
        //设置当前用户的资源信息
        res.setPermissions(sortedSet);

        // 生成token
        String token = TokenUtils.genToken(res);
        res.setToken(token);
        return Result.success(res);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, user.getUsername()));
        if (res != null) {
            return Result.error("-1", "用户名重复");
        }
        // if(user.getPassword()==null){
        //     user.setPassword("123456");
        // }

        User userInfo = User.builder()
                .username(user.getUsername())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .nick_name("用户" + IdWorker.getId())
                .build();

        userMapper.insert(userInfo);

        UserRole userRole = UserRole.builder()
                .user_id(userInfo.getId())
                .role_id(RoleEnum.USER.getRole_id())
                .build();
        userRoleMapper.insert(userRole);

        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody User user) {
        userMapper.updateById(user);
        return Result.success();
    }

    @PutMapping("/pass")
    public Result<?> pass(@RequestBody Map<String, Object> map) {
        User user = userMapper.selectById((Integer) map.get("user_id"));
        if (user == null) {
            return Result.error("-1", "未找到用户");
        }
        if (!bCryptPasswordEncoder.matches(map.get("password").toString(), user.getPassword())) {
            return Result.error("-1", "密码错误");
        }
        map.put("newPass", (bCryptPasswordEncoder.encode(map.get("newPass").toString())));
        userMapper.updatePass(map);
        return Result.success();
    }

    // 改变权限接口
    @PutMapping("/changeRole")
    public Result<?> changeRole(@RequestBody User user) {
        // 先根据角色id删除所有的角色跟权限的绑定关系
        roleMapper.deleteRoleByUserId(user.getId());
        // 再新增 新的绑定关系
        for (Integer roleId : user.getRoles()) {
            roleMapper.insertUserRole(user.getId(), roleId);
        }

        // 获取当前登录用户的角色id列表
        User currentUser = getUser();
        // 如果当前登录用户的角色列表包含需要修改的角色id，那么就重新登录
        if (user.getId().equals(currentUser.getId())) {
            return Result.success(true);
        }
        //如果不包含，则返回false，不需要重新登录。
        return Result.success(false);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userRoleMapper.deleteByUserRoleId(id);
        userMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        // 1. 从user_role表通过用户id查询所有的角色信息
        Integer userId = user.getId();
        List<UserRole> userRoles = roleMapper.getUserRoleByUserId(userId);

        return Result.success(userMapper.selectById(id) + "," + userRoles);
    }

    /**
     * 用户分页列表查询，包含书籍表的一对多查询
     *
     * @param pageNum
     * @param pageSize
     * @param search
     * @return
     */
    @GetMapping
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String search) {
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().orderByAsc(User::getId);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(User::getUsername, search);
        }
        Page<User> userPage = userMapper.findPage(new Page<>(pageNum, pageSize), search);
        // 设置用户的角色id列表
        for (User record : userPage.getRecords()) {
            List<UserRole> roles = roleMapper.getUserRoleByUserId(record.getId());
            List<Integer> roleIds = roles.stream().map(UserRole::getRole_id).distinct().collect(Collectors.toList());
            record.setRoles(roleIds);
        }
        return Result.success(userPage);
    }

    /**
     * Excel导出
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<User> all = userMapper.selectList(null);
        for (User user : all) {
            Map<String, Object> row1 = new LinkedHashMap<>();
            row1.put("用户名", user.getUsername());
            row1.put("昵称", user.getNick_name());
            row1.put("年龄", user.getAge());
            row1.put("性别", user.getSex());
            row1.put("地址", user.getAddress());
            list.add(row1);
        }

        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }

    /**
     * Excel导入
     * 导入的模板可以使用 Excel导出的文件
     *
     * @param file Excel
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    public Result<?> upload(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<List<Object>> lists = ExcelUtil.getReader(inputStream).read(1);
        List<User> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setNick_name(row.get(1).toString());
            user.setAge(Integer.valueOf(row.get(2).toString()));
            user.setSex(row.get(3).toString());
            user.setAddress(row.get(4).toString());
            saveList.add(user);
        }
        for (User user : saveList) {
            userMapper.insert(user);
        }
        return Result.success();
    }

    /**
     * 统计数据
     *
     * @return
     */
    @GetMapping("/count")
    public Result<?> count() {
        // User user = getUser(); // 当前登录的用户信息
        return Result.success(userMapper.countAddress());
    }


}
