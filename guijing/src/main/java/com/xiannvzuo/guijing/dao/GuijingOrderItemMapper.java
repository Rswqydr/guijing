package com.xiannvzuo.guijing.dao;

import com.xiannvzuo.guijing.entity.GuijingOrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GuijingOrderItemMapper {
    /**
     *删除一条订单项
     */
    int deleteOrderItemByPrimaryKey(Long orderItemId);

    int insertOrderItemSelective(GuijingOrderItem guijingOrderItem);

    GuijingOrderItem selectByPrimaryKey(Long orderItemId);

    List<GuijingOrderItem> getOrderItemByOrderId(Long orderId);

    // 通过订单的ids列表来获取一系列订单项
    List<GuijingOrderItem> getByOrderIds(@Param("orderIds")List<Long> orderIds);
    // 插入一列订单项

    int batchInsertOrderItem(@Param("orderItems")List<GuijingOrderItem> guijingOrderItemList);

    // 通过主键更新
    int updateByPrimaryKeySelective(GuijingOrderItem guijingOrderItem);

}
