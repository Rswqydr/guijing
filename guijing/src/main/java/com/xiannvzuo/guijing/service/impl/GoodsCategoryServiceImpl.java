package com.xiannvzuo.guijing.service.impl;

import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.MallCategoryLevelEnum;
import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.*;
import com.xiannvzuo.guijing.dao.GoodsCategoryMapper;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.service.GoodsCategoryService;
import com.xiannvzuo.guijing.util.BeanUtil;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import java.util.stream.Collectors;

@Service
public class GoodsCategoryServiceImpl implements GoodsCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(GoodsCategoryServiceImpl.class);
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    // 返回的是分页数据，结果用page工具类包装
    @Override
    public PageResult getCategoriesPage(PageQueryUtil pageQueryUtil) {
        LOG.info("getCategoriesPage");
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageQueryUtil);
        LOG.info("List<GoodsCategory>" +goodsCategories.toString());
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageQueryUtil);
        return new PageResult(goodsCategories, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    public String updateCategory(GoodsCategory goodsCategory) {
        // 首先查询是否又该条数据
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if ( temp == null ) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        // 获取的是同一层集下，该名称的类别，如果发现同层级下名称相同，但是id不同，则不能进行修改，因为这样
        // 的化同一层就有两个名字一样却id不同的分类，这是不允许的
        if (temp2 != null && !temp2.getCategoryName().equals(goodsCategory.getCategoryName())) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryMapper.update(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getCaategoryById(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteCategoriesBatch(Integer[] ids) {
        // 可以提前判断的一定提前判断
        LOG.info("进入deleteCategoriesBatch");
        if (ids.length < 1) {
            return false;
        }
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<GoodsCategory> getCategoriesByLevelAndParentId(int categoryLevel, List<Long> parentIds) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(categoryLevel, parentIds, 0);
    }

    /**
     * 此处疑似有问题：
     * 检查思路整理：
     * 首先查看第一层层，如果不为空，因为第一层id为第二分层的parentid，所以依据此找出第二分层的全部，在由此找出第三分层的全部
     * 然后从后往前，以第三层开始，建立第三层的map关系，该关系映射为：第三层的parentid: 第三层的id
     * 同样的，往回传播，建立第二层的映射关系, 该映射关系为：第二层的parentId： 第二层的id，
     * 由于parentId指示的是上一层，这样可以建立本层与上一层的映射关系
     * 根据此检查：-------------------------------------------------标准日期：2020.11.24
     * 检查未发现异常：新疑似问题：
     * 由于实体对象、数据传输对象、表现层对象的toString方法格式不对，引起的前端页面解析错误！！！
     * @return
     */
    @Override
    public List<GuijingIndexCategoryVO> getCategoryByIndex() {
        LOG.info("进入获取首页分层项目组");
        // 表现层：第一级层表现对象
        List<GuijingIndexCategoryVO> guijingIndexCategoryVOS =new ArrayList<>();
        // 获取一级分类的所有category对象
        List<GoodsCategory> firstGoodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber( MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Collections.singletonList(0L),Constants.INDEX_CATEGORY_NUMBER);
        LOG.info("获取一级分类成功：");
        LOG.info(firstGoodsCategories.toString());
        if (!CollectionUtils.isEmpty(firstGoodsCategories)) {
            // 将所有找到的第一层级的id组成List<Long>集合
            List<Long> firstLevelCategoryIds = firstGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            List<GoodsCategory> secondGoodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), firstLevelCategoryIds, 0);
            if (!CollectionUtils.isEmpty(secondGoodsCategories)) {
                LOG.info("获取第二层id通过：stream-map");
                List<Long> secondLevelGategoryIds = secondGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                LOG.info("正在获取第三层数据");
                // 获取第三层的所有数据
                List<GoodsCategory> thirdGoodsGategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), secondLevelGategoryIds, 0);
                if (!CollectionUtils.isEmpty(thirdGoodsGategories)) {
                    // 如果三层都有数据的话，现在将所有拿出来的数据进行分组，然后装进不同的组中， 根据parentId
                    LOG.info("正在获取第三层分组");
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdGoodsGategories.stream().collect(Collectors.groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS =new ArrayList<>();
                    // 处理二级分类
                    for (GoodsCategory secondGoodsCategory : secondGoodsCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondGoodsCategory, secondLevelCategoryVO);
                        // 如果二层下有数据则放入secondLevelCategoryVOS对象中
                        if (thirdLevelCategoryMap.containsKey(secondGoodsCategory.getCategoryId())) {
                            // 根据第二层分类的id取出第三层的list
                            List<GoodsCategory> tempGoodCategories = thirdLevelCategoryMap.get(secondGoodsCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(BeanUtil.copyList(tempGoodCategories, ThirdLevelCategoryVO.class));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    // 处理第一层分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        // 根据parentId将第二层分类
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryMap = secondLevelCategoryVOS.stream().collect(Collectors.groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstGoodsCategory : firstGoodsCategories) {
                            GuijingIndexCategoryVO guijingIndexCarouselVO = new GuijingIndexCategoryVO();
                            BeanUtil.copyProperties(firstGoodsCategory, guijingIndexCarouselVO);
                            if (secondLevelCategoryMap.containsKey(firstGoodsCategory.getCategoryId())) {
                                List<SecondLevelCategoryVO> tempSecondLevelCategoryVO = secondLevelCategoryMap.get(firstGoodsCategory.getCategoryId());
                                guijingIndexCarouselVO.setSecondLevelCategoryVOS(tempSecondLevelCategoryVO);
                                guijingIndexCategoryVOS.add(guijingIndexCarouselVO);
                            }
                        }
                    }
                }
            }
            LOG.info(guijingIndexCategoryVOS.toString());
            System.out.println(guijingIndexCategoryVOS);
            return guijingIndexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public SearchPageCategoryVO getCategoriesForeach(Long id) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        GoodsCategory thirdLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(id);
        if (thirdLevelGoodsCategory != null && thirdLevelGoodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            GoodsCategory secondLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
                List<GoodsCategory> thirdGoodCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), Collections.singletonList(secondLevelGoodsCategory.getCategoryId()), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryVO.setCurrentCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdGoodCategories);
                searchPageCategoryVO.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                /*
                GoodsCategory firstLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(secondLevelGoodsCategory.getParentId());
                */
                return searchPageCategoryVO;
            }
        }
        return null;
    }
}
