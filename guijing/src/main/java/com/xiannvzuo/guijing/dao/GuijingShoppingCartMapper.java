package com.xiannvzuo.guijing.dao;

import com.xiannvzuo.guijing.entity.GuijingShoppingCart;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface GuijingShoppingCartMapper {
    // 添加购物车
    /*int insert(GuijingShoppingCart guijingShoppingCart);
*/
    int insertSelective(GuijingShoppingCart guijingShoppingCart);

    // 删除某一样购物
    int deleteByPrimaryKey(Long cartItemId);

    //分页战术
    // List<GuijingShoppingCart> fingAllShoppingCartList(PageQueryUtil pageQueryUtil);
    // int totalShoppingCart(PageQueryUtil pageQueryUtil);

    // 修改某一项购物
    //int updateShoppingCart(GuijingShoppingCart guijingShoppingCart);
    int updateByPrimaryKeySelective(GuijingShoppingCart guijingShoppingCart);

    // 通过id找
    int selectCountByUserId(Long userId);

    GuijingShoppingCart selectByprimaryKey(Long cartItemId);

    GuijingShoppingCart selectByUserIdAndGoodsId(@Param("userId")Long userId, @Param("goodsId")Long goodsId);

    List<GuijingShoppingCart> selectByUserId(Long userId);

    List<GuijingShoppingCart> selectByUserIdAndCartIds(@Param("userId")Long userId, @Param("cartIds")List<Long> cartId);

    int batchDeleteByIds(List<Long> ids);

}
