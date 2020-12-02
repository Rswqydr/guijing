package com.xiannvzuo.guijing.service;

import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import com.xiannvzuo.guijing.controller.vo.MallOrderItemVO;
import com.xiannvzuo.guijing.controller.vo.MallShoppingCartItemVO;
import com.xiannvzuo.guijing.controller.vo.MallUserVO;
import com.xiannvzuo.guijing.entity.GuijingOrder;
import com.xiannvzuo.guijing.entity.GuijingShoppingCart;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;

import java.util.List;

public interface GuijingOrderService {
    /**
     * 保存订单，这些地方需要注意，因为它涉及多个实体对象，既有表现层对象VO（特指由前端页面封装的），也由其他entity层的实体对象
     * 需要体现将多个实体类也就是表结合的特点
     */
    String saveOrder(MallUserVO mallUserVO, List<MallShoppingCartItemVO> myShoppingCartItem);

    GuijingOrder getOrderByOrderNo(String orderNo);

    String paySuccess(String orderNo, int payType);

    PageResult getMyOrder(PageQueryUtil pageQueryUtil);

    /*
    手动取消订单
     */
    String cancelOrder(String orderNo, Long userId);
    /*
    完成订单
     */
    String finishOrder(String orderNo, Long userId);
    /**
     *后台获取订单分页
     */
    PageResult getGuijingOrderPage(PageQueryUtil pageQueryUtil);
    /*
    修改订单信息
     */
    String updateOrderInfo(GuijingOrder guijingOrder);
    /*
    配货
     */
    String checkDown(List<Long> ids);

    /*
    出库
     */
    String checkOut(List<Long> ids);

    /*
    关闭订单
     */
    String closeOrder(List<Long> ids);

    List<MallOrderItemVO> getOrderItems(Long id);


}
