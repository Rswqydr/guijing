package com.xiannvzuo.guijing.service.impl;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.MallException;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.MallShoppingCartItemVO;
import com.xiannvzuo.guijing.dao.GuijingGoodsMapper;
import com.xiannvzuo.guijing.dao.GuijingShoppingCartMapper;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.entity.GuijingGoods;
import com.xiannvzuo.guijing.entity.GuijingShoppingCart;
import com.xiannvzuo.guijing.service.GuijingShoppingCartService;
import com.xiannvzuo.guijing.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuijingShoppingCartServiceImpl implements GuijingShoppingCartService {
    @Autowired
    private GuijingShoppingCartMapper guijingShoppingCartMapper;
    @Autowired
    private GuijingGoodsMapper guijingGoodsMapper;

    @Override
    public String saveGoodsToCart(GuijingShoppingCart guijingShoppingCart) {
        /**
         * 添加购物车略微复杂点：当添加的时候，如果检测到购物车中有原来的产品，返回更新后的购物车
         * 如果购物车不存在
         *检车购物车中商品存不存在，如果商品不存在，则返回商品不存在
         * 如果商品数大于单个商品数目，返回限制条件信息
         * 如果商品总数大于购物车限制数目，返回该提示消息
         */
        GuijingShoppingCart temp = guijingShoppingCartMapper.selectByUserIdAndGoodsId(guijingShoppingCart.getUserId(), guijingShoppingCart.getGoodsId());
        if (temp != null) {
            guijingShoppingCart.setGoodsCount(temp.getGoodsCount() + guijingShoppingCart.getGoodsCount());
            return updateGuijingShoppingCart(guijingShoppingCart);
        }
        GuijingGoods guijingGoods = guijingGoodsMapper.selectByPrimaryKey(guijingShoppingCart.getGoodsId());
        if (guijingGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int total = guijingShoppingCartMapper.selectCountByUserId(guijingShoppingCart.getUserId());
        if (guijingShoppingCart.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        if (total > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        // 验证信息判断完毕后，开始保存
        if(guijingShoppingCartMapper.insertSelective(guijingShoppingCart) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
    }

    @Override
    public String updateGuijingShoppingCart(GuijingShoppingCart guijingShoppingCart) {
        // 修改购物车信息
        /**
         * 先判断该购物车是否存在，不存在则创建购物车? 当然不是，不存在则返回错误
         * 然后判断修改后的值是否大于该商品限购量
         * 相同数量不会尽心修改
         * userID不同不能修改
         */
        GuijingShoppingCart temp = guijingShoppingCartMapper.selectByUserIdAndGoodsId(guijingShoppingCart.getUserId(), guijingShoppingCart.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if(guijingShoppingCart.getGoodsCount() > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        temp.setGoodsCount(guijingShoppingCart.getGoodsCount()+temp.getGoodsCount());
        temp.setUpdateTime(new Date());
        if (guijingShoppingCartMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
    }

    @Override
    public GuijingShoppingCart getShoppingCartById(Long id) {
        GuijingShoppingCart guijingShoppingCart = guijingShoppingCartMapper.selectByprimaryKey(id);
        if (guijingShoppingCart != null) {
            return guijingShoppingCart;
        }
        return null;
    }

    @Override
    public boolean deleteGoodsById(Long id) {
        if (guijingShoppingCartMapper.deleteByPrimaryKey(id) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<MallShoppingCartItemVO> getShoppingCartItems(Long userID) {
        /**
         * 获取一组用户的商品信息
         */
        // 创建表现层对象VO
        List<MallShoppingCartItemVO> mallShoppingCartItemVOList = new ArrayList<>();
        // 找出该id列表下的所有购物车订单
        List<GuijingShoppingCart> guijingShoppingCartList = guijingShoppingCartMapper.selectByUserId(userID);
        if (!CollectionUtils.isEmpty(guijingShoppingCartList)) {
            // 根据goodsId,获取商品信息
            List<Long> goodsIds = guijingShoppingCartList.stream().map(GuijingShoppingCart::getGoodsId).collect(Collectors.toList());
            // 根据id获取商品
            List<GuijingGoods> guijingGoodsList = guijingGoodsMapper.selectByBatchPrimaryKeys(goodsIds);
            Map<Long, GuijingGoods> guijingGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(guijingGoodsList)) {
                // 如果商品不为空，为了更好地处理，将商品转换为gooodsId : GuijingGoods映射格式
                // 注意这种toMap的转换格式，抛异常的原因等
                guijingGoodsMap = guijingGoodsList.stream().collect(Collectors.toMap(GuijingGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
                for (GuijingShoppingCart guijingShoppingCart : guijingShoppingCartList) {
                    MallShoppingCartItemVO mallShoppingCartItemVO = new MallShoppingCartItemVO();
                    BeanUtil.copyProperties(guijingShoppingCart, mallShoppingCartItemVO);
                    if (guijingGoodsMap.containsKey(guijingShoppingCart.getGoodsId())) {
                        GuijingGoods temp = guijingGoodsMapper.selectByPrimaryKey(guijingShoppingCart.getGoodsId());
                        mallShoppingCartItemVO.setGoodsCoverImg(temp.getGoodsCoverImg());
                        mallShoppingCartItemVO.setSellingPrice(temp.getSellingPrice());
                        String name  = temp.getGoodsName();
                        if (temp.getGoodsName().length() > 28) {
                            name = name.substring(0, 28) + "...";
                        }
                        mallShoppingCartItemVO.setGoodsName(name);
                        mallShoppingCartItemVOList.add(mallShoppingCartItemVO);
                    }
                }
            }
        }

        return mallShoppingCartItemVOList;
    }

    @Override
    public List<MallShoppingCartItemVO> getCartByUserIdsAndCartId(List<Long> cartId, Long userId) {
        List<MallShoppingCartItemVO> mallShoppingCartItemVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(cartId)) {
            MallException.fail("购物车不能为空");
        }
        List<GuijingShoppingCart> guijingShoppingCartList = guijingShoppingCartMapper.selectByUserIdAndCartIds(userId, cartId);
        if (!CollectionUtils.isEmpty(guijingShoppingCartList)) {
            MallException.fail("购物车不能为空");
        }
        if (guijingShoppingCartList.size() != cartId.size()) {
            MallException.fail("参数异常");
        }
        if (!CollectionUtils.isEmpty(guijingShoppingCartList)) {
            // 根据goodsId,获取商品信息
            List<Long> goodsIds = guijingShoppingCartList.stream().map(GuijingShoppingCart::getGoodsId).collect(Collectors.toList());
            // 根据id获取商品
            List<GuijingGoods> guijingGoodsList = guijingGoodsMapper.selectByBatchPrimaryKeys(goodsIds);
            Map<Long, GuijingGoods> guijingGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(guijingGoodsList)) {
                // 如果商品不为空，为了更好地处理，将商品转换为gooodsId : GuijingGoods映射格式
                // 注意这种toMap的转换格式，抛异常的原因等
                guijingGoodsMap = guijingGoodsList.stream().collect(Collectors.toMap(GuijingGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
                for (GuijingShoppingCart guijingShoppingCart : guijingShoppingCartList) {
                    MallShoppingCartItemVO mallShoppingCartItemVO = new MallShoppingCartItemVO();
                    BeanUtil.copyProperties(guijingShoppingCart, mallShoppingCartItemVO);
                    if (guijingGoodsMap.containsKey(guijingShoppingCart.getGoodsId())) {
                        GuijingGoods temp = guijingGoodsMapper.selectByPrimaryKey(guijingShoppingCart.getGoodsId());
                        mallShoppingCartItemVO.setGoodsCoverImg(temp.getGoodsCoverImg());
                        mallShoppingCartItemVO.setSellingPrice(temp.getSellingPrice());
                        String name  = temp.getGoodsName();
                        if (temp.getGoodsName().length() > 28) {
                            name = name.substring(0, 28) + "...";
                        }
                        mallShoppingCartItemVO.setGoodsName(name);
                        mallShoppingCartItemVOList.add(mallShoppingCartItemVO);
                    }
                }
            }
        }
        return mallShoppingCartItemVOList;
    }

    /**
     * 建议封装这样一个方法，它可以把商品信息和购物出信息结合起来，然后封装表现层对象返回给前端页面。
     * 应该是必要，因为同前端页面的交互都需要该表现层信息。
     */
}
