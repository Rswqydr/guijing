package com.xiannvzuo.guijing.service.impl;

import com.xiannvzuo.guijing.common.ServiceResultEnum;
import com.xiannvzuo.guijing.controller.vo.MallSearchGoodsVO;
import com.xiannvzuo.guijing.dao.GuijingGoodsMapper;
import com.xiannvzuo.guijing.entity.Carousel;
import com.xiannvzuo.guijing.entity.GuijingGoods;
import com.xiannvzuo.guijing.service.GuijingCarouselService;
import com.xiannvzuo.guijing.service.GuijingGoodsService;
import com.xiannvzuo.guijing.util.BeanUtil;
import com.xiannvzuo.guijing.util.PageQueryUtil;
import com.xiannvzuo.guijing.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.pattern.PathPattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class GuijingGoodsServiceImpl implements GuijingGoodsService {

    private static final Logger LOG = LoggerFactory.getLogger(GuijingGoodsServiceImpl.class);
    @Autowired
    private GuijingGoodsMapper guijingGoodsMapper;

    @Override
    public GuijingGoods getGuijingGoods(Long id) {
        return guijingGoodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageResult getGuijingGoodsPage(PageQueryUtil pageQueryUtil) {
        // 分页常规包装成PageResult的步骤
        LOG.info("--进入getGuijingGoodsPage--");
        List<GuijingGoods> guijingGoodsList = guijingGoodsMapper.findGuijingGoodsList(pageQueryUtil);
        LOG.info(guijingGoodsList.toString());
        int total = guijingGoodsMapper.getGTotalGuijingGoods(pageQueryUtil);
        return new PageResult(guijingGoodsList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    public String saveGuijingGoods(GuijingGoods guijingGoods) {
        if (guijingGoodsMapper.insertSelective(guijingGoods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveGuijingGoods(List<GuijingGoods> guijingGoodsList) {
        if (!CollectionUtils.isEmpty(guijingGoodsList)) {
            guijingGoodsMapper.batchInsertGuijingGoods(guijingGoodsList);
        }
    }

    @Override
    public String updateGuijingGoods(GuijingGoods guijingGoods) {
        // 更新前一定要检测待更新目标是否存在
        if (guijingGoodsMapper.selectByPrimaryKey(guijingGoods.getGoodsId()) == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (guijingGoodsMapper.updateByPrimarySelective(guijingGoods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return ServiceResultEnum.DB_ERROR.getResult();
        }
    }

    @Override
    public Boolean batchUpdateGuijingGoodsStatus(Long[] goodsIds, int sellStatus) {
        /*if (goodsIds.length < 1) {
            return false;
        }
        if (guijingGoodsMapper.batchUpdateSellingStatus(goodsIds, sellStatus) > 0) {
            return true;
        } else {
            return false;
        }*/
        return guijingGoodsMapper.batchUpdateSellingStatus(goodsIds, sellStatus) > 0;
    }

    @Override
    public PageResult searchGuijingGoods(PageQueryUtil pageQueryUtil) {
        // 提供给前端页面展示的部分
        // 常规步骤
        // 首先还是获取page的列表
        List<GuijingGoods> guijingGoods = guijingGoodsMapper.findGuijingGoodsBySearch(pageQueryUtil);
        int total = guijingGoodsMapper.getGTotalGuijingGoods(pageQueryUtil);
        // 获取前端的VO对象
        List<MallSearchGoodsVO> mallSearchGoodsVOS = new ArrayList<>();
        // 如果获取到的商品实体类不为空
        if (!CollectionUtils.isEmpty(guijingGoods)) {
            // 将实体类对象转化为VO对象  使用BeanUtil, 主要演习其具体实现
            // 其他情况下可以返回改VO即可，但是此处因为商品描述信息过大，需要对VO对象内部字段做出一些处理
            mallSearchGoodsVOS = BeanUtil.copyList(guijingGoods, MallSearchGoodsVO.class);
            for (MallSearchGoodsVO mallSearchGoodsVO : mallSearchGoodsVOS) {
                String goodsName = mallSearchGoodsVO.getGoodsName();
                String goodsIntro = mallSearchGoodsVO.getGoodsIntro();
                if(goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "....";
                    mallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    mallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        // 包装成PageResult返回
        return new PageResult(mallSearchGoodsVOS, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }
}
