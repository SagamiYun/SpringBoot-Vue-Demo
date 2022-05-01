package com.sagamiyun.springbootproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sagamiyun.springbootproject.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper extends BaseMapper<Order> {

    @Update("update t_order set state = #{state},pay_time = #{pay_time} where order_no = #{order_no}")
    int updateState(@Param("trade_no") String trade_no, @Param("state") int state, @Param("pay_time") String pay_time);
}
