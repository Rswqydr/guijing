package com.xiannvzuo.guijing.service;

import com.xiannvzuo.guijing.controller.vo.GuijingIndexCarouselVO;
import com.xiannvzuo.guijing.entity.Carousel;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;

import java.util.List;

public interface GuijingCarouselService {

    // 获取分页结果
    PageResult getCarouselPage(PageQueryUtil pageQueryUtil);

    // 保存轮播图
    String saveCarousel(Carousel carousel);

    //更新轮播图数据
    String updateCarousel(Carousel carousel);

    // 通过id获取轮播图
    Carousel getCarouselById(Integer id);

    // 删除一组轮播图
    Boolean deleteBatch(Integer[] ids);


    List<GuijingIndexCarouselVO> getCarouselsForIndex(int number);

}
