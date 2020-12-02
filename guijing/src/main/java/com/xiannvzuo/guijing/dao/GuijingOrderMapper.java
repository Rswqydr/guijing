package com.xiannvzuo.guijing.dao;

import com.xiannvzuo.guijing.entity.GuijingOrder;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
public interface GuijingOrderMapper {
    /**
     * 思考需要那些基础操作 + 常用操作 + 业务需要
     */
    int deleteByPrimaryKey(Long orderId);

    // 加
    int insertSelective(GuijingOrder guijingOrder);

    // 查
    GuijingOrder selectByPrimaryKey(Long orderId);

    GuijingOrder selectByOrderNo(String orderNo);

    // update
    int updateOrderSelective(GuijingOrder order);

    List<GuijingOrder> findGuijingOrderList(PageQueryUtil pageQueryUtil);

    int getTotalGuijingOrder(PageQueryUtil pageQueryUtil);

    List<GuijingOrder> getByPrimaryKeys(@Param("orderIds") List<Long> ids);

    // 批量出库
    int checkOut(@Param("orderIds") List<Long> orderIds);

    // 批量设定订单状态 主要是关闭订单
    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    // 检查完成情况
    int checkDone(@Param("orderIds")List<Long> ids);






}
