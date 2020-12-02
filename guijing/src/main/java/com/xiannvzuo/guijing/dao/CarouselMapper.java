package com.xiannvzuo.guijing.dao;

import com.xiannvzuo.guijing.entity.Carousel;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public interface CarouselMapper {
    /**
     * 业务逻辑
     */
    /**
     * 考虑点：
     * 增删改查，着重不同的扩展
     * 增加：
     * 默认增加
     * 修改性的增加
     */
    // 通过主键删除
    int deleteByPrimaryKey(Integer carouselId);
    // 通过主键列表删除
    int deleteByPrimaryKeyList(List<Integer> carouselIdList);

    // 插入一条轮播图
    int insert(Carousel carousel);
    // 对轮播图属性有选择地插入一条轮播图
    int insertSelective(Carousel carousel);

    // 查询
    Carousel selectByPrimaryKey(Integer carouselId);

    // 以下两个我只考虑实现一个，因为可以公用
    // +++++++++++++++++++++++++
    // 通过主键更新
    int updateByPrimaryKey(Carousel carousel);
    // 更新部分消息
    int updateByPrimaryKeySelective(Carousel carousel);
    // ++++++++++++++++++++++++
    // 获取分页图
    List<Carousel> findCarouselList(PageQueryUtil pageQueryUtil);

    int getTotalCarousel(PageQueryUtil pageQueryUtil);

    // 删除id数组内所有id值的carousel
    int deleteBatch(Integer[] ids);

    // 查询前num个图片
    List<Carousel> findCarouselsByNum(@Param("number") Integer number);
}
