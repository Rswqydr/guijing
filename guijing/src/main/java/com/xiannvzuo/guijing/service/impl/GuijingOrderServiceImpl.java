package com.xiannvzuo.guijing.service.impl;

import ch.qos.logback.core.joran.event.SaxEventRecorder;
import com.xiannvzuo.guijing.common.*;
import com.xiannvzuo.guijing.controller.vo.MallOrderItemVO;
import com.xiannvzuo.guijing.controller.vo.MallOrderListVO;
import com.xiannvzuo.guijing.controller.vo.MallShoppingCartItemVO;
import com.xiannvzuo.guijing.controller.vo.MallUserVO;
import com.xiannvzuo.guijing.dao.GuijingGoodsMapper;
import com.xiannvzuo.guijing.dao.GuijingOrderItemMapper;
import com.xiannvzuo.guijing.dao.GuijingOrderMapper;
import com.xiannvzuo.guijing.dao.GuijingShoppingCartMapper;
import com.xiannvzuo.guijing.entity.GuijingGoods;
import com.xiannvzuo.guijing.entity.GuijingOrder;
import com.xiannvzuo.guijing.entity.GuijingOrderItem;
import com.xiannvzuo.guijing.entity.StockNumDTO;
import com.xiannvzuo.guijing.service.GuijingOrderService;
import com.xiannvzuo.guijing.util.BeanUtil;
import com.xiannvzuo.guijing.util.NumberUtil;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuijingOrderServiceImpl implements GuijingOrderService {

    @Autowired
    private GuijingOrderMapper guijingOrderMapper;

    @Autowired
    private GuijingGoodsMapper guijingGoodsMapper;

    @Autowired
    private GuijingOrderItemMapper guijingOrderItemMapper;

    @Autowired
    private GuijingShoppingCartMapper guijingShoppingCartMapper;

    @Override
    @Transactional
    public String saveOrder(MallUserVO mallUserVO, List<MallShoppingCartItemVO> myShoppingCartItem) {
        // 注意添加事务支持
        // 获取购物车的项的id列表
        List<Long> orderItemIds = myShoppingCartItem.stream().map(MallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        // 获取绑定的商品的id
        List<Long> goodsIds = myShoppingCartItem.stream().map(MallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        // 根据商品id获取商品
        List<GuijingGoods> guijingGoodsList = guijingGoodsMapper.selectByBatchPrimaryKeys(goodsIds);
        // 判断购物车中商品是否都在售卖中
        List<GuijingGoods> guijingGoodsNotSelling = guijingGoodsList.stream().filter(guijintGoodsTemp->guijintGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(guijingGoodsNotSelling)) {
            MallException.fail(guijingGoodsNotSelling.get(0)+"已下架，无法生成订单");
        }
        // 获取商品的goodsid : goods的映射关系
        Map<Long, GuijingGoods> goodsMap = guijingGoodsList.stream().collect(Collectors.toMap( GuijingGoods::getGoodsId, Function.identity(), (entity1, entiry2)->entity1));
        // 判断购买商品的数量是否小于剩余数量 或者符合限购要求
        for (MallShoppingCartItemVO mallShoppingCartItemVO : myShoppingCartItem) {
            // 如果查出的商品信息不在购物车中，则抛出异常
            if (goodsMap.containsKey(mallShoppingCartItemVO.getGoodsId())) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            // 如果要购买的数量超过库存或者限购量
            if (mallShoppingCartItemVO.getGoodsCount() > guijingGoodsMapper.selectByPrimaryKey(mallShoppingCartItemVO.getGoodsId()).getStockNum()) {
                MallException.fail(ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult());
            }
        }
        // 删除购物车，生成订单
        if (!CollectionUtils.isEmpty(orderItemIds) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(guijingGoodsList)) {
            // 提交删除亲求
            if (guijingShoppingCartMapper.batchDeleteByIds(orderItemIds) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItem, StockNumDTO.class);
                if (guijingGoodsMapper.updateStockNum(stockNumDTOS) < 1) {
                    MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                // 保存订单  设置订单信息
                GuijingOrder guijingOrder = new GuijingOrder();
                guijingOrder.setUserName(mallUserVO.getLoginName());
                guijingOrder.setUserId(mallUserVO.getUserId());
                guijingOrder.setCreateTime(new Date());
                guijingOrder.setUserAddress(mallUserVO.getAddress());
                guijingOrder.setOrderNo(orderNo);
                // 计算总价
                for (MallShoppingCartItemVO mallShoppingCartItemVO : myShoppingCartItem) {
                    priceTotal += mallShoppingCartItemVO.getGoodsCount() * mallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                guijingOrder.setTotalPrice(priceTotal);
                // 还可以设置如额外信息等
                if (guijingOrderMapper.insertSelective(guijingOrder) > 0) {
                    // 生成所有订单快照并保存到数据库中
                    List<GuijingOrderItem> guijingOrderItemList = new ArrayList<>();
                    for (MallShoppingCartItemVO mallShoppingCartItemVO : myShoppingCartItem) {
                        GuijingOrderItem guijingOrderItem = new GuijingOrderItem();
                        BeanUtil.copyProperties(mallShoppingCartItemVO, guijingOrderItem);
                        guijingOrderItem.setOrderId(guijingOrder.getOrderId());
                        guijingOrderItemList.add(guijingOrderItem);

                    }
                    if (guijingOrderItemMapper.batchInsertOrderItem(guijingOrderItemList) > 0) {
                        // 如果保存成功，返回订单号
                            return orderNo;
                    }
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }

            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }




    @Override
    public GuijingOrder getOrderByOrderNo(String orderNo) {
        GuijingOrder guijingOrder = guijingOrderMapper.selectByOrderNo(orderNo);
        if (guijingOrder == null) {
            MallException.fail("无此订单" + orderNo);
        }
        return guijingOrder;
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        /**
         * 此处无实际意义
         */
        GuijingOrder guijingOrder = guijingOrderMapper.selectByOrderNo(orderNo);
        if (guijingOrder != null ) {
            // 检查支付状态是否为待支付，否则不能支付
            if (guijingOrder.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                MallException.fail("非待支付状态不可支付");
            }
            guijingOrder.setOrderStatus((byte)MallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            guijingOrder.setPayType((byte) payType);
            guijingOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            guijingOrder.setPayTime(new Date());
            guijingOrder.setUpdateTime(new Date());
            if (guijingOrderMapper.updateOrderSelective(guijingOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public PageResult getMyOrder(PageQueryUtil pageQueryUtil) {
        /**
         * 只需要将获取的分页用分页类包装然后返回即可
         * 仅查看不需要加事务
         */
        List<GuijingOrder> guijingOrderList = guijingOrderMapper.findGuijingOrderList(pageQueryUtil);
        int total = guijingOrderMapper.getTotalGuijingOrder(pageQueryUtil);
        return new PageResult(guijingOrderList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }
    
    @Override
    public String cancelOrder(String orderNo, Long userId) {
        GuijingOrder guijingOrder = guijingOrderMapper.selectByOrderNo(orderNo);
        if (guijingOrder != null) {
            if (guijingOrderMapper.closeOrder(Collections.singletonList(guijingOrder.getOrderId()), MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        // 首先查看订单是否存在
        GuijingOrder guijingOrder = guijingOrderMapper.selectByOrderNo(orderNo);
        if (guijingOrder != null) {
            guijingOrder.setOrderStatus((byte)MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            guijingOrder.setUpdateTime(new Date());
            if (guijingOrderMapper.updateOrderSelective(guijingOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public PageResult getGuijingOrderPage(PageQueryUtil pageQueryUtil) {
        // 获取常规如total data
        int total = guijingOrderMapper.getTotalGuijingOrder(pageQueryUtil);
        List<GuijingOrder> guijingOrderList = guijingOrderMapper.findGuijingOrderList(pageQueryUtil);
        // 定义表现层VO对象
        List<MallOrderListVO> mallOrderListVOList = new ArrayList<>();
        if (total > 0) {
            // 实体类转化，将实体类对象转换为表现层对象
            mallOrderListVOList = BeanUtil.copyList(guijingOrderList, MallOrderListVO.class);
            // 获取订单id
            List<Long> orderIds = guijingOrderList.stream().map(GuijingOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                // 通过订单id获取orderItem列表
                List<GuijingOrderItem> guijingOrderItemList = guijingOrderItemMapper.getByOrderIds(orderIds);
                // 将orderItem列表通过订单id分类
                Map<Long, List<GuijingOrderItem>> guijingOrderItemMap = guijingOrderItemList.stream().collect(Collectors.groupingBy(GuijingOrderItem::getOrderId));
                // 获取订单和orderItem数据的绑定
                for (MallOrderListVO mallOrderListVO : mallOrderListVOList) {
                    if (guijingOrderItemMap.containsKey(mallOrderListVO.getOrderId())) {
                        // 存在订单项的时候，将订单项添加到订单中
                        List<GuijingOrderItem> guijingOrderItems = guijingOrderItemMap.get(mallOrderListVO.getOrderId());
                        // 转化为表现层对象
                        List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(guijingOrderItems, MallOrderItemVO.class);
                        mallOrderListVO.setMallOrderItemVOS(mallOrderItemVOS);
                    }
                }
            }
        }
        return new PageResult(mallOrderListVOList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    @Transactional
    public String updateOrderInfo(GuijingOrder guijingOrder) {
        /**
         * 用户修改信息的前提
         * 1 、 该订单要存在
         * 2 、 需要添加事务
         * 3 、 状态符合，如在出库前和ordeStatus>0
         */
        GuijingOrder temp = guijingOrderMapper.selectByPrimaryKey(guijingOrder.getOrderId());
        if (temp != null) {
            // 注意区分使用查询到的数据判断和用元数据判断的区别
            if (temp.getOrderStatus()>=0 && temp.getOrderStatus() < 3) {
                temp.setUserAddress(guijingOrder.getUserAddress());
                temp.setOrderStatus(guijingOrder.getOrderStatus());
                temp.setUpdateTime(new Date());
                // 如果修改成功就返回成功信息
                if (guijingOrderMapper.updateOrderSelective(temp) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                return "订单已出库,请联系商家修改信息。";
            }

        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDown(List<Long> ids) {
        // 订单出库操作
        /**
         * 首先查询是否有订单
         * 添加事务支持
         * 如果存在该订单，检查订单支付状态是否成功，如果支付成功，则修改订单状态到出货即可
         */
        List<GuijingOrder> guijingOrderList = guijingOrderMapper.getByPrimaryKeys(ids);
        if (!CollectionUtils.isEmpty(guijingOrderList)) {
            String errorOrder = "";
            for (GuijingOrder guijingOrder : guijingOrderList) {
                if(guijingOrder.getIsDeleted() == 1) {
                    // 说明此订单不存在
                    errorOrder += guijingOrder.getOrderNo() + " ";
                    continue;
                }
                if (guijingOrder.getOrderStatus() != 1) {
                    // 说明支付未完成，则不能出库
                    errorOrder += guijingOrder.getOrderNo() + " ";
                }
            }
            // 如果检查每个订单都存在，且均已支付，提交数据到数据库
            if (StringUtils.isEmpty(errorOrder)) {
                if (guijingOrderMapper.checkDone(ids) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                return "该列订单中存在订单不存在或者订单未完成支付状态的订单";
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(List<Long> ids) {
        /**
         * 订单出库，如果订单不是已支付或者已出货的怎不能执行出库功能
         * 需要添加事务支持
         */
        List<GuijingOrder> guijingOrderList = guijingOrderMapper.getByPrimaryKeys(ids);
        if (!CollectionUtils.isEmpty(guijingOrderList) ) {
            String errorOrderNo = "";
            for (GuijingOrder guijingOrder : guijingOrderList) {
                if (guijingOrder.getIsDeleted() == 1) {
                    // 订单不存在
                    errorOrderNo += guijingOrder.getOrderNo() + " ";
                    continue;
                }
                if (guijingOrder.getOrderStatus() != 1 || guijingOrder.getOrderStatus() != 2 ) {
                    errorOrderNo += guijingOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.isEmpty(errorOrderNo)){
                /**
                 * isEmpty 和 isBlank的区别。isBlank会将 “  ” 也判定为空
                 */

                if (guijingOrderMapper.checkOut(ids) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                return "此时订单不能完成出库，请检查订单支付情况或订单是否存在。";
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(List<Long> ids) {
        /**
         * 关闭订单
         * 事务支持
         * 订单状态未完成则不能关闭 订单已完成也不能关闭，
         */
        List<GuijingOrder> guijingOrderList = guijingOrderMapper.getByPrimaryKeys(ids);
        if (!CollectionUtils.isEmpty(guijingOrderList)) {
            String errorOrderNo = "";
            for (GuijingOrder guijingOrder : guijingOrderList) {
                if (guijingOrder.getIsDeleted() == 1){
                    // 订单已不存在
                    errorOrderNo += guijingOrder.getOrderNo() + " ";
                    continue;
                }
                if (guijingOrder.getOrderStatus() < 0 || guijingOrder.getOrderStatus() ==4) {
                    // 订单取消或者订单订单已完成则不能关闭
                    errorOrderNo += guijingOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNo)) {
                //
                if (guijingOrderMapper.closeOrder(ids, MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                return "你选择的订单不能执行关闭操作";
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<MallOrderItemVO> getOrderItems(Long id) {
        GuijingOrder guijingOrder = guijingOrderMapper.selectByPrimaryKey(id);
        if (guijingOrder != null) {
            List<GuijingOrderItem> guijingOrderItemList = guijingOrderItemMapper.getOrderItemByOrderId(id);
            if (!CollectionUtils.isEmpty(guijingOrderItemList)) {
                List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(guijingOrderItemList, MallOrderItemVO.class);
                return mallOrderItemVOS;
            }
        }
        return null;
    }

}
