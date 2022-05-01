package com.sagamiyun.springbootproject.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sagamiyun.springbootproject.common.Result;
import com.sagamiyun.springbootproject.entity.Book;
import com.sagamiyun.springbootproject.entity.Order;
import com.sagamiyun.springbootproject.entity.User;
import com.sagamiyun.springbootproject.mapper.BookMapper;
import com.sagamiyun.springbootproject.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Resource
    OrderMapper OrderMapper;

    @Resource
    BookMapper bookMapper;

    @PostMapping
    public Result<?> save(@RequestBody Order Order) {
        OrderMapper.insert(Order);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Order Order) {
        OrderMapper.updateById(Order);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        OrderMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(OrderMapper.selectById(id));
    }

    @GetMapping("/buy/{bookId}")
    public Result<?> buy(@PathVariable Long bookId) {
        Book book = bookMapper.selectById(bookId);
        String orderNo = IdUtil.getSnowflake().nextIdStr();
        String payUrl = "http://localhost:9090/alipay/pay?subject=" + book.getName() + "&traceNo=" + orderNo + "&totalAmount=" + book.getPrice();

        User user = getUser();
        Order order = new Order();
        order.setOrder_no(orderNo);
        order.setTotal_price(book.getPrice());
        order.setPay_price(book.getPrice());
        order.setTransport_price(BigDecimal.ZERO);
        order.setUser_id(user.getId());
        order.setUsername(user.getUsername());
        order.setName(book.getName());
        save(order);
        // 新建订单，扣减库存
        return Result.success(payUrl);
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        LambdaQueryWrapper<Order> wrapper = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Order::getOrder_no, search);
        }
        Page<Order> OrderPage = OrderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(OrderPage);
    }
}
