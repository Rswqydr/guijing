package com.xiannvzuo.guijing;

import com.jayway.jsonpath.internal.function.text.Concatenate;
import com.xiannvzuo.guijing.common.Constants;
import com.xiannvzuo.guijing.common.MallCategoryLevelEnum;
import com.xiannvzuo.guijing.controller.vo.GuijingIndexCategoryVO;
import com.xiannvzuo.guijing.controller.vo.SecondLevelCategoryVO;
import com.xiannvzuo.guijing.controller.vo.ThirdLevelCategoryVO;
import com.xiannvzuo.guijing.dao.GoodsCategoryMapper;
import com.xiannvzuo.guijing.entity.GoodsCategory;
import com.xiannvzuo.guijing.service.GoodsCategoryService;
import com.xiannvzuo.guijing.util.BeanUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class GuijingApplicationTests {
	private static final Logger LOG = LoggerFactory.getLogger(GuijingApplicationTests.class);
	@Autowired
	private GoodsCategoryService goodsCategoryService;
	@Autowired
	private GoodsCategoryMapper goodsCategoryMapper;
	@Test
	void contextLoads() throws SQLException {
		System.out.println(Collections.singletonList(0L).toString());
		List<GoodsCategory> goodsCategoryList = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(1, Collections.singletonList(0L), 0);
		System.out.println(goodsCategoryList.toString());
	}

	@Test
	void test2() {
		List<GoodsCategory> firstGoodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber( MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Collections.singletonList(0L), Constants.INDEX_CATEGORY_NUMBER);
		List<Long> firstLevelCategoryIds = firstGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
		System.out.println(firstGoodsCategories);
		System.out.println(firstLevelCategoryIds.toString());
		List<GoodsCategory> secondGoodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), firstLevelCategoryIds, 0);
		System.out.println(secondGoodsCategories);
		List<Long> secondLevelGategoryIds = secondGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
		List<GoodsCategory> thirdGoodsGategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), secondLevelGategoryIds, 0);
		System.out.println(thirdGoodsGategories);
		Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdGoodsGategories.stream().collect(Collectors.groupingBy(GoodsCategory::getParentId));

		System.out.println(thirdLevelCategoryMap);
	}

	@Test
	void test3 () {

		// 表现层：第一级层表现对象
		List<GuijingIndexCategoryVO> guijingIndexCategoryVOS =new ArrayList<>();
		// 获取一级分类的所有category对象
		List<GoodsCategory> firstGoodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber( MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Collections.singletonList(0L),Constants.INDEX_CATEGORY_NUMBER);

		if (!CollectionUtils.isEmpty(firstGoodsCategories)) {
			// 将所有找到的第一层级的id组成List<Long>集合
			List<Long> firstLevelCategoryIds = firstGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
			List<GoodsCategory> secondGoodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(MallCategoryLevelEnum.LEVEL_TWO.getLevel(), firstLevelCategoryIds, 0);
			if (!CollectionUtils.isEmpty(secondGoodsCategories)) {

				List<Long> secondLevelGategoryIds = secondGoodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());

				// 获取第三层的所有数据
				List<GoodsCategory> thirdGoodsGategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(MallCategoryLevelEnum.LEVEL_THREE.getLevel(), secondLevelGategoryIds, 0);
				if (!CollectionUtils.isEmpty(thirdGoodsGategories)) {
					// 如果三层都有数据的话，现在将所有拿出来的数据进行分组，然后装进不同的组中， 根据parentId

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

			System.out.println(guijingIndexCategoryVOS);

		}
	}

	@Test
	public void test() {
		System.out.println("simpleName======"+ getClass().getSimpleName());
	}

}
