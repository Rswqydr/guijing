package com.xiannvzuo.guijing.service;

import com.xiannvzuo.guijing.controller.vo.MallShoppingCartItemVO;
import com.xiannvzuo.guijing.entity.GuijingShoppingCart;

import java.util.List;

public interface GuijingShoppingCartService {

    // 保存商品到购物车中
    String saveGoodsToCart(GuijingShoppingCart guijingShoppingCart);
    // 修改购物车的属性
    String updateGuijingShoppingCart(GuijingShoppingCart guijingShoppingCart);
    // 获取购物车详情
    GuijingShoppingCart getShoppingCartById(Long id);
    // 删除购物车商品
    boolean deleteGoodsById(Long id);
    // 获取个人购物车清单
    List<MallShoppingCartItemVO> getShoppingCartItems(Long userID);
    // 根据userIds和cartId获取对应的购物项记录
    List<MallShoppingCartItemVO> getCartByUserIdsAndCartId(List<Long> cartId, Long userId);

}
