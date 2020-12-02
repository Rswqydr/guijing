package com.xiannvzuo.guijing.dao;

import com.sun.tracing.dtrace.ProviderAttributes;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GoodsCategoryMapper {
    /**
     * 插入一条分类类目
     * 非完备信息插入
     */
    int insertSelective(GoodsCategory goodsCategory);

    /**
     * 修改一条分类类目， 依据该分类的id
     *
     */
    int update(GoodsCategory goodsCategory);

    /**
     * 查看一条分类的类目，依据该类目id
     * 查看，通过类目名、和自己的level层级 0 1 2
     */
    GoodsCategory selectByPrimaryKey(Long categoryId);
    GoodsCategory selectByLevelAndName(@Param("categoryLevel") Byte cateLevel, @Param("categoryName") String name);
    /**
     * 通过分页查找
     * 获取类目总数
     */
    List<GoodsCategory> findGoodsCategoryList(PageQueryUtil pageQueryUtil);

    /**
     * 注意 pageQueryUtil继承了linkedHashMap,所以是map类型，它里面不仅存放分页的数据，
     * 也会存放前端传来的和分页有关的数据，依据此来查询，比如果某一个类目的总数等
     * @param pageQueryUtil
     * @return
     */
    int getTotalGoodsCategories(PageQueryUtil pageQueryUtil);

    /**
     * 删除一组：batch
     */
    int deleteBatch(Integer[] ids);
    /**
     * 通过层级level、父级类目id组，要查询的数量
     * ！关联查询
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("categoryLevel")int level, @Param("parentIds")List<Long> parentIds, @Param("number")int number);


}
