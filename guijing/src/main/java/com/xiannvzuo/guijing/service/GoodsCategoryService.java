package com.xiannvzuo.guijing.service;

import com.xiannvzuo.guijing.controller.vo.GuijingIndexCategoryVO;
import com.xiannvzuo.guijing.controller.vo.SearchPageCategoryVO;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;
import org.springframework.stereotype.Component;

import java.util.List;



public interface GoodsCategoryService {
    /**
     * 分页
     */
    PageResult getCategoriesPage(PageQueryUtil pageQueryUtil);
    /**
     * 修改
     */
    String updateCategory(GoodsCategory goodsCategory);
    /**
     * 添加
     */
    String saveCategory(GoodsCategory goodsCategory);
    /**
     * 获取某一个分类通过类目id
     */
    GoodsCategory getCaategoryById(Long id);
    /**
     * 删除类目组
     */
    Boolean deleteCategoriesBatch(Integer[] ids);
    /**
     * 根据层级和父类目的id获取类目
     */
    List<GoodsCategory> getCategoriesByLevelAndParentId(int categoryLevel, List<Long> parentIds);
    /**
     * 返回分页数据，首页用
     */
    List<GuijingIndexCategoryVO> getCategoryByIndex();

    /**
     * 返回分页数据，搜索用
     */
    SearchPageCategoryVO getCategoriesForeach(Long id);
}
