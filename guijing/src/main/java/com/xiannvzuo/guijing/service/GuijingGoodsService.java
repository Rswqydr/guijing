package com.xiannvzuo.guijing.service;

import com.xiannvzuo.guijing.entity.GuijingGoods;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;

import java.util.List;


public interface GuijingGoodsService {


    /**
     * 获取商品详细信息
     */
    GuijingGoods getGuijingGoods(Long id);

    /**
     * 后台获取分页信息
     */
    PageResult getGuijingGoodsPage(PageQueryUtil pageQueryUtil);

    /**
     * 添加商品信息
     */
    String saveGuijingGoods(GuijingGoods guijingGoods);

    /**
     * 批量添加商品信息
     */
    void batchSaveGuijingGoods(List<GuijingGoods> guijingGoodsList);

    /**
     * 修改商品信息
     */
    String updateGuijingGoods(GuijingGoods guijingGoods);

    /**
     * 批量更改商品状态
     */
    Boolean batchUpdateGuijingGoodsStatus(Long[] goodsIds, int sellStatus);

    /**
     * 商品搜索
     */
    PageResult searchGuijingGoods(PageQueryUtil pageQueryUtil);
}
