package com.sagamiyun.springbootproject.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sagamiyun.springbootproject.common.Result;
import com.sagamiyun.springbootproject.entity.News;
import com.sagamiyun.springbootproject.mapper.NewsMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;


@RestController
@RequestMapping("/news")
public class NewsController extends BaseController{

    // Resource注解负责将Mapper引入
    @Resource
    NewsMapper newsMapper;

    // RequestBody此注解的含义为将Json文件转换成Java对象
    @PostMapping
    public Result<?> save(@RequestBody News news){
        news.setTime(new Date());
        newsMapper.insert(news);
        return Result.success();
    }


    @PutMapping
    public Result<?> update(@RequestBody News news){
        newsMapper.updateById(news);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        newsMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id){
        return Result.success(newsMapper.selectById(id));
    }

    @GetMapping
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String search){
        LambdaQueryWrapper<News> wrapper = Wrappers.<News>lambdaQuery();
        if(StrUtil.isNotBlank(search)){
            wrapper.like(News::getTitle,search);
         }
        Page<News> newsPage = newsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(newsPage);
    }
}
