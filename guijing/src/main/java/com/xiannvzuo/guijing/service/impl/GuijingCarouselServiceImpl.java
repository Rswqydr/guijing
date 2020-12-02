package com.xiannvzuo.guijing.service.impl;

import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.GuijingIndexCarouselVO;
import com.xiannvzuo.guijing.dao.CarouselMapper;
import com.xiannvzuo.guijing.entity.Carousel;
import com.xiannvzuo.guijing.service.GuijingCarouselService;
import com.xiannvzuo.guijing.util.BeanUtil;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuijingCarouselServiceImpl implements GuijingCarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public PageResult getCarouselPage(PageQueryUtil pageQueryUtil) {
        List<Carousel> carousels = carouselMapper.findCarouselList(pageQueryUtil);
        int total = carouselMapper.getTotalCarousel(pageQueryUtil);
        return new PageResult(carousels, total,pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    public String saveCarousel(Carousel carousel) {
        // 插入一条轮播图， 直接插入即可
        if (carouselMapper.insertSelective(carousel) > 0 ) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        // 在修改之前先判断是否存在该轮播图id
        Carousel temp = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        // 如果未查询到数据，则数据不存在
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (carouselMapper.updateByPrimaryKeySelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        return carouselMapper.deleteBatch(ids) > 0;
    }


    public List<GuijingIndexCarouselVO> getCarouselsForIndex(int number) {
        //
        List<GuijingIndexCarouselVO> guijingIndexCarouselVOS = new ArrayList<>();
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        // 如果查询到的Carousel不为空，则将所有的carousel类型转化为GuijingIndexCarouselVO 的 VO类型
        if (!CollectionUtils.isEmpty(carousels)){
            guijingIndexCarouselVOS = BeanUtil.copyList(carousels, GuijingIndexCarouselVO.class);
        }
        return guijingIndexCarouselVOS;
    }

}
