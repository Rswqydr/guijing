package com.xiannvzuo.guijing.dao;

import com.xiannvzuo.guijing.entity.GuijingGoods;
import com.xiannvzuo.guijing.entity.StockNumDTO;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GuijingGoodsMapper {
    /**
     *常规dao层接口如示：
     */
    /**
     * 查找相关：
     * 通过主键查找
     * 通过商品名称查找一系列商品
     * 通过一组主键ID值查找
     * 分页查找  find相关的，传入的是Map的pageUtils
     *      与分页相关的，查找商品总数
     *      与搜索相关的，查找搜索到的所有商品总数
     *
     */
    GuijingGoods selectByPrimaryKey(Long goodsId);
    List<GuijingGoods> selectByBatchPrimaryKeys(List<Long> goodIds);
    List<GuijingGoods> findGuijingGoodsList(PageQueryUtil pageQueryUtil);
    List<GuijingGoods> findGuijingGoodsBySearch(PageQueryUtil pageQueryUtil);
    int getGTotalGuijingGoods(PageQueryUtil pageQueryUtil);
    int getTotalGuijingGoodsBySearch(PageQueryUtil pageQueryUtil);
    /**
     * 删除更新操作：
     * 通过主键删除
     * 通过主键更新
     * 通过主键有选择地更新
     * updateByPrimaryKeyWithBLOBs(MallGoods record);
     * 通过一组主键id值更新某一个一组值 :
     *      有两种，一种是新建一个仅包含主键id和要更新数据的实体类，每一个实体的状态可以不同《同以下的区别》
     *      另一种方式是是通过主键数组，然后对这些做相同的操作。
     */
    int deleteByPrimaryKey(Long goodsId);
    int updateByPrimaryKey(GuijingGoods guijingGoods);
    int updateByPrimarySelective(GuijingGoods guijingGoods);
    //  *
    int updateByPrimaryKeyWithBLOBs(GuijingGoods guijingGoods);
    // 更新库存
    /*
    !!!!!!!!!!!!!!!!!!!!!!!此处考虑是否使用@Param，同数组对比!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    int updateStockNum(List<StockNumDTO> stockNumDTOS);
    // 更新售卖状态
    int batchUpdateSellingStatus(@Param("orderIds") Long[] orderIds,  @Param("sellStatus") int sellStatus);

    /**
     * 插入一整条数据
     * 插入有选择性地插入
     */
    int insert(GuijingGoods guijingGoods);
    int insertSelective(GuijingGoods guijingGoods);
    int batchInsertGuijingGoods(List<GuijingGoods> guijingGoods);


}
